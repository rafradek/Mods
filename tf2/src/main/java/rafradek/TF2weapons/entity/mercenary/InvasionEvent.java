package rafradek.TF2weapons.entity.mercenary;

import com.google.common.collect.Multimap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;

public class InvasionEvent implements INBTSerializable<NBTTagCompound> {

	public static Multimap<Squad.Type, Squad> squads;
	public World world;
	public BlockPos target;
	public int startTime;
	public float difficulty;
	public int wave;
	
	public InvasionEvent(NBTTagCompound tag) {
		this.deserializeNBT(tag);
	}
	
	public InvasionEvent(World world, BlockPos targetPos, float difficulty) {
		this.world = world;
		this.startTime = (int) world.getTotalWorldTime();
		this.target = targetPos;
		this.difficulty = difficulty;
		
	}

	@Override
	public NBTTagCompound serializeNBT() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

}
