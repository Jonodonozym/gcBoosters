
package jdz.gcBoosters.commands.member;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandAsync;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.gcBoosters.GCBoosters;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;
import net.md_5.bungee.api.ChatColor;

@CommandAsync
@CommandLabel("start")
@CommandLabel("activate")
@CommandShortDescription("activates a booster")
@CommandUsage("start [ID]")
@CommandRequiredArgs(1)
public class BoosterStartCommand extends SubCommand {

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		Booster booster = Booster.get(args[0]);
		if (booster == null) {
			sender.sendMessage(ChatColor.RED+"No boosters found called '"+args[0]+"'. Do /booster list to see your boosters");
			return;
		}
		Player player = (Player)sender;
		startBooster(sender, player, booster);
	}
	
	public static void startBooster(CommandSender sender, Player player, Booster booster) {
		if (BoosterDatabase.getInstance().isStopped()) {
			sender.sendMessage(ChatColor.RED+"Activating boosters has been temporarily disabled.");
			return;
		}
		if (!BoosterDatabase.getInstance().hasBooster(player, booster)) {
			sender.sendMessage(ChatColor.RED+"You don't have any of those boosters! Do /booster list to see your boosters");
			return;
		}
		if (BoosterDatabase.getInstance().isQueued(player, booster)) {
			sender.sendMessage(ChatColor.RED+"You already have a booster running in that queue.");
			return;
		}

		BoosterDatabase.getInstance().removeBooster(player, booster);
		BoosterDatabase.getInstance().queue(player, booster);

		Bukkit.getScheduler().runTaskAsynchronously(GCBoosters.instance, ()->{
			if (BoosterDatabase.getInstance().isRunning(booster.getQueue()))
				player.sendMessage(ChatColor.GREEN+"Your booster has been added to the queue!");
			else
				player.sendMessage(ChatColor.GREEN+"Booster activated! Please wait a minute for it to start.");
		});
	}
}
