
package jdz.gcBoosters.commands.member;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javafx.util.Pair;
import jdz.bukkitUtils.commands.Command;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.gcBoosters.GCBoosters;
import jdz.gcBoosters.data.QueuedBooster;
import jdz.gcBoosters.event.BoosterEndEvent;
import jdz.gcBoosters.event.BoosterStartEvent;

@CommandLabel("tip")
@CommandRequiredArgs(2)
@CommandUsage("/tip <player> <boosterID>")
public class BoosterTipCommand extends Command implements Listener {
	static Map<QueuedBooster, Set<CommandSender>> activeBoosters;

	BoosterTipCommand() {
		if (activeBoosters == null) {
			activeBoosters = new HashMap<QueuedBooster, Set<CommandSender>>();
			Bukkit.getPluginManager().registerEvents(this, GCBoosters.instance);
		}
	}

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		for (QueuedBooster b: activeBoosters.keySet()) {
			if (b.getPlayer().getName().equalsIgnoreCase(args[0]) && b.getBooster().getID().equalsIgnoreCase(args[1])) {
				Pair<Boolean, String> result = tip((Player)sender, b);
				if (!result.getKey())
					sender.sendMessage(result.getValue());
				return;
			}
		}
		
	}

	public Pair<Boolean, String> tip(Player tipper, QueuedBooster b) {
		if (!b.getBooster().isTipping())
			return new Pair<Boolean, String>(false, ChatColor.RED+"Tipping is disabled for this booster");

		if (!b.getBooster().isOfflineTipping() && !b.getPlayer().isOnline())
			return new Pair<Boolean, String>(false, ChatColor.RED+"You cannot tip while "+b.getPlayer().getName()+" is offline");

		if (activeBoosters.get(b).contains(tipper))
			return new Pair<Boolean, String>(false, ChatColor.RED+"You have already tipped this player");

		activeBoosters.get(b).add(tipper);

		for (String command : b.getBooster().getTipReward())
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", tipper.getName()));
		for (String command : b.getBooster().getTippedReward())
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", b.getPlayer().getName()));

		for (String message : b.getBooster().getTipMessages())
			tipper.sendMessage(withPlaceholders(tipper, b, message));
		if (b.getPlayer().isOnline())
			for (String message : b.getBooster().getTippedMessages())
				b.getPlayer().getPlayer().sendMessage(withPlaceholders(tipper, b, message));

		return new Pair<Boolean, String>(true, "");
	}

	private String withPlaceholders(Player tipper, QueuedBooster b, String message) {
		String s = message;
		s = s.replaceAll("%boosterName%", b.getBooster().getName());
		s = s.replaceAll("%tippedPlayer%", b.getPlayer().getName());
		s = s.replaceAll("%tippingPlayer%", tipper.getName());
		return s;
	}

	@EventHandler
	public void onBoosterStart(BoosterStartEvent event) {
		activeBoosters.put(new QueuedBooster(event.getBooster(), event.getPlayer()), new HashSet<CommandSender>());
	}

	@EventHandler
	public void onBoosterStart(BoosterEndEvent event) {
		activeBoosters.remove(new QueuedBooster(event.getBooster(), event.getPlayer()));
	}
}
