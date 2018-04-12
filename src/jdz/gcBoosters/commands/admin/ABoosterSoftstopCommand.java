
package jdz.gcBoosters.commands.admin;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandAsync;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.gcBoosters.data.BoosterDatabase;

@CommandLabel("softstop")
@CommandPermission("booster.admin")
@CommandShortDescription("Halts and redeems queued boosters, and disables further booster activation. Active boosters continue.")
@CommandAsync
class ABoosterSoftstopCommand  extends SubCommand {

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		if (BoosterDatabase.getInstance().isStopped())
			sender.sendMessage(ChatColor.RED+"Booster queue is already stopped");
		else {
			BoosterDatabase.getInstance().softStop();
			sender.sendMessage(ChatColor.GREEN+"Booster queue was cleared. All cleared boosters were redeemed.");
		}
	}
}