package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILookIdle;

public class EntityAISeek extends EntityAILookIdle {

	private EntityLiving host;

	public EntityAISeek(EntityLiving entitylivingIn) {
		super(entitylivingIn);
		this.host = entitylivingIn;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean shouldExecute() {
		return this.host.getRNG().nextFloat() < 0.13F;
	}
}
