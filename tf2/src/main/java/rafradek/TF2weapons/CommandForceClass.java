package rafradek.TF2weapons;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import rafradek.TF2weapons.characters.ItemToken;
import rafradek.TF2weapons.weapons.WeaponsCapability;

public class CommandForceClass extends CommandBase {

	@Override
	public String getUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "commands.forceclass.usage";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "forceclass";
	}
	
	public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1)
			throw new WrongUsageException(getUsage(sender), new Object[0]);
		try {
			EntityPlayerMP player = args.length > 1 ? getPlayer(server, sender, args[1])
					: getCommandSenderAsPlayer(sender);
			if (args[0].equals("clear")) {
				((ItemToken)TF2weapons.itemToken).updateAttributes(ItemStack.EMPTY, player);
				WeaponsCapability.get(player).forcedClass = false;
				notifyCommandListener(sender, this, "commands.forceclass.success2", new Object[] {player.getName()});
			}
			else {
				int clazz = Integer.parseInt(args[0]); 
				((ItemToken)TF2weapons.itemToken).updateAttributes(new ItemStack(TF2weapons.itemToken, 1, clazz), player);
				WeaponsCapability.get(player).forcedClass = true;
				notifyCommandListener(sender, this, "commands.forceclass.success", new Object[] {player.getName(), ItemToken.CLASS_NAMES[clazz]});
			}
			
			
		} catch (Exception e) {
			throw new WrongUsageException(getUsage(sender), new Object[0]);
		}
	}
}
