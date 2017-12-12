package rafradek.TF2weapons.building;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterDim extends Teleporter {

	public BlockPos target;
	
	public TeleporterDim(WorldServer worldIn, BlockPos targetPos) {
		super(worldIn);
		target = targetPos;
		// TODO Auto-generated constructor stub
	}
	
	public boolean makePortal(Entity entityIn)
    {
		return true;
    }
	
	public boolean placeInExistingPortal(Entity entityIn, float rotationYaw)
    {
		if (entityIn instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)entityIn).connection.setPlayerLocation(target.getX(), target.getY(), target.getZ(), entityIn.rotationYaw, entityIn.rotationPitch);
        }
        else
        {
            entityIn.setLocationAndAngles(target.getX(), target.getY(), target.getZ(), entityIn.rotationYaw, entityIn.rotationPitch);
        }
        return true;
    }
}
