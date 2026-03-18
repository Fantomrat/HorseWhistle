package io.github.jbossjaslow.horse_whistle.items;

import io.github.jbossjaslow.horse_whistle.HorseWhistle;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.*;
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
import java.util.List;

public class HorseWhistleItem extends Item {

    private static final int ITEM_COOLDOWN = 20; // ticks?

	/*
	##################################################

	PUBLIC METHODS

	##################################################
	 */

    public HorseWhistleItem(Item.Properties settings) {
        super(settings);
    }


    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        super.use(world, user, hand);

        ItemStack stack = user.getItemInHand(hand);

        if (user.level().isClientSide()) return InteractionResult.FAIL;

        if (user.getPose() == Pose.CROUCHING) {
            if (stack.getComponents().has(HorseWhistleRegistry.ATTUNED_HORSE)) {
                var component = stack.get(HorseWhistleRegistry.ATTUNED_HORSE);

                assert component != null;
                String horseName = component.horseName();

                user.displayClientMessage(Component.translatable("text.item.horse_whistle.remove_attunement", horseName), true);

                stack.remove(HorseWhistleRegistry.ATTUNED_HORSE);

                world.playSound(
                        null,
                        user.blockPosition(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.MASTER,
                        0.5f,
                        0.5f
                );
                return InteractionResult.CONSUME;
            } else return InteractionResult.PASS;
        }

        if (stack.getComponents().has(HorseWhistleRegistry.ATTUNED_HORSE)) {
            user.getCooldowns().addCooldown(stack, ITEM_COOLDOWN);

            stack.hurtWithoutBreaking(1, user);

            var component = stack.get(HorseWhistleRegistry.ATTUNED_HORSE);
            assert component != null;
            String horseId = component.horseId();
            String horseName = component.horseName();

            // Поиск лошади по UUID
            double radius = HorseWhistle.CONFIG.searchRadius();
            AABB searchArea = new AABB(
                    user.getX() - radius,
                    user.getY() - radius,
                    user.getZ() - radius,
                    user.getX() + radius,
                    user.getY() + radius,
                    user.getZ() + radius
            );

            List<Horse> horses = world.getEntities(
                    EntityType.HORSE,
                    searchArea,
                    EntitySelector.LIVING_ENTITY_STILL_ALIVE
            );

            for (Horse horse : horses) {
                if (horse.getStringUUID().equals(horseId)) {
                    teleportHorse(horse, user, world);
                    return InteractionResult.CONSUME;
                }
            }

            user.displayClientMessage(Component.translatable("text.item.horse_whistle.could_not_find_horse", horseName), true);

        }

        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        super.interactLivingEntity(stack, user, entity, hand);
        user.getCooldowns().addCooldown(stack, ITEM_COOLDOWN);

        // We cannot be on the client to check the UUID of the player
        if (user.level().isClientSide()) return InteractionResult.FAIL;

        if (entity.getType() != EntityType.HORSE || stack.getComponents().has(HorseWhistleRegistry.ATTUNED_HORSE))
            return InteractionResult.PASS;

        Horse horseEntity = (Horse) entity;

        if (horseEntity.isTamed() && horseEntity.isTamed()) {
            if (horseEntity.getOwner() == null || horseEntity.getOwner().getUUID() != user.getUUID()) {
                user.displayClientMessage(Component.translatable("text.item.horse_whistle.not_owner"), true);
                return InteractionResult.CONSUME;
            }



            user.level().playSound(
                    null,
                    user.blockPosition(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundSource.MASTER,
                    0.5f,
                    0.5f);

            String horseName = horseEntity.hasCustomName()
                    ? horseEntity.getCustomName().getString()
                    : horseEntity.getName().getString();

            String horseId = horseEntity.getStringUUID();

            stack.set(
                    HorseWhistleRegistry.ATTUNED_HORSE,
                    new AttunedHorseComponent(horseId, horseName)
            );

            user.displayClientMessage(Component.translatable("text.item.horse_whistle.add_attunement", horseName), true);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || stack.getComponents().has(HorseWhistleRegistry.ATTUNED_HORSE);
    }

	/*
	##################################################

	PRIVATE METHODS

	##################################################
	 */

    private void teleportHorse(Horse horse, Player player, Level world) {
        double xPos = player.getX();
        double yPos = player.getY();
        double zPos = player.getZ();
        // TODO: make teleport random, ensure it teleports to a valid block

        int randomX = horse.getRandom().nextInt(10) - 5;
        int randomZ = horse.getRandom().nextInt(10) - 5;

        horse.randomTeleport(xPos + randomX, yPos, zPos + randomZ, false);
        world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                SoundSource.MASTER,
                1f,
                1f);
    }
}

