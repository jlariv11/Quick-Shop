package main.jake.quickshop;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Events implements Listener {

    private QuickShop plugin;

    public Events(QuickShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void invClick(InventoryClickEvent e) {
        Player clicker = (Player) e.getWhoClicked();
        ClickType type = e.getClick();
        Inventory inv = e.getClickedInventory();


        if (inv == null) {
            return;
        }

        if (e.getView().getTitle().equals("Shop")) {
            int compareSize = plugin.shops.size() == 0 ? 8 : (int) ((Math.ceil(plugin.shops.size() / 9.0) * 9) - 1);
            if (e.getRawSlot() == compareSize) {
                clicker.closeInventory();
                plugin.setupAndOpenAddItem(clicker, false);
            }
            if (e.getRawSlot() != inv.getSize() - 1 && e.getRawSlot() < plugin.shops.size()) {
                ItemStack stack = inv.getItem(e.getRawSlot());
                if (stack != null) {
                    Shop shop = plugin.shops.get(e.getRawSlot());
                    if (shop != null) {
                        int cost = shop.getCost();
                        ItemStack toRemove = shop.isInfinite() ? shop.getItem() : new ItemStack(plugin.currency, cost);
                        if (removeMoney(clicker, toRemove)) {
                            shop.sell();
                            ItemStack toGive = shop.isInfinite() ? new ItemStack(plugin.currency, cost) : shop.getItem().clone();
                            toGive.setItemMeta(null);
                            int remainder = ItemStackHelper.addItemsToInventory(clicker, toGive);
                            if (remainder > 0) {
                                toGive.setAmount(remainder);
                                clicker.sendMessage(ChatColor.RED + "Your inventory is full. Your purchase has been dropped on the ground.");
                                clicker.getWorld().dropItem(clicker.getLocation(), toGive);
                            }
                            clicker.sendMessage("Item received! " + shop.getBuffer());
                            if(!shop.isInfinite() && shop.getBuffer() <= 0){
                                plugin.shops.remove(shop);
                                clicker.closeInventory();
                                plugin.setupandOpenShop(clicker);
                            }
                        }else{
                            clicker.sendMessage("You don't have enough money!");
                        }
                        e.setCancelled(true);
                    }
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("Sell an Item") || e.getView().getTitle().equals("Sell an Item(Infinite)")) {
            if (e.getRawSlot() == 7) {
                ItemStack item = inv.getItem(7);
                if (item != null) {
                    if (type == ClickType.LEFT) {
                        if (item.getAmount() + 1 > 64) {
                            return;
                        }
                        item.setAmount(item.getAmount() + 1);
                    } else if (type == ClickType.SHIFT_LEFT) {
                        if (item.getAmount() + 10 > 64) {
                            item.setAmount(64);
                            return;
                        }
                        item.setAmount(item.getAmount() + 10);
                    } else if (type == ClickType.RIGHT) {
                        if (item.getAmount() - 1 < 1) {
                            return;
                        }
                        item.setAmount(item.getAmount() - 1);
                    } else if (type == ClickType.SHIFT_RIGHT) {
                        if (item.getAmount() - 10 < 1) {
                            item.setAmount(1);
                            return;
                        }
                        item.setAmount(item.getAmount() - 10);
                    }
                    ItemMeta cost = item.getItemMeta();
                    cost.setDisplayName(ChatColor.AQUA + "Cost: " + item.getAmount());
                    item.setItemMeta(cost);
                }
                e.setCancelled(true);
            }
            if (e.getRawSlot() == 8) {
                if (inv.getItem(0) != null) {
                    ItemStack toSell = inv.getItem(0);
                    ItemStack cost = inv.getItem(7);
                    if (toSell == null || cost == null) {
                        clicker.sendMessage("Item to sell or cost not specified");
                        return;
                    }

                    int buffer = 0;
                    for (int i = 0; i < 6; i++) {
                        ItemStack s = inv.getItem(i);
                        if (s != null) {
                            if (s.getType() == toSell.getType()) {
                                buffer += s.getAmount();
                            } else {
                                clicker.getWorld().dropItem(clicker.getLocation(), s);
                            }
                        }
                    }
                    if (e.getView().getTitle().equals("Sell an Item(Infinite)")) {
                        plugin.shops.add(new Shop(inv.getItem(0), clicker, toSell.getAmount(), cost.getAmount(), -1));
                    } else {
                        plugin.shops.add(new Shop(inv.getItem(0), clicker, toSell.getAmount(), cost.getAmount(), buffer));
                    }
                    clicker.sendMessage("Shop Created!");
                    clicker.closeInventory();
                    plugin.setupandOpenShop(clicker);
                }else{
                    e.setCancelled(true);
                }
            }
        }
        if(e.getView().getTitle().equals("Remove Shops")){
            ItemStack stack = inv.getItem(e.getRawSlot());
            Shop shop = plugin.shops.get(e.getRawSlot());
            if(shop != null && stack != null){
                if(shop.getSeller() == null){
                    plugin.shops.remove(shop);
                    clicker.sendMessage("Shop Removed!");
                    stack.setAmount(0);
                    e.setCancelled(true);
                    return;
                }
                int stacks = shop.getBuffer() / shop.getItem().getMaxStackSize();
                int remaining = shop.getBuffer() % shop.getItem().getMaxStackSize();
                ItemStack s = shop.getItem().clone();
                s.setItemMeta(null);
                for(int i = 0; i < stacks; i++){
                    s.setAmount(shop.getItem().getMaxStackSize());
                    int remainder = ItemStackHelper.addItemsToInventory(clicker, s);
                    if(remainder > 0){
                        ItemStack rem = new ItemStack(s.getType(), remainder);
                        clicker.sendMessage(ChatColor.RED + "Your inventory is full. Your items have been dropped on the ground.");
                        clicker.getWorld().dropItem(clicker.getLocation(), rem);
                    }
                }
                s.setAmount(remaining);
                int remainder = ItemStackHelper.addItemsToInventory(clicker, s);
                if(remainder > 0){
                    ItemStack rem = new ItemStack(s.getType(), remainder);
                    clicker.sendMessage(ChatColor.RED + "Your inventory is full. Your items have been dropped on the ground.");
                    clicker.getWorld().dropItem(clicker.getLocation(), rem);
                }
                plugin.shops.remove(shop);
                shop.getSeller().sendMessage("Your shop has been deleted and your items have been returned to you!");
                clicker.sendMessage("Shop Removed!");
                stack.setAmount(0);
                e.setCancelled(true);
            }
        }

    }

    private boolean removeMoney(Player player, ItemStack toRemove){

        List<Integer> moneySlots = new ArrayList<>();

        for(int i = 0; i < 36; i++){
            ItemStack s = player.getInventory().getItem(i);
            if(s != null){
                if(s.isSimilar(toRemove)){
                    moneySlots.add(i);
                }
            }
        }
        int totalMoney = 0;
        for(int i : moneySlots){
            ItemStack s = player.getInventory().getItem(i);
            if(s != null){
                totalMoney += s.getAmount();
            }
        }
        int totalAmtRemove = toRemove.getAmount();
        if(totalMoney >= toRemove.getAmount()){
            for(int i : moneySlots){
                ItemStack s = player.getInventory().getItem(i);
                if(s != null){
                    int amtRemove = Math.min(totalAmtRemove, s.getAmount());
                    s.setAmount(s.getAmount() - amtRemove);
                    totalAmtRemove -= amtRemove;
                    if(totalAmtRemove <= 0){
                        break;
                    }
                }
            }
        }else{
            return false;
        }

        return true;
    }

}
