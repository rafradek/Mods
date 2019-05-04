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

public class CommandMarkEntity extends CommandBase implements IClientCommand  {

	String name;
	
	public CommandMarkEntity(String name) {
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
		return "commands.markent.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length > 0) {
			String name = args[0];
			boolean join = false;
			boolean separate = false;
			boolean samename = false;
			for (int i = 2; i < args.length; i++) {
				if (args[i].equals("-j")) {
					join = true;
				}
				
				if (args[i].equals("-s")) {
					separate = true;
				}
				
				if (args[i].equals("-sn")) {
					samename = true;
				}
			}
			
			List<EntityMark> list = Minecraft2Source.entities;
			for (int j = list.size()-1; j >= 0; j--) {
				EntityMark mark2 = list.get(j);
				if (mark2.type == MarkType.ENTITY) {
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
			if (separate) {
				int id = 0;
				for (int x= range.minX; x <= range.maxX; x++) {
					for (int y= range.minY; y <= range.maxY; y++) {
						for (int z= range.minZ; z <= range.maxZ; z++) {
							id++;
							addMark(new BlockRange(x,y,z,x,y,z), join, samename? name : (name+id), args[1]);
						}
					}
				}
			}
			else {
				addMark(range, join, name, args[1]);
			}
		}
	}

	public void addMark(BlockRange range, boolean join, String name, String classname) {
		EntityMark mark = null;
		if (!join || !Minecraft2Source.entitiesMap.containsKey(name)) {
			mark = new EntityMark(name,classname);
			
			Minecraft2Source.entitiesMap.put(name, mark);
			Minecraft2Source.entities.add(mark);
		}
		else
			mark = (EntityMark) Minecraft2Source.entitiesMap.get(name);
		
		mark.classname = classname;
		
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
