
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

@CommandLabel("hardstop")
@CommandPermission("booster.admin")
@CommandShortDescription("Halts and redeems active and queued boosters, and disables further booster activation.")
@CommandAsync
class ABoosterHardstopCommand  extends SubCommand {

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		if (BoosterDatabase.getInstance().isHardStopped())
			sender.sendMessage(ChatColor.RED+"Booster queue is already hard-stopped");
		else {
			BoosterDatabase.getInstance().hardStop();
			sender.sendMessage(ChatColor.GREEN+"Active and queued Boosters were halted. All halted boosters were redeemed.");
		}
	}
}
