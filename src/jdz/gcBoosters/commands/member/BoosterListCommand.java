
package jdz.gcBoosters.commands.member;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.misc.StringUtils;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;
import net.md_5.bungee.api.ChatColor;

@CommandLabel("list")
@CommandShortDescription("lists the boosters you currently have")
public class BoosterListCommand extends SubCommand {

	@CommandMethod
	public void list(Player player) {
		list(player, player);
	}

	public void list(CommandSender sender, OfflinePlayer target) {

		Map<Booster, Integer> boosters = BoosterDatabase.getInstance().getAllBoosters(target);

		Map<Booster, Integer> zeroRemovedBoosters = new HashMap<>();
		for (Booster b : boosters.keySet())
			if (boosters.get(b) != 0)
				zeroRemovedBoosters.put(b, boosters.get(b));

		if (zeroRemovedBoosters.isEmpty()) {
			if (target.isOnline() && sender.equals(target.getPlayer()))
				sender.sendMessage(ChatColor.RED + "You don't have any boosters");
			else
				sender.sendMessage(ChatColor.RED + target.getName() + " doesn't have any boosters");
			return;
		}

		String[] lines = new String[zeroRemovedBoosters.size() + 2];
		if (target.isOnline() && sender.equals(target.getPlayer()))
			lines[0] = ChatColor.GRAY + "========[ " + ChatColor.GOLD + "My boosters" + ChatColor.GRAY + " ]========";
		else
			lines[0] = ChatColor.GRAY + "========[ " + ChatColor.GOLD + target.getName() + "'s boosters"
					+ ChatColor.GRAY + " ]========";

		int i = 1;
		for (Booster b : zeroRemovedBoosters.keySet())
			lines[i++] = ChatColor.GOLD + "-" + ChatColor.GREEN + " x" + zeroRemovedBoosters.get(b) + " " + b.getName()
					+ ChatColor.GRAY + " [" + b.getID() + "]";

		lines[i] = ChatColor.GRAY + StringUtils.repeat("=", lines[0].length() - 8);

		sender.sendMessage(lines);
	}
}
