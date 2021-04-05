package main.jake.quickshop;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;

public class DealsFileHandler {

    private QuickShop plugin;

    public DealsFileHandler(QuickShop plugin) {
        this.plugin = plugin;
    }

    public void write(){
        HashMap<String, Integer> writeMap = new HashMap<>();
        for(Material m : plugin.deals.keySet()){
            writeMap.put(m.name(), plugin.deals.get(m));
        }
        plugin.config.set("specialDeals", writeMap);
    }

    public void read(){
        ConfigurationSection cs = plugin.config.getConfigurationSection("specialDeals");
        if(cs != null){
            for(String s : cs.getKeys(true)){
                Material m = Material.getMaterial(s);
                if(m != null){
                    plugin.deals.put(m, cs.getInt(s));
                }

            }
        }
    }


}
