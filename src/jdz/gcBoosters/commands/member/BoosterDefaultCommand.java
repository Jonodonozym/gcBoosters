
package jdz.gcBoosters.commands.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.gcBoosters.gui.BoostersGUI;

@CommandLabel("DEFAULT")
@CommandUsage(" ")
@CommandShortDescription("Opens the boosters gui")
class BoosterDefaultCommand  extends SubCommand{

	@Override
	public void execute(CommandSender sender, String... args) {
		Player player = (Player)sender;
		new BoostersGUI(player).open(player);
	}
}
