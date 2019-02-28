
package jdz.gcBoosters.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
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
public class ABoosterListCommand extends SubCommand {
	private static final BoosterListCommand listCommand = new BoosterListCommand();

	@CommandMethod
	public void list(CommandSender sender, OfflinePlayer target) {
		listCommand.list(sender, target);
	}
}
