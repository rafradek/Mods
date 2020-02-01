package rafradek.TF2weapons.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.client.gui.TF2GuiConfig;
import rafradek.TF2weapons.entity.EntityPickup;
import rafradek.TF2weapons.entity.mercenary.InvasionEvent;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.tileentity.IEntityConfigurable;
import rafradek.TF2weapons.util.TF2Util;

public class ItemConfigure extends Item {

	public ItemConfigure() {
		this.setUnlocalizedName("configurator");
	}
	
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if (!worldIn.isRemote) {
			TileEntity ent = worldIn.getTileEntity(pos);
			if (ent instanceof IEntityConfigurable) {
				//FMLNetworkHandler.openGui(player, TF2weapons.instance, 7, worldIn, pos.getX(),
				//		pos.getY(), pos.getZ());
				TF2weapons.network.sendTo(new TF2Message.GuiConfigMessage(((IEntityConfigurable)ent).writeConfig(new NBTTagCompound()), pos), (EntityPlayerMP) player);
			}
			else {
				BlockPos forwardpos = pos.offset(facing);
				for (EntityPickup pickup: worldIn.getEntitiesWithinAABB(EntityPickup.class, new AxisAlignedBB(forwardpos))) {
					pickup.setDead();
				}
			}
		}
		else {
			TileEntity ent = worldIn.getTileEntity(pos);
			if (!(ent instanceof IEntityConfigurable)) {
				GuiScreen.setClipboardString(pos.getX()+" "+pos.getY()+" "+pos.getZ());
				player.sendMessage(new TextComponentString("Copied coordinates to clipboard: "+GuiScreen.getClipboardString()));
			}
		}
        return EnumActionResult.SUCCESS;
    }
	
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
		if (worldIn.isRemote) {
			RayTraceResult ray= worldIn.rayTraceBlocks(playerIn.getPositionEyes(1f), playerIn.getPositionEyes(1f).add(playerIn.getLookVec().scale(256)), false);
			if (ray !=null && ray.getBlockPos() != null) {
				BlockPos pos = ray.getBlockPos();
				TileEntity ent = worldIn.getTileEntity(pos);
				if (!(ent instanceof IEntityConfigurable)) {
					GuiScreen.setClipboardString(pos.getX()+" "+pos.getY()+" "+pos.getZ());
					playerIn.sendMessage(new TextComponentString("Copied coordinates to clipboard: "+GuiScreen.getClipboardString()));
				}
			}
		}
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
}
