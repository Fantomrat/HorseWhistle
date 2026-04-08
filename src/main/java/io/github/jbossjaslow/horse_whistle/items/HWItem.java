package io.github.jbossjaslow.horse_whistle.items;

import io.github.jbossjaslow.horse_whistle.config.HorseWhistleConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class HWItem extends Item {

    private static final int ITEM_COOLDOWN = HorseWhistleConfig.INSTANCE.itemCooldown();

    public HWItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player player, @NonNull InteractionHand hand) {
        super.use(world, player, hand);

        ItemStack stack = player.getItemInHand(hand);

        if (player.level().isClientSide()) return InteractionResult.FAIL;

        if (player.getPose() == Pose.CROUCHING) {
            if (stack.getComponents().has(HWComponents.ATTUNED_HORSE.get())) {
                var component = stack.get(HWComponents.ATTUNED_HORSE.get());

                assert component != null;
                String horseName = component.horseName();

                player.sendOverlayMessage(Component.translatable("text.item.horse_whistle.remove_attunement", horseName));

                stack.remove(HWComponents.ATTUNED_HORSE.get());

                world.playSound(
                        null,
                        player.blockPosition(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.MASTER,
                        0.5f,
                        0.5f
                );
                return InteractionResult.CONSUME;
            } else return InteractionResult.PASS;
        }

        if (stack.getComponents().has(HWComponents.ATTUNED_HORSE.get())) {
            player.getCooldowns().addCooldown(stack, ITEM_COOLDOWN);

            stack.hurtAndBreak(1, player, hand);

            var component = stack.get(HWComponents.ATTUNED_HORSE.get());
            assert component != null;
            String horseId = component.horseId();
            String horseName = component.horseName();

            double radius = HorseWhistleConfig.INSTANCE.searchRadius();
            AABB searchArea = new AABB(
                    player.getX() - radius,
                    player.getY() - radius,
                    player.getZ() - radius,
                    player.getX() + radius,
                    player.getY() + radius,
                    player.getZ() + radius
            );

            List<Horse> horses = world.getEntities(
                    EntityType.HORSE,
                    searchArea,
                    EntitySelector.LIVING_ENTITY_STILL_ALIVE
            );

            for (Horse horse : horses) {
                if (horse.getStringUUID().equals(horseId)) {
                    teleportHorse(horse, player, world);
                    return InteractionResult.CONSUME;
                }
            }

            player.sendOverlayMessage (Component.translatable("text.item.horse_whistle.could_not_find_horse", horseName));

        }

        return InteractionResult.FAIL;
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(
            @NonNull ItemStack stack, @NonNull Player player,
            @NonNull LivingEntity entity, @NonNull InteractionHand hand
    ) {
        super.interactLivingEntity(stack, player, entity, hand);
        player.getCooldowns().addCooldown(stack, ITEM_COOLDOWN);

        if (player.level().isClientSide()) return InteractionResult.FAIL;

        if (entity.getType() != EntityType.HORSE || stack.getComponents().has(HWComponents.ATTUNED_HORSE.get()))
            return InteractionResult.PASS;

        Horse horseEntity = (Horse) entity;

        if (horseEntity.isTamed()) {
            if (horseEntity.getOwner() == null || horseEntity.getOwner().getUUID() != player.getUUID()) {
                player.sendOverlayMessage(Component.translatable("text.item.horse_whistle.not_owner"));
                return InteractionResult.CONSUME;
            }



            player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.MASTER,
                    0.5f,
                    0.5f);

            String horseName = horseEntity.hasCustomName()
                    ? horseEntity.getCustomName().getString()
                    : horseEntity.getName().getString();

            String horseId = horseEntity.getStringUUID();

            stack.set(
                    HWComponents.ATTUNED_HORSE.get(),
                    new AttunedHorseComponent(horseId, horseName)
            );

            player.sendOverlayMessage (Component.translatable("text.item.horse_whistle.add_attunement", horseName));
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || stack.getComponents().has(HWComponents.ATTUNED_HORSE.get());
    }

    private void teleportHorse(Horse horse, Player player, Level world) {
        int radius = HorseWhistleConfig.INSTANCE.teleportRadius();

        BlockPos center = player.blockPosition();
        RandomSource random = horse.getRandom();

        List<BlockPos> validPositions = new ArrayList<>();

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    BlockPos pos = center.offset(dx, dy, dz);

                    if (isValidTeleportPosition(horse, world, pos)) {
                        validPositions.add(pos);
                    }
                }
            }
        }

        if (!validPositions.isEmpty()) {
            for (int i = 0; i < validPositions.size(); i++) {
                BlockPos chosen = validPositions.get(random.nextInt(validPositions.size()));

                double newX = chosen.getX() + 0.5;
                double newY = chosen.getY() + 0.5;
                double newZ = chosen.getZ() + 0.5;

                if (horse.randomTeleport(newX, newY, newZ, false)) {
                    world.playSound(null, player.blockPosition(),
                            SoundEvents.CHORUS_FRUIT_TELEPORT,
                            SoundSource.MASTER, 1.0F, 1.0F);
                    return;
                }
            }
            player.sendOverlayMessage (Component.translatable("text.item.horse_whistle.impossible_to_teleport_horse", horse.getName()));
        }
        else {
            player.sendOverlayMessage (Component.translatable("text.item.horse_whistle.impossible_to_teleport_horse", horse.getName()));
        }
    }

    private boolean isValidTeleportPosition(Horse horse, Level world, BlockPos pos) {
        BlockPos above = pos.above();

        return world.getBlockState(pos).isAir() &&
                world.getBlockState(above).isAir() &&
                world.getBlockState(above.above()).isAir() &&
                !world.getBlockState(pos.below()).isAir();
    }
}

