
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
import jdz.gcBoosters.data.BoosterDatabase;
import net.md_5.bungee.api.ChatColor;

@CommandLabel("give")
@CommandLabel("add")
@CommandRequiredArgs(2)
@CommandUsage("give <boosterID> <player>")
@CommandShortDescription("Gives a player the specified booster")
@CommandPermission("booster.admin")
class ABoosterGiveCommand  extends SubCommand {

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		tryParse("addBooster", true, sender, flags, args);
	}
	
	public void addBooster(CommandSender sender, String boosterID, OfflinePlayer target) {
		Booster booster = Booster.get(boosterID);
		if (booster == null) {
			sender.sendMessage(ChatColor.RED+"No booster found called '"+boosterID+"'");
			return;
		}
		BoosterDatabase.getInstance().addBooster(target, booster);
		sender.sendMessage(ChatColor.GREEN+target.getName()+" was given a "+boosterID+" booster!");
	}
}
