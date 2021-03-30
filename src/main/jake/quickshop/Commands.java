package main.jake.quickshop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Commands implements CommandExecutor, Listener {

    public final String shop = "openshop";
    public final String adminShop = "adminshop";
    public final String removeShop = "removeshop";
    public final String currency = "currency";
    private QuickShop plugin;

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
