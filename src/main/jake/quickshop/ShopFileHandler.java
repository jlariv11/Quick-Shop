package main.jake.quickshop;

import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ShopFileHandler {

    private QuickShop plugin;
    public final File shopfile = new File("plugins/QuickShop/shops.txt");

    public ShopFileHandler(QuickShop plugin) {
        this.plugin = plugin;
    }

    public void write(){
        try {
            FileWriter writer = new FileWriter(shopfile);
            for (Shop shop : plugin.shops) {
                if(shop.getSeller() == null){
                    writer.write(shop.getItem().getType().name() + "," + shop.getItem().getAmount() + "," +
                            "#none#" + "," + shop.getCost() + "," + shop.getAmountPerSell() + "," + shop.getBuffer() + "\n");
                }else {
                    writer.write(shop.getItem().getType().name() + "," + shop.getItem().getAmount() + "," +
                            shop.getSeller().getName() + "," + shop.getCost() + "," + shop.getAmountPerSell() + "," + shop.getBuffer() + "\n");
                }
            }
            writer.close();
        }catch (IOException e){
            Log.info("FILEEE");
        }
    }

    public List<Shop> read(){
        List<Shop> shops = new ArrayList<>();

        try{
            Scanner scan = new Scanner(shopfile);
            while(scan.hasNextLine()){
                String[] line = scan.nextLine().split(",");
                Material m = Material.getMaterial(line[0]);
                if(m != null) {
                    ItemStack stack = new ItemStack(m, Integer.parseInt(line[1]));
                    Player seller = line[2].equals("#none#") ? null : plugin.getServer().getPlayer(line[2]);
                    int cost = Integer.parseInt(line[3]);
                    int amountPerSell = Integer.parseInt(line[4]);
                    int buffer = Integer.parseInt(line[5]);
                    shops.add(new Shop(stack, seller, amountPerSell, cost, buffer));
                }

            }
        }catch (FileNotFoundException e){
            Log.info("FILEEE READDD");
        }

        return shops;
    }


}
