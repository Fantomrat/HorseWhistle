package io.github.jbossjaslow.horse_whistle.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.jbossjaslow.horse_whistle.HorseWhistleNeoForge;
import io.github.jbossjaslow.horse_whistle.config.HorseWhistleConfig;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HWRegistryNeoforge {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HorseWhistleNeoForge.MOD_ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, HorseWhistleNeoForge.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AttunedHorseComponent>> ATTUNED_HORSE =
            DATA_COMPONENTS.registerComponentType("attuned_horse", builder ->
                    builder.persistent(
                            RecordCodecBuilder.create(instance -> instance.group(
                                    Codec.STRING.fieldOf("horse_id").forGetter(AttunedHorseComponent::horseId),
                                    Codec.STRING.fieldOf("horse_name").forGetter(AttunedHorseComponent::horseName)
                            ).apply(instance, AttunedHorseComponent::new))
                    )
            );

    public static final DeferredItem<Item> HORSE_WHISTLE_ITEM = ITEMS.registerItem(
            "horse_whistle",
            props -> new HWItem(props
                            .durability(HorseWhistleConfig.INSTANCE.durability())
                            .rarity(Rarity.RARE)
            )
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        DATA_COMPONENTS.register(eventBus);
        HWComponents.ATTUNED_HORSE = ATTUNED_HORSE;

        eventBus.addListener(HWRegistryNeoforge::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(HORSE_WHISTLE_ITEM);
        }
    }
}
