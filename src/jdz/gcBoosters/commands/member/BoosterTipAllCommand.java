
package jdz.gcBoosters.commands.member;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import jdz.bukkitUtils.commands.Command;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.gcBoosters.data.QueuedBooster;

@CommandLabel("tipall")
public class BoosterTipAllCommand extends Command implements Listener {
	private final BoosterTipCommand tipCommand = new BoosterTipCommand();

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		if (args.length == 0) {
			boolean tipped = false;
			for (QueuedBooster b : BoosterTipCommand.activeBoosters.keySet())
				tipped &= tipCommand.tip((Player) sender, b).getKey();
			
			if (BoosterTipCommand.activeBoosters.isEmpty())
				sender.sendMessage(ChatColor.RED+"There are no active boosters");
			else if (!tipped)
				sender.sendMessage(ChatColor.RED+"You have already tipped the owners of the active boosters, or they are offline, or tipping is disabled");
		}
	}
}
