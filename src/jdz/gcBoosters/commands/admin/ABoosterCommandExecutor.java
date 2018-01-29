
package jdz.gcBoosters.commands.admin;

import java.util.Arrays;
import java.util.List;

import jdz.bukkitUtils.commands.CommandExecutor;
import jdz.bukkitUtils.commands.SubCommand;
import jdz.gcBoosters.GCBoosters;

public class ABoosterCommandExecutor extends CommandExecutor {

	private final List<SubCommand> subCommands = Arrays.asList(
			new ABoosterListCommand(),
			new ABoosterGiveCommand(),
			new ABoosterRemoveCommand(),
			new ABoosterCancelCommand(),
			new ABoosterSoftstopCommand(),
			new ABoosterHardstopCommand(),
			new ABoosterOpenCommand()
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
