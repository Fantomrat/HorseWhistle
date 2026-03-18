package io.github.jbossjaslow.horse_whistle.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jbossjaslow.horse_whistle.HorseWhistle;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import java.util.function.Function;

public class HorseWhistleRegistry {

    public static final DataComponentType<AttunedHorseComponent> ATTUNED_HORSE =
            DataComponentType.<AttunedHorseComponent>builder()
                    .persistent(
                            RecordCodecBuilder.create(instance -> instance.group(
                                    Codec.STRING.fieldOf("horse_id").forGetter(AttunedHorseComponent::horseId),
                                    Codec.STRING.fieldOf("horse_name").forGetter(AttunedHorseComponent::horseName)
                            ).apply(instance, AttunedHorseComponent::new))
                    )
                    .build();

    public static final Item HORSE_WHISTLE_ITEM = register("horse_whistle", HorseWhistleItem::new, new Item.Properties().durability(HorseWhistle.CONFIG.durability()).rarity(Rarity.RARE));

    public static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(HorseWhistle.MOD_ID, name));

        Item item = itemFactory.apply(settings.setId(itemKey));

        return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }

    public static void init() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("attuned_horse"), ATTUNED_HORSE);
        ComponentTooltipAppenderRegistry.addAfter(DataComponents.DAMAGE, ATTUNED_HORSE);


        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(HORSE_WHISTLE_ITEM);
        });

    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(HorseWhistle.MOD_ID, path);
    }
}
