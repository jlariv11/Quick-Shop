package main.jake.quickshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class QuickShop extends JavaPlugin {

    public FileConfiguration config = this.getConfig();
    public List<Shop> shops = new ArrayList<>();
    public HashMap<Material, Integer> deals = new HashMap<>();
    private ShopFileHandler fileHandler = new ShopFileHandler(this);
    public ConfigHandler configHandler = new ConfigHandler(this);
    public Material currency;

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "QuickShop has been ENABLED");
        getServer().getPluginManager().registerEvents(new Events(this), this);

        Commands commands = new Commands(this);
        for(String name : commands.COMMANDS){
            PluginCommand cmd = getCommand(name);
            if(cmd != null){
                cmd.setExecutor(commands);
            }
        }

        configHandler.read();
        configHandler.defaults();

        shops = fileHandler.read();
        String material = config.getString("item");
        if(material != null) {
            currency = Material.getMaterial(material);
        }else{
            currency = Material.DIAMOND;
        }

        if(config.getBoolean("allowSpecialDeals")) {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    Random rand = new Random();
                    int num = rand.nextInt(101);
                    if (num <= config.getInt("specialDealChance")) {
                        Material m = getRandMaterial();
                        ItemStack stack = new ItemStack(m);
                        shops.add(new Shop(stack, null, 1, deals.get(m), rand.nextInt(128)));
                        getServer().broadcastMessage("[QuickShop] Special Deal Added to Shops! 1 " + m.name() + " for " + deals.get(m) + " " + currency.name() + "!");
                    }
                }
            }, config.getInt("specialDealRollRate") * 20L, config.getInt("specialDealRollRate") * 20L);
        }

    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "QuickShop has been DISABLED");
        fileHandler.write();
        configHandler.write();

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
                String costItem = " " + getConfig().getString("item").toLowerCase();
                if(shop.isInfinite()) {
                    lore.add("Infinite: " + shop.getAmountPerSell() + " for " + shop.getCost() + costItem);
                }else{
                    lore.add("Cost: " + shop.getCost() + costItem + " for " + shop.getAmountPerSell());
                }
                m.setLore(lore);
                s.setItemMeta(m);
                shopInv.setItem(i, s);
            }
        }

        player.openInventory(shopInv);
    }

    public void setupAndOpenAddItem(Player player, boolean admin){

        Inventory addItemInv = getServer().createInventory(null, 9, admin ? "Sell an Item(Infinite)" : "Sell an Item");

        ItemStack stack = new ItemStack(currency, 1);
        ItemStack confirm = new ItemStack(Material.EMERALD, 1);
        ItemMeta meta = confirm.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Confirm");
        confirm.setItemMeta(meta);
        ItemMeta cost = stack.getItemMeta();
        cost.setDisplayName(ChatColor.AQUA + "Cost: " + stack.getAmount());
        stack.setItemMeta(cost);
        addItemInv.setItem(7, stack);
        addItemInv.setItem(8, confirm);
        player.openInventory(addItemInv);
    }

    public void setupRemoveShop(Player player){
        int slotsNeeded = (int)Math.ceil((shops.size() + 1) / 9.0) * 9;
        Inventory removeShopInv = getServer().createInventory(null, slotsNeeded, "Remove Shops");

        for(int i = 0; i < shops.size(); i++){
            Shop shop = shops.get(i);
            ItemStack s = shop.getItem();
            ItemMeta m = s.getItemMeta();
            if(player.isOp() && shop.getSeller() == null || shop.getSeller().getUniqueId().equals(player.getUniqueId()) || player.isOp()) {
                if (m != null) {
                    List<String> lore = new ArrayList<>();
                    lore.add(shop.getSeller() == null ? "Deal Shop" : shop.getSeller().getDisplayName() + "'s " + "Shop");
                    m.setLore(lore);
                    s.setItemMeta(m);
                    removeShopInv.setItem(i, s);
                }
            }
        }
        player.openInventory(removeShopInv);
    }

    private Material getRandMaterial(){
        List<Material> mats = new ArrayList<>(deals.keySet());
        return mats.get(new Random().nextInt(mats.size()));
    }

}
