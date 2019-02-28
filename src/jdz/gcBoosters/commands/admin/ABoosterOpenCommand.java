
package jdz.gcBoosters.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.gcBoosters.data.BoosterDatabase;

@CommandLabel("open")
@CommandPermission("booster.admin")
@CommandShortDescription("Opens the booster queue after a soft or hard stop")
public class ABoosterOpenCommand extends SubCommand {

	@Override
	public void execute(CommandSender sender, String... args) {
		if (!BoosterDatabase.getInstance().isStopped())
			sender.sendMessage(ChatColor.RED + "Booster queue is already open");
		else {
			BoosterDatabase.getInstance().open();
			sender.sendMessage(ChatColor.GREEN + "Booster queue opened");
		}
	}
}
