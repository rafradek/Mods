package rafradek.TF2weapons;

import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.Chunk;
import rafradek.TF2weapons.message.TF2Message;

public class CommandResetWeapons extends CommandBase {

	@Override
	public String getUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "command.resetitemdata.usage";
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
		notifyCommandListener(sender, this, "command.resetitemdata.success");
	}

}
