package main.jake.simpleshop;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleShop extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SimpleShop has been ENABLED");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "SimpleShop has been DISABLED");
    }

}
