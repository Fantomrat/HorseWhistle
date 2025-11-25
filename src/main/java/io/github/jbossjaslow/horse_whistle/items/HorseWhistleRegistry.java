package io.github.jbossjaslow.horse_whistle.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jbossjaslow.horse_whistle.HorseWhistle;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class HorseWhistleRegistry {

    public static final ComponentType<AttunedHorseComponent> ATTUNED_HORSE =
            ComponentType.<AttunedHorseComponent>builder()
                    .codec(
                            RecordCodecBuilder.create(instance -> instance.group(
                                    Codec.STRING.fieldOf("horse_id").forGetter(AttunedHorseComponent::horseId),
                                    Codec.STRING.fieldOf("horse_name").forGetter(AttunedHorseComponent::horseName)
                            ).apply(instance, AttunedHorseComponent::new))
                    )
                    .build();

    public static final Item HORSE_WHISTLE_ITEM = register("horse_whistle", HorseWhistleItem::new, new Item.Settings().maxDamage(HorseWhistle.CONFIG.durability()).rarity(Rarity.RARE));

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(HorseWhistle.MOD_ID, name));

        Item item = itemFactory.apply(settings.registryKey(itemKey));

        return Registry.register(Registries.ITEM, itemKey, item);
    }

    public static void init() {
        Registry.register(Registries.DATA_COMPONENT_TYPE, id("attuned_horse"), ATTUNED_HORSE);
        ComponentTooltipAppenderRegistry.addAfter(DataComponentTypes.DAMAGE, ATTUNED_HORSE);


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(HORSE_WHISTLE_ITEM);
        });

    }

    public static Identifier id(String path) {
        return Identifier.of(HorseWhistle.MOD_ID, path);
    }
}
