package main.jake.simpleshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class Commands implements CommandExecutor, Listener {

    public final String shop = "openshop";
    private SimpleShop plugin;

    public Commands(SimpleShop plugin) {
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
        }
        return false;
    }
}
