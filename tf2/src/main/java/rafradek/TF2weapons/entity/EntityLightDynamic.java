package rafradek.TF2weapons.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityLightDynamic extends Entity {

	public int timeToLive;
	public Entity parent;
	
	public EntityLightDynamic(World worldIn) {
		super(worldIn);
	}
	
	public EntityLightDynamic(World worldIn, Entity parent, int timeToLive) {
		super(worldIn);
		this.parent=parent;
		this.timeToLive = timeToLive;
		// TODO Auto-generated constructor stub
	}
	
	public EntityLightDynamic(World worldIn, BlockPos pos, int timeToLive) {
		super(worldIn);
		this.setPosition(pos.getX(), pos.getY(), pos.getZ());
		this.timeToLive = timeToLive;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub

	}
	
	public void onUpdate() {
		super.onUpdate();
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		if (this.ticksExisted >= timeToLive || (this.parent != null && this.parent.isDead)) {
			this.setDead();
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
