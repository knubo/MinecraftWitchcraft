package no.knubo.minecraft.witchcraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ElevatorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.getServer().getLogger().info("Command executed " + label + " " + args);

        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.chat("Elevator summoned");

            return true;
        }
        return false;
    }

}
