package io.github.jbossjaslow.horse_whistle.config;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.Option;

import java.util.function.Consumer;

public class HorseWhistleConfig extends ConfigWrapper<io.github.jbossjaslow.horse_whistle.config.ConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Integer> searchRadius = this.optionForKey(this.keys.searchRadius);
    private final Option<java.lang.Integer> durability = this.optionForKey(this.keys.durability);

    private HorseWhistleConfig() {
        super(io.github.jbossjaslow.horse_whistle.config.ConfigModel.class);
    }

    private HorseWhistleConfig(Consumer<Jankson.Builder> janksonBuilder) {
        super(io.github.jbossjaslow.horse_whistle.config.ConfigModel.class, janksonBuilder);
    }

    public static HorseWhistleConfig createAndLoad() {
        var wrapper = new HorseWhistleConfig();
        wrapper.load();
        return wrapper;
    }

    public static HorseWhistleConfig createAndLoad(Consumer<Jankson.Builder> janksonBuilder) {
        var wrapper = new HorseWhistleConfig(janksonBuilder);
        wrapper.load();
        return wrapper;
    }

    public int searchRadius() {
        return searchRadius.value();
    }

    public void searchRadius(int value) {
        searchRadius.set(value);
    }

    public int durability() {
        return durability.value();
    }

    public void durability(int value) {
        durability.set(value);
    }


    public static class Keys {
        public final Option.Key searchRadius = new Option.Key("searchRadius");
        public final Option.Key durability = new Option.Key("durability");
    }
}

