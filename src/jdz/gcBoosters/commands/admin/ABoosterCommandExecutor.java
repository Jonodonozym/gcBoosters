
package jdz.gcBoosters.commands.admin;

import java.util.Arrays;
import java.util.List;

import jdz.bukkitUtils.commands.CommandExecutor;
import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandExecutorPermission;
import jdz.gcBoosters.GCBoosters;

@CommandExecutorPermission("booster.admin")
public class ABoosterCommandExecutor extends CommandExecutor {

	private final List<SubCommand> subCommands = Arrays.asList(new ABoosterListCommand(), new ABoosterGiveCommand(),
			new ABoosterRemoveCommand(), new ABoosterCancelCommand(), new ABoosterSoftstopCommand(),
			new ABoosterHardstopCommand(), new ABoosterOpenCommand());

	public ABoosterCommandExecutor(GCBoosters plugin) {
		super(plugin, "abooster", false);
	}

	@Override
	protected List<SubCommand> getSubCommands() {
		return subCommands;
	}

}
