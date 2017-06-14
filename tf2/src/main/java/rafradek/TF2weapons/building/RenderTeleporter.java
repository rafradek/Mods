package rafradek.TF2weapons.building;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import rafradek.TF2weapons.TF2weapons;

public class RenderTeleporter extends RenderLiving<EntityTeleporter> {

	private static final ResourceLocation TELEPORTER_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Teleporter.png");
	private static final ResourceLocation TELEPORTER_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Teleporter.png");

	public RenderTeleporter(RenderManager renderManager) {
		super(renderManager, new ModelTeleporter(), 0.6f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTeleporter par1EntityLiving) {
		// System.out.println("class: "+clazz);

		return par1EntityLiving.getEntTeam() == 0 ? TELEPORTER_RED : TELEPORTER_BLU;
	}

	@Override
	public void doRender(EntityTeleporter living, double x, double y, double z, float rot, float partial) {
		if (living.getSoundState() == 1)
			living.spinRender = living.spin
					+ ((float) Math.PI * (living.getLevel() == 1 ? 0.25f : (living.getLevel() == 2 ? 0.325f : 0.4f)))
							* partial;
		else
			living.spinRender = 0;

		super.doRender(living, x, y, z, rot, partial);

		if (living.getSoundState() == 1) {

			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer renderer = tessellator.getBuffer();
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x, (float) y + 0.23D, (float) z);
			// GL11.glRotatef((living.prevRotationYawHead +
			// (living.rotationYawHead - living.prevRotationYawHead) *
			// p_76986_9_)*-1, 0.0F, 1.0F, 0.0F);
			// GL11.glRotatef((living.prevRotationPitch + (living.rotationPitch
			// - living.prevRotationPitch) * p_76986_9_), 1.0F, 0.0F, 0.0F);
			// GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
			/*
			 * if(TF2weapons.getTeamForDisplay(living)==0){ GL11.glColor4f(1.0F,
			 * 0.0F, 0.0F, 0.28F); } else{ GL11.glColor4f(0.0F, 0.0F, 1.0F,
			 * 0.28F); }
			 */
			renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			renderer.pos(-0.5, 0.0D, 0.5).tex(0.75D, 0.5D).endVertex();
			renderer.pos(0.5, 0.0D, 0.5).tex(1D, 0.5D).endVertex();
			renderer.pos(0.5, 0.0D, -0.5).tex(1D, 0.0D).endVertex();
			renderer.pos(-0.5, 0.0D, -0.5).tex(0.75D, 0.0D).endVertex();
			tessellator.draw();

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1F);
			GL11.glDisable(GL11.GL_BLEND);
			// GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}
}
