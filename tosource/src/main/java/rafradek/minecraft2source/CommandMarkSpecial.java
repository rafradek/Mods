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

public class CommandMarkSpecial extends CommandBase implements IClientCommand  {

	String name;
	
	public CommandMarkSpecial(String name) {
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
		return "commands.markspecial.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length > 0) {
			MarkType type = MarkType.valueOf(args[0].toUpperCase());
			
			List<EntityMark> list = Minecraft2Source.entities;
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
			
			BlockRange range = Minecraft2Source.range;
				addMark(range, true, type);
		}
	}

	public void addMark(BlockRange range, boolean join, MarkType type) {
		EntityMark mark = null;
		String name = type.toString();
		if (!join || !Minecraft2Source.entitiesMap.containsKey(name)) {
			mark = new EntityMark(name, "");
			mark.type = type;
			Minecraft2Source.entities.add(mark);
			Minecraft2Source.entitiesMap.put(name, mark);
		}
		else
			mark = Minecraft2Source.entitiesMap.get(name);
		for (int i = mark.range.size() -1; i >= 0; i--) {
			BlockRange range2 = mark.range.get(i);
			if (range2.contains(range) || range.contains(range2)) {
				mark.range.remove(i);
			}
		}
		
		mark.range.add(range);
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
