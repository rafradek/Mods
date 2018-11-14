package rafradek.TF2weapons.client.particle;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityMuzzleFlash extends Particle {

	private int duration;
	private boolean nextDead;
	private EntityLivingBase owner;
	private EnumHand hand;
	
	public EntityMuzzleFlash(World par1World, EntityLivingBase shooter,EnumHand hand) {
		super(par1World, 0, 0, 0);
		this.particleMaxAge = 5;
		this.hand=hand;
		this.setSize(0.025f, 0.025f);
		// this.setParticleIcon(Item.itemsList[2498+256].getIconFromDamage(0));
		this.setParticleTextureIndex(65);
		// this.setParticleTextureIndex(81);
		this.multipleParticleScaleBy(1);
		this.owner = shooter;
		this.setPosition();
		// TODO Auto-generated constructor stub
		this.setRBGColorF(0.97f, 0.76f, 0.51f);
		// S/ystem.out.println("Crits: "+crits);e
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.particleScale *= 0.92f;
		this.particleAlpha *= 0.8f;
		this.setPosition();

	}

	public void setPosition() {
		float move = Minecraft.getMinecraft().player == this.owner ? 0.48f : 0.24f;
		this.posX = owner.posX - MathHelper.cos(owner.rotationYawHead / 180.0F * (float) Math.PI) * move * (hand==EnumHand.MAIN_HAND?1:-1);
		this.posY = owner.posY + owner.getEyeHeight() - move * 0.45f;
		this.posZ = owner.posZ - MathHelper.sin(owner.rotationYawHead / 180.0F * (float) Math.PI) * move * (hand==EnumHand.MAIN_HAND?1:-1);

		this.posX += -MathHelper.sin(owner.rotationYawHead / 180.0F * (float) Math.PI)
				* MathHelper.cos(owner.rotationPitch / 180.0F * (float) Math.PI);
		this.posZ += MathHelper.cos(owner.rotationYawHead / 180.0F * (float) Math.PI)
				* MathHelper.cos(owner.rotationPitch / 180.0F * (float) Math.PI);
		this.posY += (-MathHelper.sin(owner.rotationPitch / 180.0F * (float) Math.PI));
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
