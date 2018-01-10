
package jdz.gcBoosters.commands.admin;

import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.scheduler.BoosterScheduler;
import net.md_5.bungee.api.ChatColor;

@CommandLabel("cancel")
@CommandPermission("booster.admin")
@CommandShortDescription("Remove the booster from the queue and returns it to the player")
@CommandUsage("/abooster cancel <boosterID> <player>")
@CommandRequiredArgs(2)
class ABoosterCancelCommand  extends SubCommand {

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		tryParse("cancel", true, sender, flags, args);
	}
	
	@SuppressWarnings("unused")
	private void cancel(CommandSender sender, String boosterID, OfflinePlayer player) {
		Booster booster = Booster.get(boosterID);
		if (booster == null) {
			sender.sendMessage(ChatColor.RED+"No booster found called '"+boosterID+"'");
			return;
		}
		if (BoosterScheduler.dequeue(player, booster))
			sender.sendMessage(ChatColor.GREEN+player.getName()+"'s "+booster.getName()+" booster was de-queued and redeemed");
		else
			sender.sendMessage(ChatColor.RED+player.getName()+" has no "+booster.getName()+" booster queued");
	}
}
