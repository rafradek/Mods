package rafradek.TF2weapons.weapons;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2EventsClient;
import rafradek.TF2weapons.TF2Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityBulletTracer extends Particle {

	private int duration;
	private boolean nextDead;
	private boolean special;

	public EntityBulletTracer(World par1World, double startX, double startY, double startZ, double x, double y,
			double z, int duration, int crits, EntityLivingBase shooter,boolean special) {
		super(par1World, startX, startY, startZ);
		this.particleScale = 0.2f;
		this.duration = duration;
		this.special = special;
		
		this.motionX = (x - startX) / duration;
		this.motionY = (y - startY) / duration;
		this.motionZ = (z - startZ) / duration;
		
		if(special) {
			crits=2;
			this.motionX *= 0.001;
			this.motionY *= 0.001;
			this.motionZ *= 0.001;
		}
		this.particleMaxAge = 200;
		this.setSize(0.025f, 0.025f);
		// this.setParticleIcon(Item.itemsList[2498+256].getIconFromDamage(0));
		this.setParticleTexture(TF2EventsClient.pelletIcon);
		// this.setParticleTextureIndex(81);
		this.multipleParticleScaleBy(2);

		// TODO Auto-generated constructor stub
		if (crits != 2)
			this.setRBGColorF(0.97f, 0.76f, 0.51f);
		else {
			int color = TF2Util.getTeamColor(shooter);
			this.setRBGColorF(MathHelper.clamp((color >> 16) / 255f, 0.2f, 1f), 
					MathHelper.clamp((color >> 8 & 255) / 255f, 0.2f, 1f), MathHelper.clamp((color & 255) / 255f, 0.2f, 1f));
		}
	}

	@Override
	public void onUpdate() {
		if (nextDead)
			this.setExpired();
		if (this.world.rayTraceBlocks(new Vec3d(posX, posY, posZ),
				new Vec3d(posX + motionX, posY + motionY, posZ + motionZ)) != null)
			nextDead = true;
		// this.setVelocity(0, 0, 0);
		super.onUpdate();
		this.motionX *= 1.025D;
		this.motionY *= 1.025D;
		this.motionZ *= 1.025D;
		if (duration > 0) {
			duration--;
			if (duration == 0)
				this.setExpired();
		}
	}

	@Override
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX,
			float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {

		float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
		float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
		float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);
		Vec3d rightVec = new Vec3d(this.motionX, this.motionY, this.motionZ)
				.crossProduct(Minecraft.getMinecraft().getRenderViewEntity().getLook(1)).normalize();
		// System.out.println(rightVec);
		float f4 = 0.1F * this.particleScale;

		int i = this.getBrightnessForRender(partialTicks);
		int j = i >> 16 & 65535;
		int k = i & 65535;

		float xNext = (float) (x + this.motionX * 2 * (special?10000:1));
		float yNext = (float) (y + this.motionY * 2 * (special?10000:1));
		float zNext = (float) (z + this.motionZ * 2 * (special?10000:1));

		float xMin = this.particleTexture.getMinU();
		float xMax = this.particleTexture.getMaxU();
		float yMin = this.particleTexture.getMinV();
		float yMax = this.particleTexture.getMaxV();

		worldRendererIn.pos(x - rightVec.x * f4, y - rightVec.y * f4, z - rightVec.z * f4)
				.tex(xMax, yMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
				.lightmap(j, k).endVertex();
		;
		worldRendererIn.pos(x + rightVec.x * f4, y + rightVec.y * f4, z + rightVec.z * f4)
				.tex(xMax, yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
				.lightmap(j, k).endVertex();
		;
		worldRendererIn.pos(xNext + rightVec.x * f4, yNext + rightVec.y * f4, zNext + rightVec.z * f4)
				.tex(xMin, yMin).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
				.lightmap(j, k).endVertex();
		;
		worldRendererIn.pos(xNext - rightVec.x * f4, yNext - rightVec.y * f4, zNext - rightVec.z * f4)
				.tex(xMin, yMax).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)
				.lightmap(j, k).endVertex();
		;

		/*
		 * worldRendererIn.pos((double)(x + rightVec.x * f4),
		 * (double)(y+rightVec.y*f4),
		 * (double)(z+rightVec.z*f4)).tex((double)xMax,
		 * (double)yMax).color(this.particleRed, this.particleGreen,
		 * this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
		 * worldRendererIn.pos((double)(x - rightVec.x * f4),
		 * (double)(y-rightVec.y*f4),
		 * (double)(z-rightVec.z*f4)).tex((double)xMax,
		 * (double)yMin).color(this.particleRed, this.particleGreen,
		 * this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
		 * worldRendererIn.pos((double)(xNext - rightVec.x * f4),
		 * (double)(yNext-rightVec.y*f4),
		 * (double)(zNext-rightVec.z*f4)).tex((double)xMin,
		 * (double)yMin).color(this.particleRed, this.particleGreen,
		 * this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
		 * worldRendererIn.pos((double)(xNext + rightVec.x * f4 ),
		 * (double)(yNext+rightVec.y*f4),
		 * (double)(zNext+rightVec.z*f4)).tex((double)xMin,
		 * (double)yMin).color(this.particleRed, this.particleGreen,
		 * this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();;
		 */
		// System.out.println("Rotation X: "+rotationX+" Rotation Z:
		// "+rotationZ+" Rotation YZ: "+rotationYZ+" Rotation XY: "+rotationXY+"
		// rotation XZ: "+rotationXZ);
		// super.renderParticle(worldRendererIn, entityIn, partialTicks,
		// rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}

	@Override
	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
		this.resetPositionToBB();
	}

	@Override
	public int getFXLayer() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_) {
		return 15728880;
	}

	public float getBrightness(float p_70013_1_) {
		return 1.0F;
	}
}
