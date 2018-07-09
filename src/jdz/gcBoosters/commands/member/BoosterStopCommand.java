
package jdz.gcBoosters.commands.member;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandAsync;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;
import net.md_5.bungee.api.ChatColor;

@CommandAsync
@CommandLabel("stop")
@CommandLabel("cancel")
@CommandShortDescription("cancels a queued booster")
@CommandUsage("stop [ID]")
@CommandRequiredArgs(1)
public class BoosterStopCommand extends SubCommand {

	@Override
	public void execute(CommandSender sender, String... args) {
		Booster booster = Booster.get(args[0]);
		Player player = (Player)sender;
		if (booster == null) {
			sender.sendMessage(ChatColor.RED+"No boosters found called '"+args[0]+"'. Do /booster queue to see your boosters");
			return;
		}
		if (!BoosterDatabase.getInstance().isQueued(player, booster)) {
			sender.sendMessage(ChatColor.RED+"You don't have any queued "+args[0]+" boosters");
			return;
		}
		
		if (BoosterDatabase.getInstance().getQueue(booster.getQueue()).get(0).getStartTime() != Long.MAX_VALUE) {
			sender.sendMessage(ChatColor.RED+"You cannot cancel a booster that is already running");
			return;
		}
		
		BoosterDatabase.getInstance().dequeue(player, booster);
		BoosterDatabase.getInstance().addBooster(player, booster);
		sender.sendMessage(ChatColor.GREEN+"Booster removed from the queue.");
	}

}
