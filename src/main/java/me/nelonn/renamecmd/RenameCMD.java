package me.nelonn.renamecmd;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class RenameCMD extends JavaPlugin implements Listener {
    public static final String LOG_PREFIX = "[RenameCMD] ";

    private List<RenameItem> items;

    @Override
    public void onEnable() {
        items = new ArrayList<>();
        File itemsFolder = getDataFolder();
        if (itemsFolder.exists()) {
            if (!itemsFolder.isDirectory()) {
                Bukkit.getLogger().severe(LOG_PREFIX + "'plugins/" + getName() + "' isn't directory");
                return;
            }
            Bukkit.getLogger().info(LOG_PREFIX + "Loading items...");
            readItemsIn(itemsFolder);
            Bukkit.getLogger().info(LOG_PREFIX + String.format("Loaded %s item%s", items.size(), items.size() == 1 ? "" : "s"));
        } else {
            itemsFolder.mkdir();
            Reader reader = getTextResource("example_item.yml");
            if (reader != null) {
                FileConfiguration exampleItem = YamlConfiguration.loadConfiguration(reader);
                try {
                    exampleItem.save(new File(itemsFolder, "example_item.yml"));
                    Bukkit.getLogger().info(LOG_PREFIX + "Saved 'example_item.yml'");
                } catch (Exception ignore) {}
            }
        }

        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("renamecmd").setExecutor(new Command(this));

        Bukkit.getLogger().info(LOG_PREFIX + "Success!");
    }

    private void readItemsIn(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                readItemsIn(file);
            } else if (file.getName().endsWith(".yml")) {
                RenameItem renameItem = RenameItem.load(YamlConfiguration.loadConfiguration(file));
                if (renameItem == null) {
                    Bukkit.getLogger().severe(LOG_PREFIX + "Item '" + file.getName() + "' not loaded!");
                    continue;
                }
                items.add(renameItem);
            }
        }
    }

    @Override
    public void onDisable() {
        items = null;
        HandlerList.unregisterAll((JavaPlugin) this);
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result == null || result.getType().isAir()) return;

        ItemMeta itemMeta = result.getItemMeta();
        assert itemMeta != null;
        String name = itemMeta.getDisplayName();
        if (name.isEmpty()) return;

        for (RenameItem renameItem : items) {
            if (renameItem.getType() == result.getType()) {
                Matcher matcher = renameItem.getPattern().matcher(name);
                if (matcher.find()) {
                    itemMeta.setCustomModelData(renameItem.getCustomModelData());
                    result.setItemMeta(itemMeta);
                    event.setResult(result);
                    return;
                }
            }
        }
    }
}
