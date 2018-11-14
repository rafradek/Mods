package rafradek.TF2weapons.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message;

public class CommandResetWeapons extends CommandBase {

	@Override
	public String getUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "commands.resetitemdata.usage";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "resetitemdata";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		TF2weapons.loadWeapons();
		if(server.isDedicatedServer())
			TF2weapons.network.sendToAll(new TF2Message.WeaponDataMessage(TF2weapons.itemDataCompressed));
		notifyCommandListener(sender, this, "commands.resetitemdata.success");
	}

}
