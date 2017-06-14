package rafradek.blocklauncher;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RenderFallingBlock extends Render<EntityFallingEnchantedBlock> {

	// private final RenderBlocks sandRenderBlocks = new RenderBlocks();
	public RenderFallingBlock(RenderManager p_i46177_1_) {
		super(p_i46177_1_);
		this.shadowSize = 0.0F;
	}

	@Override
	public void doRender(EntityFallingEnchantedBlock entity, double par2, double par4, double par6, float par8,
			float par9) {
		World world = entity.world;
		IBlockState state = entity.block;
		Block block = entity.block.getBlock();
		BlockPos blockpos = new BlockPos(entity);
		if (world.getBlockState(blockpos).getBlock() != entity.block.getBlock())

			if (block != null) {
				this.bindEntityTexture(entity);
				float f2;
				Tessellator tessellator = Tessellator.getInstance();
				GL11.glPushMatrix();
				GL11.glTranslatef((float) par2, (float) par4, (float) par6);
				if (entity.isPrimed && entity.fuse - par9 + 1.0F < 10.0F) {
					f2 = 1.0F - (entity.fuse - par9 + 1.0F) / 10.0F;

					if (f2 < 0.0F)
						f2 = 0.0F;

					if (f2 > 1.0F)
						f2 = 1.0F;

					f2 *= f2;
					f2 *= f2;
					float f3 = entity.scale + f2 * 0.3F;
					GL11.glScalef(f3, f3, f3);
				} else
					GL11.glScalef(entity.scale, entity.scale, entity.scale);

				/*
				 * if(block instanceof BlockChest||block.getRenderType()==22){
				 * GL11.glTranslatef(0,0.5f,0); int a =
				 * entity.getBrightnessForRender(par9); int b = a % 65536; int c
				 * = a / 65536;
				 * OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.
				 * lightmapTexUnit, (float)b / 1.0F, (float)c / 1.0F);
				 * this.sandRenderBlocks.renderBlockAsItem(block, 0, 1.0F); }
				 * else
				 */if (state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
					GlStateManager.pushMatrix();
					GlStateManager.disableLighting();
					VertexBuffer worldrenderer = tessellator.getBuffer();
					worldrenderer.begin(7, DefaultVertexFormats.BLOCK);
					int i = blockpos.getX();
					int j = blockpos.getY();
					int k = blockpos.getZ();
					worldrenderer.setTranslation((-i) - 0.5F, (-j), (-k) - 0.5F);
					BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft()
							.getBlockRendererDispatcher();
					IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(state);
					blockrendererdispatcher.getBlockModelRenderer().renderModel(world, ibakedmodel, state, blockpos,
							worldrenderer, false);

					tessellator.draw();

					super.doRender(entity, par2, par4, par6, par8, par9);
					if (entity.isPrimed && entity.fuse / 5 % 2 == 0) {
						GlStateManager.translate(-0.5F, 0.0F, 0.5F);
						f2 = (1.0F - (entity.fuse - par9 + 1.0F) / 100.0F) * 0.8F;
						GlStateManager.disableTexture2D();
						;
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(770, 772);
						GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
						GlStateManager.doPolygonOffset(-3.0F, -3.0F);
						GlStateManager.enablePolygonOffset();
						blockrendererdispatcher.renderBlockBrightness(Blocks.TNT.getDefaultState(), 1.0F);
						GlStateManager.doPolygonOffset(0.0F, 0.0F);
						GlStateManager.disablePolygonOffset();
						GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
						GlStateManager.disableBlend();
						GlStateManager.enableTexture2D();
						;
					}
					worldrenderer.setTranslation(0, 0, 0);
					GlStateManager.enableLighting();
					GlStateManager.popMatrix();
					GL11.glEnable(GL11.GL_LIGHTING);
				}
				GL11.glPopMatrix();
			}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFallingEnchantedBlock entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}

}
