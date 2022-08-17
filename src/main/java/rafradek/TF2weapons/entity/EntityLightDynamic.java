package rafradek.TF2weapons.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLightDynamic extends Entity {

	public int timeToLive;
	public Entity parent;
	
	public EntityLightDynamic(World worldIn) {
		super(worldIn);
	}
	
	public EntityLightDynamic(World worldIn, Entity parent, int timeToLive) {
		super(worldIn);
		this.setPosition(parent.posX, parent.posY+parent.getEyeHeight(), parent.posZ);
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
		if (this.parent != null)
			this.setPosition(this.parent.posX, this.parent.posY+this.parent.getEyeHeight(), this.parent.posZ);
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

	@SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
		return false;
	}
}
