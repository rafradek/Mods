package rafradek.TF2weapons.characters.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.characters.EntityTF2Character.Order;

public class EntityAIFollowTrader extends EntityAIBase {

	public EntityTF2Character owner;
	private int timeToRecalcPath;

	public EntityAIFollowTrader(EntityTF2Character entity) {
		this.owner = entity;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return this.owner.getOwner() != null && this.owner.getDistanceSqToEntity(this.owner.getOwner()) > 100 && this.owner.isEntityAlive() && this.owner.getOrder() == Order.FOLLOW && 
				!(this.owner.getAttackTarget() != null && this.owner.getOwner().getDistanceSqToEntity(this.owner) < 600);
	}

	@Override
	public void startExecuting() {
		this.timeToRecalcPath = 0;
	}

	public void resetTask() {
		this.owner.getNavigator().clearPathEntity();
	}
	@Override
	public boolean shouldContinueExecuting() {
		return !this.owner.getNavigator().noPath() && this.owner.getDistanceSqToEntity(this.owner.getOwner()) > 100;
	}

	@Override
	public void updateTask() {

		if(this.owner.getAttackTarget() == null || (this.owner.getAttackTarget().getDistanceSqToEntity(this.owner) < this.owner.getAttackTarget().getDistanceSqToEntity(this.owner.getOwner())))
			this.owner.getLookHelper().setLookPositionWithEntity(this.owner.getOwner(), 10.0F, 10F);

		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 8;

			double distance = this.owner.getDistanceSqToEntity(this.owner.getOwner());
			if((!this.owner.getNavigator().tryMoveToEntityLiving(this.owner.getOwner(), 1.2f) && distance > 750) || (this.owner.getNavigator().noPath() && distance > 144)) {
				
				int i = MathHelper.floor(this.owner.getOwner().posX) - 2;
                int j = MathHelper.floor(this.owner.getOwner().posZ) - 2;
                int k = MathHelper.floor(this.owner.getOwner().getEntityBoundingBox().minY);

                for (int l = 0; l <= 4; ++l)
                {
                    for (int i1 = 0; i1 <= 4; ++i1)
                    {
                        if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1))
                        {
                            this.owner.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.owner.rotationYaw, this.owner.rotationPitch);
                            this.owner.getNavigator().clearPathEntity();
                            return;
                        }
                    }
                }
			}
		}
	}
	
	protected boolean isTeleportFriendlyBlock(int x, int p_192381_2_, int y, int p_192381_4_, int p_192381_5_)
    {
        BlockPos blockpos = new BlockPos(x + p_192381_4_, y - 1, p_192381_2_ + p_192381_5_);
        IBlockState iblockstate = this.owner.world.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(this.owner.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && 
        		iblockstate.canEntitySpawn(this.owner) && this.owner.world.isAirBlock(blockpos.up()) && this.owner.world.isAirBlock(blockpos.up(2));
    }
}
