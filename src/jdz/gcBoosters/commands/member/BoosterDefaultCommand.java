
package jdz.gcBoosters.commands.member;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.gcBoosters.gui.BoostersGUI;

@CommandLabel("DEFAULT")
@CommandUsage(" ")
@CommandShortDescription("Opens the boosters gui")
public class BoosterDefaultCommand extends SubCommand {
	@CommandMethod
	public void openGui(Player player) {
		new BoostersGUI(player).open(player);
	}
}
