package rafradek.minecraft2source;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.IClientCommand;

public class CommandBuildMap extends CommandBase implements IClientCommand {

	public String getName()
    {
        return "buildmap";
    }

    public String getUsage(ICommandSender sender)
    {
        return "commands.buildmap.usage";
    }

	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
    	
    	int xmin = Minecraft2Source.range.minX;
    	int ymin = Minecraft2Source.range.minY;
    	int zmin = Minecraft2Source.range.minZ;
    	int xsize = Minecraft2Source.range.maxX-xmin+1;
    	int ysize = Minecraft2Source.range.maxY-ymin+1;
    	int zsize = Minecraft2Source.range.maxZ-zmin+1;
    	
    	String worldname = Minecraft2Source.getWorldName(sender.getEntityWorld());
    	String manifestname = "";
    	String prefixname = "";
    	boolean saveworld = false;
    	boolean saveentity = false;
    	boolean nicename = false;
    	boolean onlymodels = false;
    	Vector3f offset = new Vector3f(0,0,0);
    	for (int i= 0; i < args.length;i++) {
    		if(args[i].equals("-m")) {
    			manifestname = args[i+1];
    		}
    		
    		if(args[i].equals("-p")) {
    			prefixname = args[i+1];
    		}
    		
    		if (args[i].equals("-w"))
    			saveworld =true;
    		
    		if (args[i].equals("-e"))
    			saveentity =true;
    		
    		if (args[i].equals("-nn"))
    			nicename =true;
    		
    		if (args[i].equals("-om"))
    			onlymodels =true;
    		
    		if (args[i].equals("-mo"))
    			offset = new Vector3f(Float.parseFloat(args[i+1]), Float.parseFloat(args[i+2]), Float.parseFloat(args[i+3]));
    	}
    	
    	try {
    		if (!prefixname.isEmpty()) {
    			worldname = prefixname+"_"+worldname;
    		}
    		if (!manifestname.isEmpty()) {
    			File manifest= new File(Minecraft2Source.mapOutput,manifestname+".vmm");
    			if (!manifest.exists()) {
    				FileWriter writer = new FileWriter(manifest);
    				KeyValueWriter keywriter = new KeyValueWriter(writer);
    				keywriter.startGroup("Info");
    				keywriter.keyValue("NextInternalID", "3");
    				keywriter.endGroup();
    				keywriter.startGroup("Maps");
    				keywriter.startGroup("VMF");
    				keywriter.keyValue("Name", "generated-"+worldname);
    				keywriter.keyValue("File", worldname+"_w.vmf");
    				keywriter.keyValue("InternalID", "1");
    				keywriter.keyValue("TopLevel", "1");
    				keywriter.endGroup();
    				keywriter.startGroup("VMF");
    				keywriter.keyValue("Name", "entity-"+worldname);
    				keywriter.keyValue("File", worldname+"_e.vmf");
    				keywriter.keyValue("InternalID", "2");
    				keywriter.endGroup();
    				keywriter.endGroup();
    				writer.flush();
    				writer.close();
    			}
    			File mapfile= new File(Minecraft2Source.mapOutput,manifestname+".vmf");
    			if (!mapfile.exists()) {
    				FileWriter writer = new FileWriter(mapfile);
    				KeyValueWriter keywriter = new KeyValueWriter(writer);
    				MapBuilder.writeMapInit(keywriter);
    				keywriter.endGroup();
    				keywriter.startGroup("entity");
    				keywriter.keyValue("id", 2);
    				keywriter.keyValue("classname", "func_instance");
    				keywriter.keyValue("file", manifestname+"/"+worldname+"_w.wmf");
    				keywriter.keyValue("fixup_style", 2);
    				keywriter.keyValue("origin", "0 0 0");
    				keywriter.endGroup();
    				keywriter.startGroup("entity");
    				keywriter.keyValue("id", 3);
    				keywriter.keyValue("classname", "func_instance");
    				keywriter.keyValue("file", manifestname+"/"+worldname+"_e.wmf");
    				keywriter.keyValue("fixup_style", 2);
    				keywriter.keyValue("origin", "0 0 0");
    				keywriter.endGroup();
    				keywriter.startGroup("cameras");
    				keywriter.keyValue("activecamera", -1);
    				keywriter.endGroup();
    				keywriter.startGroup("cordon");
    				keywriter.keyValue("mins", "(0 0 0)");
    				keywriter.keyValue("maxs", "("+zsize*Minecraft2Source.blockSize+" "+xsize*Minecraft2Source.blockSize+" "+ysize*Minecraft2Source.blockSize+")");
    				keywriter.keyValue("active", Minecraft2Source.boundaries == 1 ? 1 : 0);
    				keywriter.endGroup();
    				writer.flush();
    				writer.close();
    			}
    			worldname = manifestname+"/"+worldname;
    		}
			MapBuilder builder = new MapBuilder(xmin, ymin, zmin, xsize, ysize, zsize);
			builder.modelsOnly = onlymodels;
			builder.niceName = nicename;
			builder.offset = offset;
			builder.isManifest = !manifestname.isEmpty();
			builder.build(sender.getEntityWorld(), worldname, saveworld, saveentity);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		// TODO Auto-generated method stub
		return true;
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[] {"start", "stop"}) : Collections.emptyList();
    }
    
}
