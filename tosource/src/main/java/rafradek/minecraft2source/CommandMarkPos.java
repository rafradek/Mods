package rafradek.minecraft2source;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.IClientCommand;

public class CommandMarkPos extends CommandBase implements IClientCommand  {

	String name;
	boolean max;
	
	public CommandMarkPos(String name, boolean max) {
		super();
		this.name = name;
		this.max = max;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "commands.markpos.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		BlockPos pos = BlockPos.ORIGIN;
		if (args.length == 0) {
			RayTraceResult raytrace = Minecraft.getMinecraft().player.rayTrace(250, 0);
			if (raytrace != null) {
				pos = raytrace.getBlockPos();
			}
		}
		else if (args.length == 3)
			pos = new BlockPos(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
		if (this.max) {
			Minecraft2Source.sel1 = pos;
			if (Minecraft2Source.sel2 == null)
				Minecraft2Source.sel2 = pos;
		}
		else {
			Minecraft2Source.sel2 = pos;
			if (Minecraft2Source.sel1 == null)
				Minecraft2Source.sel1 = pos;
		}
		int minX = Math.min(Minecraft2Source.sel1.getX(), Minecraft2Source.sel2.getX());
		int minY = Math.min(Minecraft2Source.sel1.getY(), Minecraft2Source.sel2.getY());
		int minZ = Math.min(Minecraft2Source.sel1.getZ(), Minecraft2Source.sel2.getZ());
		int maxX = Math.max(Minecraft2Source.sel1.getX(), Minecraft2Source.sel2.getX());
		int maxY = Math.max(Minecraft2Source.sel1.getY(), Minecraft2Source.sel2.getY());
		int maxZ = Math.max(Minecraft2Source.sel1.getZ(), Minecraft2Source.sel2.getZ());
		Minecraft2Source.range = new BlockRange(minX, minY, minZ, maxX, maxY, maxZ);
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		// TODO Auto-generated method stub
		return true;
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
		RayTraceResult raytrace = Minecraft.getMinecraft().player.rayTrace(250, 0);
		if (raytrace != null) {
			BlockPos pos = raytrace.getBlockPos();
			switch (args.length) {
			case 1: return Lists.newArrayList(Integer.toString(pos.getX()));
			case 2: return Lists.newArrayList(Integer.toString(pos.getY()));
			case 3: return Lists.newArrayList(Integer.toString(pos.getZ()));
			default: return Collections.emptyList();
			}
		}
		else {
			BlockPos pos = Minecraft.getMinecraft().player.getPosition();
			switch (args.length) {
			case 1: return Lists.newArrayList(Integer.toString(pos.getX()));
			case 2: return Lists.newArrayList(Integer.toString(pos.getY()));
			case 3: return Lists.newArrayList(Integer.toString(pos.getZ()));
			default: return Collections.emptyList();
			}
		}
    }
}
