package io.github.jbossjaslow.horse_whistle.items;

import io.github.jbossjaslow.horse_whistle.HorseWhistle;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class HorseWhistleItem extends Item {

    private static final int ITEM_COOLDOWN = 20; // ticks?

	/*
	##################################################

	PUBLIC METHODS

	##################################################
	 */

    public HorseWhistleItem(Item.Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);

        ItemStack stack = user.getStackInHand(hand);

        if (user.getEntityWorld().isClient()) return ActionResult.FAIL;

        if (user.getPose() == EntityPose.CROUCHING) {
            if (stack.getComponents().contains(HorseWhistleRegistry.ATTUNED_HORSE)) {
                var component = stack.get(HorseWhistleRegistry.ATTUNED_HORSE);

                assert component != null;
                String horseName = component.horseName();

                user.sendMessage(Text.translatable("text.item.horse_whistle.remove_attunement", horseName), true);

                stack.remove(HorseWhistleRegistry.ATTUNED_HORSE);

                world.playSound(
                        null,
                        user.getBlockPos(),
                        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                        SoundCategory.MASTER,
                        0.5f,
                        0.5f
                );
                return ActionResult.CONSUME;
            } else return ActionResult.PASS;
        }

        if (stack.getComponents().contains(HorseWhistleRegistry.ATTUNED_HORSE)) {
            user.getItemCooldownManager().set(stack, ITEM_COOLDOWN);

            stack.damage(1, user);

            var component = stack.get(HorseWhistleRegistry.ATTUNED_HORSE);
            assert component != null;
            String horseId = component.horseId();
            String horseName = component.horseName();

            // Поиск лошади по UUID
            double radius = HorseWhistle.CONFIG.searchRadius();
            Box searchArea = new Box(
                    user.getX() - radius,
                    user.getY() - radius,
                    user.getZ() - radius,
                    user.getX() + radius,
                    user.getY() + radius,
                    user.getZ() + radius
            );

            List<HorseEntity> horses = world.getEntitiesByType(
                    EntityType.HORSE,
                    searchArea,
                    EntityPredicates.VALID_LIVING_ENTITY
            );

            for (HorseEntity horse : horses) {
                if (horse.getUuidAsString().equals(horseId)) {
                    teleportHorse(horse, user, world);
                    return ActionResult.CONSUME;
                }
            }

            user.sendMessage(Text.translatable("text.item.horse_whistle.could_not_find_horse", horseName), true);

        }

        return ActionResult.FAIL;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        super.useOnEntity(stack, user, entity, hand);
        user.getItemCooldownManager().set(stack, ITEM_COOLDOWN);

        // We cannot be on the client to check the UUID of the player
        if (user.getEntityWorld().isClient()) return ActionResult.FAIL;

        if (entity.getType() != EntityType.HORSE || stack.getComponents().contains(HorseWhistleRegistry.ATTUNED_HORSE))
            return ActionResult.PASS;

        HorseEntity horseEntity = (HorseEntity) entity;

        if (horseEntity.isTame() && horseEntity.isTame()) {
            if (horseEntity.getOwner() == null || horseEntity.getOwner().getUuid() != user.getUuid()) {
                user.sendMessage(Text.translatable("text.item.horse_whistle.not_owner"), true);
                return ActionResult.CONSUME;
            }



            user.getEntityWorld().playSound(
                    null,
                    user.getBlockPos(),
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.MASTER,
                    0.5f,
                    0.5f);

            String horseName = horseEntity.hasCustomName()
                    ? horseEntity.getCustomName().getString()
                    : horseEntity.getName().getString();

            String horseId = horseEntity.getUuidAsString();

            stack.set(
                    HorseWhistleRegistry.ATTUNED_HORSE,
                    new AttunedHorseComponent(horseId, horseName)
            );

            user.sendMessage(Text.translatable("text.item.horse_whistle.add_attunement", horseName), true);
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return super.hasGlint(stack) || stack.getComponents().contains(HorseWhistleRegistry.ATTUNED_HORSE);
    }

	/*
	##################################################

	PRIVATE METHODS

	##################################################
	 */

    private void teleportHorse(HorseEntity horse, PlayerEntity player, World world) {
        double xPos = player.getX();
        double yPos = player.getY();
        double zPos = player.getZ();
        // TODO: make teleport random, ensure it teleports to a valid block

        int randomX = horse.getRandom().nextInt(10) - 5;
        int randomZ = horse.getRandom().nextInt(10) - 5;

        horse.teleport(xPos + randomX, yPos, zPos + randomZ, false);
        world.playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                SoundCategory.MASTER,
                1f,
                1f);
    }
}

