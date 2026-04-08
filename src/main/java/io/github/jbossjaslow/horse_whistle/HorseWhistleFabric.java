package io.github.jbossjaslow.horse_whistle;

import io.github.jbossjaslow.horse_whistle.config.HorseWhistleConfig;
import io.github.jbossjaslow.horse_whistle.items.HWRegistryFabric;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HorseWhistleFabric implements ModInitializer {
	public static final String MOD_ID = "horse_whistle";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
	public void onInitialize() {
		LOGGER.info("Horse Whistle initialized!");

		HorseWhistleConfig.load();
        HWRegistryFabric.init();
	}
}