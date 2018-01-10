
package jdz.gcBoosters.commands.admin;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.gcBoosters.scheduler.BoosterScheduler;

@CommandLabel("softstop")
@CommandPermission("booster.admin")
@CommandShortDescription("Halts and redeems queued boosters, and disables further booster activation. Active boosters continue.")
class ABoosterSoftstopCommand  extends SubCommand {

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		if (!BoosterScheduler.isOpen())
			sender.sendMessage(ChatColor.RED+"Booster queue is already soft-stopped");
		else {
			BoosterScheduler.softStop();
			sender.sendMessage(ChatColor.GREEN+"Booster queue was cleared. All cleared boosters were redeemed.");
		}
	}
}
