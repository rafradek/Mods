package rafradek.minecraft2source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;
import rafradek.minecraft2source.Mark.MarkType;

public class CommandMarkProperty extends CommandBase implements IClientCommand  {

	String name;
	
	public CommandMarkProperty(String name) {
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
		return "commands.markkeyvalue.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		String[] joined = joinStrings(args);
		List<EntityMark> list = Minecraft2Source.entities;
		if (args.length % 2 == 1) {
			String name = args[0];
			for (int i = list.size()-1; i >= 0; i--) {
				EntityMark mark = list.get(i);
				if (mark.name.equals(name))
					this.setProperty(mark, Arrays.copyOfRange(joined,1,joined.length));
			}
		}
		else {
			for (int j = list.size()-1; j >= 0; j--) {
				EntityMark mark2 = list.get(j);
				for (int i = mark2.range.size() -1; i >= 0; i--) {
					BlockRange range2 = mark2.range.get(i);
					if (range2.intersects(Minecraft2Source.range)) {
						this.setProperty(mark2, joined);
					}
				}
			}
		}
	}
	
	public void setProperty(EntityMark mark, String[] keys) {
		if (keys.length == 0) {
			for (Entry<String, String> entry: mark.keyValues.entrySet()) {
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString(entry.getKey()+"="+entry.getValue()));
			}
			
		}
		for (int i =0; i < keys.length; i+=2) {
			mark.keyValues.put(keys[i], keys[i+1]);
		}
	}

	public static String[] joinStrings(String[] args) {
		ArrayList<String> build = new ArrayList<>();
		String joined = null;
		boolean open = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("\"")) {
				open = true;
				joined = "";
				args[i] = args[i].substring(1, args[i].length());
			}
			if (open) {
				joined = joined.concat(args[i]);
				if (args[i].endsWith("\"")) {
					joined = joined.substring(0, joined.length()-1);
					open = false;
					build.add(joined);
				}
				else {
					joined = joined.concat(" ");
				}
			}
			else {
				build.add(args[i]);
			}
			
			
		}
		return build.toArray(new String[build.size()]);
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
