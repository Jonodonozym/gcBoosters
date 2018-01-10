
package jdz.gcBoosters.commands.admin;

import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.gcBoosters.commands.member.BoosterListCommand;

@CommandPermission("booster.admin")
class ABoosterListCommand extends SubCommand {
	private static final BoosterListCommand listCommand = new BoosterListCommand();
	
	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		tryParse("list", true, sender, flags, args);
	}

	@SuppressWarnings("unused")
	private void list(CommandSender sender, OfflinePlayer target) {
		listCommand.list(sender, target);
	}
}
