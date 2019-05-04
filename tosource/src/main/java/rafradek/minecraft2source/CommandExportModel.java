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
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.IClientCommand;
import rafradek.minecraft2source.MapBuilder.Model;

public class CommandExportModel extends CommandBase implements IClientCommand  {

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "exportmodel";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "commands.exportmodel.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		
		BlockPos pos = BlockPos.ORIGIN;
		float offX=0;
		float offY=0;
		float offZ=0;
		if (args.length == 0) {
			RayTraceResult raytrace = Minecraft.getMinecraft().player.rayTrace(250, 0);
			if (raytrace != null) {
				pos = raytrace.getBlockPos();
			}
		}
		else if (args.length == 3) {
			offX=Float.parseFloat(args[0]);
			offY=Float.parseFloat(args[1]);
			offZ=Float.parseFloat(args[2]);
		}
		else if (args.length == 6) {
			offX=Float.parseFloat(args[0]);
			offY=Float.parseFloat(args[1]);
			offZ=Float.parseFloat(args[2]);
			pos = new BlockPos(Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2]));
		}
		
		World world = sender.getEntityWorld();
		IBlockState state = world.getBlockState(pos).getActualState(world, pos);
		IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
		long rand = world.rand.nextLong();
		TextureAtlasSprite sprtest = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:items/brick");
		int twidth=(int) (sprtest.getIconWidth()/(sprtest.getMaxU()-sprtest.getMinU()));
		int theight=(int) (sprtest.getIconHeight()/(sprtest.getMaxV()-sprtest.getMinV()));
		float twidthsp=twidth;
		float theightsp=theight;
		
		List<AxisAlignedBB> cboxo= new ArrayList<>();
        List<AxisAlignedBB> cbox= new ArrayList<>();
        
    	state.addCollisionBoxToList(world, pos, new AxisAlignedBB(pos), cboxo, null, true);
    	for (AxisAlignedBB bbox : cboxo) {
    		cbox.add(bbox.offset(-pos.getX(), -pos.getY(), -pos.getZ()));
    	}
    	
		MapBuilder builder = new MapBuilder(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ());
		builder.buildModelCache(state, model, world, rand, pos, twidthsp, theightsp, new EnumMap<>(EnumFacing.class));
		Model modelinst =builder.readModel(state, cbox, new Vector3f(offX, offY, offZ));
		modelinst.name = args[3];
		builder.addModel(state, modelinst);
		
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
