package main.jake.quickshop;

import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class ItemStackHelper {

    public static int addItemsToInventory(Player player, ItemStack stack){

        PlayerInventory inv = player.getInventory();
        List<Integer> possibleSlots = new ArrayList<>();
        for(int i = 0; i < 36; i++){
            ItemStack s = inv.getItem(i);
            if(s != null){
                if(s.isSimilar(stack)){
                    //Log.info("similar slot at " + i);
                   possibleSlots.add(i);
                }
            }

        }


        for(int i : possibleSlots){
            ItemStack s = inv.getItem(i);
            if(s != null){
                if(s.getAmount() + stack.getAmount() > s.getMaxStackSize()){
                    int toAdd = s.getMaxStackSize() - s.getAmount();
                    s.setAmount(s.getAmount() + toAdd);
                    stack.setAmount(stack.getAmount() - toAdd);
                }else if(s.getAmount() + stack.getAmount() < s.getMaxStackSize()){
                    s.setAmount(s.getAmount() + stack.getAmount());
                    stack.setAmount(0);
                }else{
                    s.setAmount(s.getAmount() + stack.getAmount());
                    stack.setAmount(0);
                    return stack.getAmount();
                }
            }
        }

        for(int i = 0; i < 36; i++){
            ItemStack s = inv.getItem(i);
            if(s == null){
                inv.setItem(i, stack);
                stack.setAmount(0);
            }
        }

        return stack.getAmount();
    }


}
