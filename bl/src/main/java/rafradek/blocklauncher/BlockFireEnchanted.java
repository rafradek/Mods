package rafradek.blocklauncher;

import net.minecraft.block.BlockFire;
import net.minecraft.world.World;

public class BlockFireEnchanted extends BlockFire {
	@Override
	public int tickRate(World worldIn) {
		return 3;
	}
}
