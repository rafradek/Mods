package rafradek.TF2weapons.lightsource;

import atomicstryker.dynamiclights.client.IDynamicLightSource;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.Optional;;

@Optional.Interface(iface = "atomicstryker.dynamiclights.client.IDynamicLightSource", modid = "dynamiclights", striprefs = true)
public class MuzzleFlashLightSource implements IDynamicLightSource {

	public int ticksLeft = 4;
	public boolean over;
	public Entity shooter;

	public MuzzleFlashLightSource(Entity entity) {
		// TODO Auto-generated constructor stub
		shooter = entity;
	}

	@Override
	public Entity getAttachmentEntity() {
		// TODO Auto-generated method stub
		return shooter;
	}

	@Override
	public int getLightLevel() {
		// TODO Auto-generated method stub
		return 13;
	}

	public void update() {
		if (--ticksLeft <= 0)
			over = true;
	}
}
