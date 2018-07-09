
package jdz.gcBoosters.commands.member;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandAsync;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.misc.StringUtils;
import jdz.bukkitUtils.misc.utils.TimeUtils;
import jdz.gcBoosters.data.Booster;
import jdz.gcBoosters.data.BoosterDatabase;
import jdz.gcBoosters.data.QueuedBooster;

@CommandAsync
@CommandLabel("queue")
@CommandShortDescription("lists the boosters in the queues")
public class BoosterQueueCommand extends SubCommand {
	@Override
	public void execute(CommandSender sender, String... args) {
		List<String> lines = new ArrayList<String>();
		lines.add(ChatColor.GRAY+"============[ "+ChatColor.AQUA + "Queued Boosters "+ChatColor.GRAY + "]============");
		for (String queue: Booster.getQueues()) {
			List<QueuedBooster> boosters = BoosterDatabase.getInstance().getQueue(queue);
			String s = ChatColor.AQUA + "" + ChatColor.BOLD + queue.toUpperCase() + ": ";
			if (boosters.isEmpty()) {
				s += ChatColor.GRAY+"There are no queued boosters";
				lines.add(s);
			}
			else {
				int timeDiffMinutes = boosters.get(0).getBooster().getDuration() - (int)(System.currentTimeMillis() - boosters.get(0).getStartTime())/1000/60;
				lines.add(s+ChatColor.RESET+""+ChatColor.GREEN+boosters.get(0).getPlayer().getName()+"'s "+boosters.get(0).getBooster().getName()+", "+TimeUtils.timeFromMinutes(timeDiffMinutes)+" left");
				boolean first = true;
				for(QueuedBooster b: boosters) {
					if (first) {
						first = false;
						continue;
					}
					lines.add(ChatColor.GREEN+"   -"+b.getPlayer().getName()+"'s "+b.getBooster().getName());
				}
			}
		}
		lines.add(ChatColor.GRAY+StringUtils.repeat("=", lines.get(0).length()-8));
		sender.sendMessage(lines.toArray(new String[lines.size()]));
	}

}
