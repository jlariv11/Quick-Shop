package main.jake.quickshop;

import org.bukkit.ChatColor;
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
        if(!infinite) {
            buffer -= amountPerSell;
            seller.sendMessage("Someone has purchased your " + item.getType().name());
            int slot = getEmptyOrCompatibleSlot();
            PlayerInventory playerInv = seller.getInventory();
            if (slot == -1) {
                seller.sendMessage(ChatColor.RED + "Your inventory is full. Your payment has been dropped on the ground.");
                seller.getWorld().dropItem(seller.getLocation(), new ItemStack(plugin.currency, cost));
            }
            if (playerInv.getItem(slot) == null) {
                playerInv.setItem(slot, new ItemStack(plugin.currency, cost));
            } else {
                playerInv.addItem(new ItemStack(plugin.currency, cost));
            }
        }
    }

    private int getEmptyOrCompatibleSlot(){
        PlayerInventory playerInv = seller.getInventory();
        for(int i = 0; i < playerInv.getSize(); i++){
            ItemStack slotStack = playerInv.getItem(i);
            if(slotStack == null || slotStack.getType() == plugin.currency){
                return i;
            }
        }
        return -1;
    }

}
