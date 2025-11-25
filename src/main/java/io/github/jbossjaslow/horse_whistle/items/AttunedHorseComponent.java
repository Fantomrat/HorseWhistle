package io.github.jbossjaslow.horse_whistle.items;

import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public record AttunedHorseComponent(String horseId, String horseName)
        implements TooltipAppender {

    @Override
    public void appendTooltip(
            Item.TooltipContext context,
            Consumer<Text> textConsumer,
            TooltipType type,
            ComponentsAccess components
    ) {
        textConsumer.accept(Text.literal("Attuned to " + horseName).formatted(Formatting.GRAY));
    }
}



