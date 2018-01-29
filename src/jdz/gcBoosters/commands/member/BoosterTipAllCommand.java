
package jdz.gcBoosters.commands.member;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.Command;
import jdz.bukkitUtils.commands.annotations.CommandAsync;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.gcBoosters.GCBoosters;
import jdz.gcBoosters.data.QueuedBooster;
import lombok.Getter;

@CommandLabel("tipall")
@CommandAsync
public class BoosterTipAllCommand extends Command {
	@Getter private static final BoosterTipAllCommand instance = new BoosterTipAllCommand();

	private BoosterTipAllCommand() {
		register(GCBoosters.instance);
	}

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		boolean tipped = false;
		for (QueuedBooster b : BoosterTipCommand.activeBoosters.keySet())
			tipped &= BoosterTipCommand.getInstance().tip((Player) sender, b).getKey();
		
		if (BoosterTipCommand.activeBoosters.isEmpty())
			sender.sendMessage(ChatColor.RED+"There are no active boosters");
		else if (!tipped)
			sender.sendMessage(ChatColor.RED+"You have already tipped the owners of the active boosters, or they are offline, or tipping is disabled");
	}
}
