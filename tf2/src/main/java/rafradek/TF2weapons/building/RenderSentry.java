package rafradek.TF2weapons.building;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import rafradek.TF2weapons.TF2weapons;

public class RenderSentry extends RenderLiving<EntitySentry> {

	private static final ResourceLocation SENTRY_RED = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/red/Sentry.png");
	private static final ResourceLocation SENTRY_BLU = new ResourceLocation(TF2weapons.MOD_ID,
			"textures/entity/tf2/blu/Sentry.png");
	public ModelBase level1;
	public ModelBase level2;
	public ModelBase level3;

	public RenderSentry(RenderManager renderManager) {
		super(renderManager, new ModelSentry(), 0.6f);
		level1 = this.mainModel;
		level2 = new ModelSentry2();
		level3 = new ModelSentry3();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySentry par1EntityLiving) {
		// System.out.println("class: "+clazz);

		return par1EntityLiving.getEntTeam() == 0 ? SENTRY_RED : SENTRY_BLU;
	}

	@Override
	public void doRender(EntitySentry living, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_,
			float p_76986_9_) {
		if (living.getLevel() == 1)
			this.mainModel = this.level1;
		else if (living.getLevel() == 2)
			this.mainModel = this.level2;
		else
			this.mainModel = this.level3;
		super.doRender(living, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder BufferBuilder = tessellator.getBuffer();
		if (living.isControlled()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(p_76986_2_, p_76986_4_, p_76986_6_);
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			// GL11.glEnable(GL11.GL_BLEND);
			// OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			if (TF2weapons.getTeamForDisplay(living) == 0)
				GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.23F);
			else
				GL11.glColor4f(0.0F, 0.0F, 1.0F, 0.23F);
			AxisAlignedBB boundingBox = living.getEntityBoundingBox().grow(0.3f, 0.3f, 0.3f);
			BufferBuilder.begin(7, DefaultVertexFormats.POSITION_NORMAL);
			BufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F)
					.endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
			BufferBuilder.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();

			Vec3d look = living.getLook(p_76986_9_).scale(200);
			RayTraceResult target = TF2weapons.pierce(living.world, living, living.posX,
					living.posY + living.getEyeHeight(), living.posZ, look.x + living.posX,
					look.y + living.posY + living.getEyeHeight(), look.z + living.posZ, false, 0.02f, false)
					.get(0);
			if (target != null) {
				GlStateManager.pushMatrix();

				double xDist = target.hitVec.x - (living.prevPosX + (living.posX - living.prevPosX) * p_76986_9_);
				double yDist = (target.hitVec.y)
						- (living.prevPosY + (living.posY - living.prevPosY) * p_76986_9_ + living.height / 2);
				double zDist = target.hitVec.z - (living.prevPosZ + (living.posZ - living.prevPosZ) * p_76986_9_);
				float f = MathHelper.sqrt(xDist * xDist + zDist * zDist);
				float fullDist = MathHelper.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
				GlStateManager.translate((float) p_76986_2_, (float) p_76986_4_ + living.getEyeHeight(),
						(float) p_76986_6_);
				GL11.glRotatef((float) (Math.atan2(xDist, zDist) * 180.0D / Math.PI), 0.0F, 1.0F, 0.0F);
				GL11.glRotatef((float) (Math.atan2(yDist, f) * 180.0D / Math.PI) * -1, 1.0F, 0.0F, 0.0F);
				BufferBuilder.begin(7, DefaultVertexFormats.POSITION);
				BufferBuilder.pos(-0.04, -0.04, 0).endVertex();
				BufferBuilder.pos(0.04, 0.04, 0).endVertex();
				BufferBuilder.pos(0.04, 0.04, fullDist).endVertex();
				BufferBuilder.pos(-0.04, -0.04, fullDist).endVertex();
				tessellator.draw();
				BufferBuilder.begin(7, DefaultVertexFormats.POSITION);
				BufferBuilder.pos(-0.04, -0.04, fullDist).endVertex();
				BufferBuilder.pos(0.04, 0.04, fullDist).endVertex();
				BufferBuilder.pos(0.04, 0.04, 0).endVertex();
				BufferBuilder.pos(-0.04, -0.04, 0).endVertex();
				tessellator.draw();
				BufferBuilder.begin(7, DefaultVertexFormats.POSITION);
				BufferBuilder.pos(0.04, -0.04, 0).endVertex();
				BufferBuilder.pos(-0.04, 0.04, 0).endVertex();
				BufferBuilder.pos(-0.04, 0.04, fullDist).endVertex();
				BufferBuilder.pos(0.04, -0.04, fullDist).endVertex();
				tessellator.draw();
				BufferBuilder.begin(7, DefaultVertexFormats.POSITION);
				BufferBuilder.pos(0.04, -0.04, fullDist).endVertex();
				BufferBuilder.pos(-0.04, 0.04, fullDist).endVertex();
				BufferBuilder.pos(-0.04, 0.04, 0).endVertex();
				BufferBuilder.pos(0.04, -0.04, 0).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();

		}
	}
}
