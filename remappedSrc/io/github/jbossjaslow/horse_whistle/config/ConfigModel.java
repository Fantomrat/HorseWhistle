package io.github.jbossjaslow.horse_whistle.config;

import io.github.jbossjaslow.horse_whistle.HorseWhistle;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.RangeConstraint;
import io.wispforest.owo.config.annotation.Sync;

@Modmenu(modId = HorseWhistle.MOD_ID)
@Config(name = "horse-whistle-config", wrapperName = "HorseWhistleConfig")
public class ConfigModel {
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RangeConstraint(min = 10, max = 1000)
    public int searchRadius = 500;

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    @RangeConstraint(min = 4, max = 256)
    public int durability = 16;
}
