package rafradek.blocklauncher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEventBus {
	public static HashMap<Entity, Integer> extraBurn = new HashMap<Entity, Integer>();
	public static ArrayList<DestroyBlockEntry> destroyProgress = new ArrayList<>();
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void getFov(FOVUpdateEvent event) {
		if (!event.getEntity().getHeldItem(EnumHand.MAIN_HAND).isEmpty()
				&& event.getEntity().getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof TNTCannon)
			if (!event.getEntity().getActiveItemStack().isEmpty()
					&& ((TNTCannon) event.getEntity().getHeldItem(EnumHand.MAIN_HAND).getItem())
							.getType(event.getEntity().getHeldItem(EnumHand.MAIN_HAND)) == 5)
				event.setNewfov(event.getNewfov() * 0.4f);
	}

	@SubscribeEvent
	public void serverTickEnd(TickEvent.ServerTickEvent event) {

		if (event.phase == TickEvent.Phase.END){
			// System.out.println("ex: "+extraBurn.size());
				for (int i = 0; i < destroyProgress.size(); i++) {
					DestroyBlockEntry entry = destroyProgress.get(i);
	
					if (entry != null) {
	
						entry.curDamage -= 0.0125f;
						if (entry.curDamage <= 0 || entry.world.isAirBlock(entry.pos)) {
							destroyProgress.set(i, null);
							entry.world.sendBlockBreakProgress(Math.min(Integer.MAX_VALUE, 0xFFFF + i), entry.pos, -1);
							continue;
						}
	
						if (entry.world.getWorldTime() % 20 == 0) {
							int val = (int) ((entry.curDamage / BlockLauncher.getHardness(entry.world.getBlockState(entry.pos), entry.world)) * 10);
							entry.world.sendBlockBreakProgress(Math.min(Integer.MAX_VALUE, 0xFFFF + i), entry.pos, val);
						}
					}
				}
			if (!extraBurn.isEmpty()) {

			Iterator<Entry<Entity, Integer>> iterator = BlockEventBus.extraBurn.entrySet().iterator();
			while (iterator.hasNext()) {
			Entry<Entity, Integer> entry = iterator.next();
			entry.getKey().attackEntityFrom(DamageSource.ON_FIRE, 1.6f);
			entry.getKey().setFire(1);
			// System.out.println("burned"+entry.getValue());
			entry.setValue(entry.getValue() - 1);
			if (entry.getValue() <= 0)
			iterator.remove();
			}
			}
		}
	}
	
	public static class DestroyBlockEntry {
		public BlockPos pos;
		public float curDamage;
		public World world;

		public DestroyBlockEntry(BlockPos pos, World world) {
			this.world = world;
			this.pos = pos;
		}
	}
}
