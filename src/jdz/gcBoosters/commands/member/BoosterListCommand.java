
package jdz.gcBoosters.commands.member;

import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.misc.StringUtils;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;
import net.md_5.bungee.api.ChatColor;

@CommandLabel("list")
@CommandShortDescription("lists the boosters you currently have")
public class BoosterListCommand  extends SubCommand{

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		list(sender, (OfflinePlayer)sender);
	}
	
	public void list(CommandSender sender, OfflinePlayer target) {
		Map<Booster, Integer> boosters = BoosterDatabase.getInstance().getAllBoosters(target);
		
		String [] lines = new String[boosters.size()+2];
		if (sender.equals(target))
			lines[0] = ChatColor.GRAY+"========[ "+ChatColor.GOLD+"My boosters"+ChatColor.GRAY+" ]========";
		else
			lines[0] = ChatColor.GRAY+"========[ "+ChatColor.GOLD+target.getName()+"'s boosters"+ChatColor.GRAY+" ]========";
			
		int i = 1;
		for (Booster b: boosters.keySet())
			lines[i++] = ChatColor.GOLD+"-"+ChatColor.GREEN+" x"+boosters.get(b)+" "+b.getName();
		
		lines[i] = ChatColor.GRAY+StringUtils.repeat("=",lines[0].length()-8);
		
		sender.sendMessage(lines);
	}
}
