package io.github.jbossjaslow.horse_whistle.items;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;

public record AttunedHorseComponent(String horseId, String horseName)
        implements TooltipProvider {

    @Override
    public void addToTooltip(
            Item.@NonNull TooltipContext context,
            Consumer<Component> textConsumer,
            @NonNull TooltipFlag type,
            @NonNull DataComponentGetter components
    ) {
        textConsumer.accept(Component.literal("Attuned to " + horseName).withStyle(ChatFormatting.GRAY));
    }
}



