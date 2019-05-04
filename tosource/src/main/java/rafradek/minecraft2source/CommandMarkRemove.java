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
import rafradek.minecraft2source.Mark.MarkType;

public class CommandMarkRemove extends CommandBase implements IClientCommand  {

	String name;
	
	public CommandMarkRemove(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "commands.markrem.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		boolean special = args.length > 0 && args[0].equalsIgnoreCase("visible");
		if (args.length > 0 && !special) {
			String name = args[0];
			for (int i = Minecraft2Source.entities.size()-1; i >= 0; i--) {
				Mark mark = Minecraft2Source.entities.get(i);
				if (mark.name.equals(name)) {
					Minecraft2Source.entities.remove(i);
				}
			}
			
			Minecraft2Source.entitiesMap.remove(name);
		}
		else {
			List<EntityMark> list = Minecraft2Source.entities;
			MarkType type = MarkType.ENTITY;
			if (special)
				type = MarkType.valueOf(args[0].toUpperCase());
			for (int j = list.size()-1; j >= 0; j--) {
				Mark mark2 = list.get(j);
				if (mark2.type == type) {
					for (int i = mark2.range.size() -1; i >= 0; i--) {
						BlockRange range2 = mark2.range.get(i);
						if (range2.contains(Minecraft2Source.range) || Minecraft2Source.range.contains(range2)) {
							mark2.range.remove(i);
							if (mark2.range.isEmpty()) {
								list.remove(j);
								Minecraft2Source.entitiesMap.remove(mark2.name);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		// TODO Auto-generated method stub
		return true;
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, Minecraft2Source.entitiesMap.keySet());
		else
			return Collections.emptyList();
    }
}
