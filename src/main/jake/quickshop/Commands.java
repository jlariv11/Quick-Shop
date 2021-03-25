package main.jake.quickshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Commands implements CommandExecutor, Listener {

    public final String shop = "openshop";
    public final String adminShop = "adminshop";
    public final String removeShop = "removeshop";
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
