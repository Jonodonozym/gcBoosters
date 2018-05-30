
package jdz.gcBoosters.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandAsync;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;
import net.md_5.bungee.api.ChatColor;

@CommandLabel("remove")
@CommandRequiredArgs(2)
@CommandUsage("remove <boosterID> <player>")
@CommandShortDescription("Removes a specified booster from a player")
@CommandPermission("booster.admin")
@CommandAsync
class ABoosterRemoveCommand extends SubCommand {

	@CommandMethod
	public void removeBooster(CommandSender sender, String boosterID, OfflinePlayer target) {
		Booster booster = Booster.get(boosterID);
		if (booster == null) {
			sender.sendMessage(ChatColor.RED+"No booster found called '"+boosterID+"'");
			return;
		}
		if (!BoosterDatabase.getInstance().hasBooster(target, booster)) {
			sender.sendMessage(ChatColor.RED+target.getName()+" doesn't have any "+booster.getQueue()+" boosters");
			return;
		}
		BoosterDatabase.getInstance().removeBooster(target, booster);
		sender.sendMessage(ChatColor.GREEN + boosterID + " was taken from "+target.getName());
	}
}
