package main.jake.quickshop;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class ConfigHandler {

    private QuickShop plugin;
    private FileConfiguration config;

    public ConfigHandler(QuickShop plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void write(){
        HashMap<String, Integer> writeMap = new HashMap<>();
        for(Material m : plugin.deals.keySet()){
            writeMap.put(m.name(), plugin.deals.get(m));
        }
        config.set("specialDeals", writeMap);
    }

    public void read(){
        ConfigurationSection cs = config.getConfigurationSection("specialDeals");
        if(cs != null){
            for(String s : cs.getKeys(true)){
                Material m = Material.getMaterial(s);
                if(m != null){
                    plugin.deals.put(m, cs.getInt(s));
                }

            }
        }
    }

    public void defaults(){
        config.addDefault("item", "DIAMOND");
        config.addDefault("allowSpecialDeals", true);
        config.addDefault("specialDealRollRate", 60);
        config.addDefault("specialDealChance", 10);

        HashMap<String, Integer> defaults = new HashMap<>();
        defaults.put("IRON_INGOT", 1);
        defaults.put("GOLD_INGOT", 1);
        defaults.put("EMERALD", 2);
        config.addDefault("specialDeals", defaults);

        config.options().copyDefaults(true);
        plugin.saveConfig();
    }


}
