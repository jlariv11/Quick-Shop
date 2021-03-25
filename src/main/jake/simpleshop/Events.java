package main.jake.simpleshop;

import net.minecraft.server.v1_16_R3.EntityItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Events implements Listener {

    private SimpleShop plugin;

    public Events(SimpleShop plugin) {
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
            if (e.getSlot() == inv.getSize() - 1) {
                clicker.closeInventory();
                plugin.setupAndOpenAddItem(clicker, false);
            }
            if (e.getSlot() != inv.getSize() - 1) {
                ItemStack stack = inv.getItem(e.getSlot());
                if (stack != null) {
                    Shop shop = getShopFromItemStack(stack);
                    if (shop != null) {
                        int cost = shop.getCost();
                        ItemStack toRemove = shop.isInfinite() ? shop.getItem() : new ItemStack(plugin.currency, cost);
                        ItemStack moneyStack = getMoney(clicker, toRemove);
                        if (moneyStack != null) {
                            moneyStack.setAmount(moneyStack.getAmount() - toRemove.getAmount());
                            shop.sell();
                            int slot = getEmptyOrCompatibleSlot(clicker);
                            ItemStack toGive = shop.isInfinite() ? new ItemStack(plugin.currency, cost) : shop.getItem().clone();
                            toGive.setItemMeta(null);
                            if (slot == -1) {
                                clicker.sendMessage(ChatColor.RED + "Your inventory is full. Your purchase has been dropped on the ground.");
                                clicker.getWorld().dropItem(clicker.getLocation(), toGive);
                            }
                            if (clicker.getInventory().getItem(slot) == null) {
                                clicker.getInventory().setItem(slot, toGive);
                            } else {
                                clicker.getInventory().addItem(toGive);
                            }
                            clicker.sendMessage("Item received!");
                            if(!shop.isInfinite() && shop.getBuffer() <= 0){
                                plugin.shops.remove(shop);
                            }
                            clicker.closeInventory();
                            plugin.setupandOpenShop(clicker);
                        }else{
                            clicker.sendMessage("You don't have enough money!");
                            e.setCancelled(true);
                        }
                    }
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("Sell an Item") || e.getView().getTitle().equals("Sell an Item(Infinite)")) {
            if (e.getSlot() == 7) {
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
            if (e.getSlot() == 8 && inv.getItem(0) != null) {

                ItemStack toSell = inv.getItem(0);
                ItemStack cost = inv.getItem(7);
                if(toSell == null || cost == null){
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
                if(e.getView().getTitle().equals("Sell an Item(Infinite)")){
                    plugin.shops.add(new Shop(inv.getItem(0), clicker, toSell.getAmount(), cost.getAmount(), -1));
                }else {
                    plugin.shops.add(new Shop(inv.getItem(0), clicker, toSell.getAmount(), cost.getAmount(), buffer));
                }
                clicker.sendMessage("Shop Created!");
                clicker.closeInventory();
                plugin.setupandOpenShop(clicker);
            }
        }
        if(e.getView().getTitle().equals("Remove Shops")){
            ItemStack stack = e.getInventory().getItem(e.getSlot());
            Shop shop = getShopFromItemStack(stack);
            if(shop != null && stack != null){
                plugin.shops.remove(shop);
                e.getWhoClicked().sendMessage("Shop Removed!");
                stack.setAmount(0);
                e.setCancelled(true);
            }
        }

    }

    private int getEmptyOrCompatibleSlot(Player player) {
        PlayerInventory playerInv = player.getInventory();
        for (int i = 0; i < playerInv.getSize(); i++) {
            ItemStack slotStack = playerInv.getItem(i);
            if (slotStack == null || slotStack.getType() == plugin.currency) {
                return i;
            }
        }
        return -1;
    }


    private Shop getShopFromItemStack(ItemStack stack) {
        for (Shop shop : plugin.shops) {
            if (shop.getItem().equals(stack)) {
                return shop;
            }
        }
        return null;
    }

    private ItemStack getMoney(Player player, ItemStack toRemove){

        for(int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack != null) {
                if (stack.getType() == toRemove.getType()) {
                    if (stack.getAmount() >= toRemove.getAmount()) {
                        return stack;
                    }
                }
            }
        }
        return null;
    }

}
