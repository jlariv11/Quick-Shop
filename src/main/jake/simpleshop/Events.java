package main.jake.simpleshop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {

    private SimpleShop plugin;

    public Events(SimpleShop plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void invClick(InventoryClickEvent e){
        Player clicker = (Player) e.getWhoClicked();
        ClickType type = e.getClick();
        Inventory inv = e.getClickedInventory();


        if(inv == null){
            return;
        }

        if(e.getView().getTitle().equals("Shop")){
            if(e.getSlot() == 8){
                clicker.closeInventory();
                plugin.setupAndOpenAddItem(clicker);
            }
            return;
        }
        if(e.getView().getTitle().equals("Sell an Item")){
            if(e.getSlot() == 7){
                ItemStack item = inv.getItem(7);
                if(item != null) {
                    if(type == ClickType.LEFT) {
                        if(item.getAmount() + 1 > 64){
                            return;
                        }
                        item.setAmount(item.getAmount() + 1);
                    }else if(type == ClickType.SHIFT_LEFT){
                        if(item.getAmount() + 10 > 64){
                            item.setAmount(64);
                            return;
                        }
                        item.setAmount(item.getAmount() + 10);
                    }else if(type == ClickType.RIGHT){
                        if(item.getAmount() - 1 < 1){
                            return;
                        }
                        item.setAmount(item.getAmount() - 1);
                    }else if(type == ClickType.SHIFT_RIGHT){
                        if(item.getAmount() - 10 < 1){
                            item.setAmount(1);
                            return;
                        }
                        item.setAmount(item.getAmount() - 10);
                    }
                }
            }
            if(e.getSlot() == 8 && inv.getItem(0) != null){
                int buffer = 0;
                for(int i = 0; i < 6; i++){
                    ItemStack s = inv.getItem(i);
                    if(s != null){
                        if(s.getData().getItemType() == inv.getItem(0).getData().getItemType()) {
                            buffer += s.getAmount();
                        }else{
                            //drop item on ground
                        }
                    }
                }
                plugin.shops.add(new Shop(inv.getItem(0), clicker, inv.getItem(0).getAmount(), inv.getItem(7).getAmount(), buffer));
                clicker.sendMessage("Shop Created!");
                clicker.closeInventory();
                plugin.setupandOpenShop(clicker);
            }
        }

    }

}
