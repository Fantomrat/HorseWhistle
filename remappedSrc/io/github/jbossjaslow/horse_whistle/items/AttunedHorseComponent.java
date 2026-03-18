package io.github.jbossjaslow.horse_whistle.items;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record AttunedHorseComponent(String horseId, String horseName)
        implements TooltipProvider {

    @Override
    public void addToTooltip(
            Item.TooltipContext context,
            Consumer<Component> textConsumer,
            TooltipFlag type,
            DataComponentGetter components
    ) {
        textConsumer.accept(Component.literal("Attuned to " + horseName).withStyle(ChatFormatting.GRAY));
    }
}



