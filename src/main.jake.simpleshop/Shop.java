package main.jake.simpleshop;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Shop {

    private ItemStack item;
    private Player seller;
    private int amountPerSell;
    private int cost;
    private int buffer;

    public Shop(ItemStack item, Player seller, int amountPerSell, int cost, int buffer) {
        this.item = item;
        this.seller = seller;
        this.amountPerSell = amountPerSell;
        this.cost = cost;
        this.buffer = buffer;
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
}
