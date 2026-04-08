package io.github.jbossjaslow.horse_whistle.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jbossjaslow.horse_whistle.HorseWhistleFabric;
import io.github.jbossjaslow.horse_whistle.config.HorseWhistleConfig;
import net.fabricmc.fabric.api.item.v1.ItemComponentTooltipProviderRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
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

public class HWRegistryFabric {

    public static final DataComponentType<AttunedHorseComponent> ATTUNED_HORSE =
            DataComponentType.<AttunedHorseComponent>builder()
                    .persistent(
                            RecordCodecBuilder.create(instance -> instance.group(
                                    Codec.STRING.fieldOf("horse_id").forGetter(AttunedHorseComponent::horseId),
                                    Codec.STRING.fieldOf("horse_name").forGetter(AttunedHorseComponent::horseName)
                            ).apply(instance, AttunedHorseComponent::new))
                    )
                    .build();

    public static final Item HORSE_WHISTLE_ITEM = register("horse_whistle", HWItem::new, new Item.Properties().durability(HorseWhistleConfig.INSTANCE.durability()).rarity(Rarity.RARE));

    public static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(HorseWhistleFabric.MOD_ID, name));

        Item item = itemFactory.apply(settings.setId(itemKey));

        return Registry.register(BuiltInRegistries.ITEM, itemKey, item);
    }

    public static void init() {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id("attuned_horse"), ATTUNED_HORSE);
        ItemComponentTooltipProviderRegistry.addAfter(DataComponents.DAMAGE, ATTUNED_HORSE);
        HWComponents.ATTUNED_HORSE = () -> ATTUNED_HORSE;

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(HORSE_WHISTLE_ITEM);
        });

    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(HorseWhistleFabric.MOD_ID, path);
    }
}
