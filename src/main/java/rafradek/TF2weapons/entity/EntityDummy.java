package rafradek.TF2weapons.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.WeaponsCapability;

public class EntityDummy extends EntityLiving {

	public WeaponsCapability cap;
	public EntityDummy(World worldIn) {
		super(worldIn);
		cap = new WeaponsCapability(this);
		this.setSize(0, 0);
		this.setDead();
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
    @Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing)
    {
		if (capability == TF2weapons.WEAPONS_CAP)
			return (T) cap;
		else
			return super.getCapability(capability, facing);
    }
	
	@Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing)
    {
        return capability == TF2weapons.WEAPONS_CAP || super.hasCapability(capability, facing);
    }
}
