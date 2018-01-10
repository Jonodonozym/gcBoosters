
package jdz.gcBoosters.commands.admin;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.gcBoosters.scheduler.BoosterScheduler;

@CommandLabel("open")
@CommandPermission("booster.admin")
@CommandShortDescription("Opens the booster queue after a soft or hard stop")
class ABoosterOpenCommand  extends SubCommand {

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		if (BoosterScheduler.isOpen())
			sender.sendMessage(ChatColor.RED+"Booster queue is already open");
		BoosterScheduler.open();
	}
}
