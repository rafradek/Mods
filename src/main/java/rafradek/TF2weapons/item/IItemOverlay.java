package rafradek.TF2weapons.item;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemOverlay {
	public boolean showInfoBox(ItemStack stack, EntityPlayer player);
	
	public String[] getInfoBoxLines(ItemStack stack, EntityPlayer player);
	
	public void drawOverlay(ItemStack stack, EntityPlayer player, Tessellator tessellator, BufferBuilder buffer, ScaledResolution resolution);
}
