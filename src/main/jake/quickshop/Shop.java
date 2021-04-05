package main.jake.quickshop;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Shop {

    private ItemStack item;
    private Player seller;
    private int amountPerSell;
    private int cost;
    private int buffer;
    private boolean infinite;
    private QuickShop plugin = QuickShop.getPlugin(QuickShop.class);

    public Shop(ItemStack item, Player seller, int amountPerSell, int cost, int buffer) {
        this.item = item;
        this.seller = seller;
        this.amountPerSell = amountPerSell;
        this.cost = cost;
        this.buffer = buffer;
        this.infinite = buffer == -1;
    }

    public ItemStack getItem() {
        return item;
    }

    public Player getSeller() {
        return seller;
    }

    public int getAmountPerSell() {
        return amountPerSell;
    }

    public int getCost() {
        return cost;
    }

    public int getBuffer() {
        return buffer;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void sell(){
        if(seller == null) {
            buffer -= amountPerSell;
        }
        if(!infinite && seller != null) {
            buffer -= amountPerSell;
            seller.sendMessage("Someone has purchased your " + item.getType().name().replace("_",  " "));
            int remainder = ItemStackHelper.addItemsToInventory(seller, new ItemStack(plugin.currency, cost));
            if (remainder > 0) {
                seller.sendMessage(ChatColor.RED + "Your inventory is full. Your payment has been dropped on the ground.");
                seller.getWorld().dropItem(seller.getLocation(), new ItemStack(plugin.currency, remainder));
            }

        }
    }

}
