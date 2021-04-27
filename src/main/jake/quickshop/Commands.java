package main.jake.quickshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Commands implements CommandExecutor, Listener {

    public final String shop = "openshop";
    public final String adminShop = "adminshop";
    public final String removeShop = "removeshop";
    public final String currency = "currency";
    public final String deals = "deals";
    private QuickShop plugin;
    public final String[] COMMANDS = {shop, adminShop, removeShop, currency, deals};


    public Commands(QuickShop plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        switch (command.getName()){
            case shop:
                if(commandSender instanceof Player){
                    plugin.setupandOpenShop((Player) commandSender);
                }else{
                    commandSender.sendMessage("You must be a player to use this command");
                }
                return true;
            case adminShop:
                if(commandSender instanceof Player && commandSender.isOp() || commandSender instanceof Player && commandSender.hasPermission("simpleshop.adminshop")){
                    plugin.setupAndOpenAddItem((Player) commandSender, true);
                }else{
                    commandSender.sendMessage("You must be a player and op to use this command");
                }
                return true;
            case removeShop:
                if(commandSender instanceof Player){
                    plugin.setupRemoveShop((Player) commandSender);
                }else{
                    commandSender.sendMessage("You must be a player to use this command");
                }
                return true;
            case currency:
                if(strings.length == 0){
                    commandSender.sendMessage(ChatColor.RED + "Invalid use of command.");
                    return false;
                }
                if(strings[0].equals("set")) {
                    if(strings.length > 1) {
                        String toSet = strings[1];
                        Material matSet = Material.matchMaterial(toSet);
                        if (matSet != null) {
                            plugin.currency = matSet;
                            plugin.config.set("item", matSet.name());
                            plugin.saveConfig();
                            commandSender.getServer().broadcastMessage("[QuickShop] The shop currency has been changed to " + matSet.name() + "!");
                        } else {
                            commandSender.sendMessage("Material: " + toSet + " does not exist.");
                        }
                    }else{
                        commandSender.sendMessage("Please specify a currency.");
                    }
                }else if(strings[0].equals("info")){
                    commandSender.sendMessage("The current currency is: " + plugin.currency.name());
                }else{
                    commandSender.sendMessage(ChatColor.RED + "Invalid use of command.");
                    return false;
                }
            case deals:
                if(strings.length == 0){
                    commandSender.sendMessage(ChatColor.RED + "Invalid use of command.");
                    return false;
                }
                if(strings[0].equals("add") && strings.length > 2){
                    try{
                        Material m = Material.matchMaterial(strings[1]);
                        int cost = Integer.parseInt(strings[2]);
                        if (m != null) {
                            plugin.deals.put(m, cost);
                            plugin.configHandler.write();
                            commandSender.sendMessage("Deal: " + m.name() + " for " + cost + " added");
                        }else {
                            commandSender.sendMessage("Material: " + strings[1] + " does not exist.");
                        }
                    }catch (NumberFormatException e){
                        commandSender.sendMessage(ChatColor.RED + "You must specify the cost of the deal");
                        return false;
                    }
                }else if(strings[0].equals("info")){
                    commandSender.sendMessage("Current Deals:");
                    for(Material m : plugin.deals.keySet()){
                        commandSender.sendMessage(m.name() + ": Cost: " + plugin.deals.get(m));
                    }

                }
                return true;


//            case "test":
//                File testFile = new File("plugins/SimpleShop/test.txt");
//                File finalFile = new File("plugins/SimpleShop/final.txt");
//
//                try{
//                    Scanner scanner = new Scanner(testFile);
//                    FileWriter writer = new FileWriter(finalFile);
//
//                    while(scanner.hasNextLine()){
//                        String mat = scanner.nextLine().replace("\t", "").replace(" ", "");
//                        Material m = Material.getMaterial(mat);
//                        if(m != null)
//                            writer.write(m.name() + "\n");
//                        Log.info(mat + ":" + m);
//                    }
//                    writer.close();
//                }catch (IOException ignored){
//
//                }
//
//                return true;
        }
        return false;
    }
}
