package io.github.jbossjaslow.horse_whistle.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HorseWhistleConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Path.of("config/horse-whistle.json");

    public final static HorseWhistleConfig INSTANCE = new HorseWhistleConfig();

    private int searchRadius = 500;
    private int durability = 16;
    private int teleportRadius = 8;
    private int itemCooldown = 20;

    private HorseWhistleConfig() {}

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                HorseWhistleConfig loaded = GSON.fromJson(json, HorseWhistleConfig.class);
                if (loaded != null) {
                    INSTANCE.copyFrom(loaded);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        save();
    }

    private void copyFrom(HorseWhistleConfig other) {
        this.searchRadius = other.searchRadius;
        this.durability = other.durability;
        this.teleportRadius = other.teleportRadius;
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int searchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(int value) {
        this.searchRadius = value;
        save();
    }

    public int durability() {
        return durability;
    }

    public void setDurability(int value) {
        this.durability = value;
        save();
    }

    public int teleportRadius() {
        return teleportRadius;
    }

    public void setTeleportRadius(int value) {
        this.teleportRadius = value;
        save();
    }

    public int itemCooldown() {
        return itemCooldown;
    }

    public void setItemCooldown(int value) {
        this.itemCooldown = value;
        save();
    }

    public static Screen createScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.horse_whistle.title"))
                .setSavingRunnable(HorseWhistleConfig::save);

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.horse_whistle.general"));

        general.addEntry(entryBuilder.startIntField(Component.translatable("text.config.horse_whistle.search_radius"), INSTANCE.searchRadius)
                .setDefaultValue(500)
                .setMin(10).setMax(1000)
                .setSaveConsumer(INSTANCE::setSearchRadius)
                .setTooltip(Component.translatable("text.config.horse_whistle.search_radius.tooltip"))
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("text.config.horse_whistle.durability"), INSTANCE.durability)
                .setDefaultValue(16)
                .setMin(4).setMax(256)
                .setSaveConsumer(INSTANCE::setDurability)
                .setTooltip(Component.translatable("text.config.horse_whistle.durability.tooltip"))
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("text.config.horse_whistle.teleport_radius"), INSTANCE.teleportRadius)
                .setDefaultValue(8)
                .setMin(1).setMax(128)
                .setSaveConsumer(INSTANCE::setTeleportRadius)
                .setTooltip(Component.translatable("text.config.horse_whistle.teleport_radius.tooltip"))
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("text.config.horse_whistle.item_cooldown"), INSTANCE.itemCooldown)
                .setDefaultValue(20)
                .setMin(1).setMax(1000000)
                .setSaveConsumer(INSTANCE::setItemCooldown)
                .setTooltip(Component.translatable("text.config.horse_whistle.item_cooldown.tooltip"))
                .build());

        return builder.build();
    }
}

