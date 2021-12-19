package me.nelonn.renamecmd;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class RenameItem {
    private final Material type;
    private final Pattern pattern;
    private final int customModelData;

    public RenameItem(Material type, Pattern pattern, int customModelData) {
        this.type = type;
        this.pattern = pattern;
        this.customModelData = customModelData;
    }

    public Material getType() {
        return type;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    @Nullable
    public static RenameItem load(@NotNull FileConfiguration config) {
        String typeString = config.getString("type");
        if (typeString == null || typeString.isEmpty()) {
            Bukkit.getLogger().severe(RenameCMD.LOG_PREFIX + "Item type not defined");
            return null;
        }
        Material type = Material.matchMaterial(typeString);
        if (type == null) {
            Bukkit.getLogger().severe(RenameCMD.LOG_PREFIX + "Item type '" + typeString + "' not found");
            return null;
        }
        String patternString = config.getString("pattern");
        if (patternString == null || patternString.isEmpty()) {
            Bukkit.getLogger().severe(RenameCMD.LOG_PREFIX + "Item pattern not defined");
            return null;
        }
        Pattern pattern;
        try {
            pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        } catch (Exception ignore) {
            Bukkit.getLogger().severe(RenameCMD.LOG_PREFIX + "Error while compiling pattern");
            return null;
        }
        int customModelData = config.getInt("custom_model_data");
        if (customModelData < 1) {
            Bukkit.getLogger().severe(RenameCMD.LOG_PREFIX + "Item custom_model_data must be more than 0");
        }
        return new RenameItem(type, pattern, customModelData);
    }
}
