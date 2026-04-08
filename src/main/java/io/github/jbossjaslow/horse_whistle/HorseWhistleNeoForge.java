package io.github.jbossjaslow.horse_whistle;

import com.mojang.logging.LogUtils;
import io.github.jbossjaslow.horse_whistle.config.HorseWhistleConfig;
import io.github.jbossjaslow.horse_whistle.items.HWRegistryNeoforge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;

@Mod(HorseWhistleNeoForge.MOD_ID)
public class HorseWhistleNeoForge {
    public static final String MOD_ID = "horse_whistle";
    public static final Logger LOGGER = LogUtils.getLogger();

    public HorseWhistleNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Horse Whistle initialized!");

        HorseWhistleConfig.load();
        HWRegistryNeoforge.register(modEventBus);

        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (mc, parent) -> HorseWhistleConfig.createScreen(parent)
        );
    }
}
