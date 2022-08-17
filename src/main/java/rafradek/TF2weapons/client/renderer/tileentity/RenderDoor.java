package rafradek.TF2weapons.client.renderer.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.block.BlockOverheadDoor;
import rafradek.TF2weapons.tileentity.TileEntityOverheadDoor;

public class RenderDoor extends TileEntitySpecialRenderer<TileEntityOverheadDoor> {

	public void render(TileEntityOverheadDoor te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
		BlockPos orig = te.getPos();
		if (!te.getWorld().isAirBlock(new BlockPos(orig.getX(), te.minBounds.getY(), orig.getZ())) || te.getWorld().getBlockState(orig).getBlock() != TF2weapons.blockOverheadDoor)
			return;
		IBlockState state = te.getWorld().getBlockState(orig).withProperty(BlockOverheadDoor.HOLDER, false).withProperty(BlockOverheadDoor.SLIDING, false);
		this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		float amountScrolled = te.amountScrolled - te.motion + partialTicks * te.motion;
		float off = amountScrolled - (int)amountScrolled;
		for(int i = 0; i <= amountScrolled; i++) {
			 GlStateManager.pushMatrix();
			 GlStateManager.disableLighting();
             Tessellator tessellator = Tessellator.getInstance();
             BufferBuilder bufferbuilder = tessellator.getBuffer();
             BlockPos blockpos = orig.down(i);
             //GlStateManager.translate(-blockpos.getX(), -blockpos.getY(), -blockpos.getZ());
            
             float yscale = 1;
             if (i == 0) {
            	 GlStateManager.scale(1f, off, 1f);
            	 yscale = 1/off;
             }
             GlStateManager.translate((float)x, (float)(y-i+1-off) * yscale, (float)z);
             bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
             
             bufferbuilder.setTranslation(-blockpos.getX(), -blockpos.getY(), -blockpos.getZ());
             BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
             blockrendererdispatcher.getBlockModelRenderer().renderModel(te.getWorld(), blockrendererdispatcher.getModelForState(state), state, blockpos, bufferbuilder, false, MathHelper.getPositionRandom(blockpos));
             tessellator.draw();
             bufferbuilder.setTranslation(0, 0, 0);
             GlStateManager.enableLighting();
             GlStateManager.popMatrix();
		}
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}
