package rafradek.minecraft2source;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rafradek.minecraft2source.MapBuilder.Model;

public class CommandExportItem extends CommandBase implements IClientCommand  {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "exportitem";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "commands.exportitem.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		
		float scale = Minecraft2Source.blockSize;
		String prefixname = "";
		boolean allmodels = false;
		Vector3f offset = new Vector3f(-0.5f,0f,-0.5f);
		for (int i= 0; i < args.length;i++) {
    		if(args[i].equals("-s")) {
    			scale = Float.parseFloat(args[i+1]);
    		}
    		
    		if(args[i].equals("-p")) {
    			prefixname = args[i+1];
    		}
    		
    		if (args[i].equals("-all"))
    			allmodels = true;
    		
    		if (args[i].equals("-o"))
    			offset = new Vector3f(Float.parseFloat(args[i+1]), Float.parseFloat(args[i+2]), Float.parseFloat(args[i+3]));
    	}
    	
		World world = sender.getEntityWorld();
		
		ItemBuilder builder = new ItemBuilder();
		if(allmodels) {
			for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
				NonNullList<ItemStack> stacks = NonNullList.create();
				item.getSubItems(CreativeTabs.SEARCH, stacks);
				for (ItemStack stack: stacks) {
					builder.buildItem(stack, world, Minecraft.getMinecraft().player, offset, scale);
				}
			}
		}
		else {
			ItemStack stack = Minecraft.getMinecraft().player.getHeldItemMainhand();
			builder.buildItem(stack, world, Minecraft.getMinecraft().player,offset, scale);
		}
    	
		
		
		
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
