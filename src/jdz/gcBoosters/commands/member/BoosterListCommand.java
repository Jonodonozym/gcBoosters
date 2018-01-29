
package jdz.gcBoosters.commands.member;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandAsync;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.misc.StringUtils;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;
import net.md_5.bungee.api.ChatColor;

@CommandLabel("list")
@CommandShortDescription("lists the boosters you currently have")
@CommandAsync
public class BoosterListCommand  extends SubCommand{

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		list(sender, (OfflinePlayer)sender);
	}
	
	public void list(CommandSender sender, OfflinePlayer target) {
		Map<Booster, Integer> boosters = BoosterDatabase.getInstance().getAllBoosters(target);
		
		Map<Booster, Integer> zeroRemovedBoosters = new HashMap<Booster, Integer>();
		for (Booster b: boosters.keySet())
			if (boosters.get(b) != 0)
				zeroRemovedBoosters.put(b, boosters.get(b));
		
		if (zeroRemovedBoosters.isEmpty()) {
			if (sender.equals(target))
				sender.sendMessage(ChatColor.RED+"You don't have any boosters");
			else
				sender.sendMessage(ChatColor.RED+target.getName()+" doesn't have any boosters");
			return;
		}
		
		String [] lines = new String[zeroRemovedBoosters.size()+2];
		if (sender.equals(target))
			lines[0] = ChatColor.GRAY+"========[ "+ChatColor.GOLD+"My boosters"+ChatColor.GRAY+" ]========";
		else
			lines[0] = ChatColor.GRAY+"========[ "+ChatColor.GOLD+target.getName()+"'s boosters"+ChatColor.GRAY+" ]========";
			
		int i = 1;
		for (Booster b: zeroRemovedBoosters.keySet())
			lines[i++] = ChatColor.GOLD+"-"+ChatColor.GREEN+" x"+zeroRemovedBoosters.get(b)+" "+b.getName() + ChatColor.GRAY+ " ["+b.getID()+"]";
		
		lines[i] = ChatColor.GRAY+StringUtils.repeat("=",lines[0].length()-8);
		
		sender.sendMessage(lines);
	}
}
