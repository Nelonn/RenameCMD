package me.nelonn.renamecmd;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Command implements CommandExecutor {
    private final RenameCMD plugin;

    public Command(RenameCMD plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] args) {
        plugin.onDisable();
        plugin.onEnable();
        sender.sendMessage(ChatColor.GREEN + "RenameCMD reloaded!");
        return true;
    }
}
