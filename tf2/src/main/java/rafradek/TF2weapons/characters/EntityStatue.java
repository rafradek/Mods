package rafradek.TF2weapons.characters;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityStatue extends Entity{

	public EntityLivingBase entity;
	public boolean isFeign;
	public float renderYawOffset ;

	public EntityStatue(World worldIn) {
		super(worldIn);
		// TODO Auto-generated constructor stub
	}

	public EntityStatue(World worldIn, EntityLivingBase toCopy, boolean isFeign) {
		super(worldIn);
		this.setPosition(toCopy.posX, toCopy.posY, toCopy.posZ);
		this.width = toCopy.width;
		this.height = toCopy.height;
		this.entity = toCopy;
		this.isFeign = isFeign;
		if(!isFeign) {
			this.entity.deathTime = 0;
			this.entity.hurtTime = 0;
			this.renderYawOffset = toCopy.renderYawOffset;
		}
		else {
			this.motionX=entity.motionX;
			this.motionY=entity.motionY;
			this.motionZ=entity.motionZ;
		}
		/*
		 * this.renderYawOffset=toCopy.renderYawOffset;
		 * this.rotationYawHead=toCopy.rotationYawHead;
		 * this.rotationYaw=toCopy.rotationYaw;
		 * this.rotationPitch=toCopy.rotationPitch;
		 * this.limbSwingAmount=toCopy.limbSwingAmount;
		 * this.limbSwing=toCopy.limbSwing;
		 * this.entityClass=toCopy.getClass().getName();
		 * this.data=toCopy.getDataManager();
		 */
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpdate() {
		if(this.onGround) {
			this.motionX *= 0.1;
			this.motionZ *= 0.1;
		}
		this.motionX *= 0.98;
		this.motionY *= 0.98;
		this.motionZ *= 0.98;
		this.motionY -= 0.08;
		
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		if(this.isFeign && this.ticksExisted >= 20) {
            /*int i = 20;
            while (i > 0)
            {
                int j = EntityXPOrb.getXPSplit(i);
                i -= j;
                EntityXPOrb orb=new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j);
                orb.xpOrbAge=5900;
                this.world.spawnEntity(orb);
            }*/

            this.setDead();

            for (int k = 0; k < 20; ++k)
            {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1);
            }
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub

	}
}
