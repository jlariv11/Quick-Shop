package main.jake.simpleshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class SimpleShop extends JavaPlugin {

    FileConfiguration config = this.getConfig();
    List<Shop> shops = new ArrayList<>();
    ShopFileHandler fileHandler = new ShopFileHandler(this);


    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SimpleShop has been ENABLED");
        getServer().getPluginManager().registerEvents(new Events(this), this);
        Commands commands = new Commands(this);
        getCommand(commands.shop).setExecutor(commands);
        config.addDefault("itemCurrency", true);
        config.addDefault("item", "minecraft:diamond");
        config.options().copyDefaults(true);
        saveConfig();
        shops = fileHandler.read();

    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "SimpleShop has been DISABLED");
        fileHandler.write();
    }

    public void setupandOpenShop(Player player){

        int slotsNeeded = (int)Math.ceil((shops.size() + 1) / 9.0) * 9;

        Inventory shopInv = getServer().createInventory(null, slotsNeeded, "Shop");

        ItemStack stack = new ItemStack(Material.DIAMOND_AXE, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Add Item to Shop");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        shopInv.setItem(shopInv.getSize() - 1, stack);


        for(int i = 0; i < shops.size(); i++){
            Shop shop = shops.get(i);
            ItemStack s = shop.getItem();
            ItemMeta m = s.getItemMeta();
            if(m != null){
                List<String> lore = new ArrayList<>();
                String diamond = " diamond";
                if(shop.getCost() > 1){
                    diamond += "s";
                }
                lore.add("Cost: " + shop.getCost() + diamond + " for " + shop.getAmountPerSell());
                m.setLore(lore);
                s.setItemMeta(m);
                shopInv.setItem(i, s);
            }
        }

        player.openInventory(shopInv);
    }

    public void setupAndOpenAddItem(Player player){

        Inventory addItemInv = getServer().createInventory(null, 9, "Sell an Item");

        ItemStack stack = new ItemStack(Material.DIAMOND, 1);
        ItemStack confirm = new ItemStack(Material.EMERALD, 1);
        ItemMeta meta = confirm.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Confirm");
        confirm.setItemMeta(meta);
        addItemInv.setItem(7, stack);
        addItemInv.setItem(8, confirm);
        player.openInventory(addItemInv);
    }

}
