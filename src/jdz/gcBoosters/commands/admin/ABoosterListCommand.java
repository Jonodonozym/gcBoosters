
package jdz.gcBoosters.commands.admin;

import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandAsync;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.gcBoosters.commands.member.BoosterListCommand;

@CommandLabel("list")
@CommandRequiredArgs(1)
@CommandPermission("booster.admin")
@CommandUsage("list [player]")
@CommandShortDescription("Lists the boosters a player has")
@CommandAsync
class ABoosterListCommand extends SubCommand {
	private static final BoosterListCommand listCommand = new BoosterListCommand();
	
	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		tryParse("list", true, sender, flags, args);
	}

	public void list(CommandSender sender, OfflinePlayer target) {
		listCommand.list(sender, target);
	}
}
