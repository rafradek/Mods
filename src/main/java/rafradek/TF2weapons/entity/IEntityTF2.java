package rafradek.TF2weapons.entity;

import net.minecraft.util.math.AxisAlignedBB;

public interface IEntityTF2 {

	boolean hasHead();
	
	AxisAlignedBB getHeadBox();

	boolean hasDamageFalloff();
	
	boolean isBuilding();
	
	boolean isBackStabbable();
}
