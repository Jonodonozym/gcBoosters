
package jdz.gcBoosters.commands.admin;

import java.util.Arrays;
import java.util.List;

import jdz.bukkitUtils.commands.CommandExecutor;
import jdz.bukkitUtils.commands.SubCommand;
import jdz.gcBoosters.GCBoosters;

public class ABoosterCommandExecutor extends CommandExecutor {

	private final List<SubCommand> subCommands = Arrays.asList(
			new ABoosterAddCommand(),
			new ABoosterCancelCommand(),
			new ABoosterHardstopCommand(),
			new ABoosterListCommand(),
			new ABoosterOpenCommand(),
			new ABoosterRemoveCommand(),
			new ABoosterSoftstopCommand()
			);
	
	public ABoosterCommandExecutor(GCBoosters plugin) {
		super(plugin, "abooster", false);
		getHelpCommand().setPermissions("booster.admin");
	}

	@Override
	protected List<SubCommand> getSubCommands() {
		return subCommands;
	}

}
