package rafradek.TF2weapons.command;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.item.ItemToken;
import rafradek.TF2weapons.util.TF2Util;

public class CommandSetOnShoulder extends CommandBase {

	@Override
	public String getUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "commands.forceclass.usage";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "setshoulder";
	}
	
	public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		try {
			EntityPlayerMP player = args.length > 0 ? getPlayer(server, sender, args[1])
					: getCommandSenderAsPlayer(sender);
			Vec3d look = player.getLook(1).scale(64);
			RayTraceResult trace = TF2Util.pierce(player.world, player, player.posX, player.posY+player.getEyeHeight(), player.posZ, player.posX+look.x, player.posY+player.getEyeHeight()+look.y,  player.posZ+look.z, false, 0.03f, false).get(0);
			if (trace.entityHit != null) {
				/*NBTTagCompound tag = new NBTTagCompound();
				trace.entityHit.writeToNBTOptional(tag);
				player.addShoulderEntity(tag);*/
				trace.entityHit.startRiding(player, true);
			}
			else {
				player.removePassengers();
			}
			
		} catch (Exception e) {
			throw new WrongUsageException(getUsage(sender), new Object[0]);
		}
	}
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
		if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, ItemToken.CLASS_NAMES);
        }
		else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
        else
        {
            return Collections.emptyList();
        }
    }
}
