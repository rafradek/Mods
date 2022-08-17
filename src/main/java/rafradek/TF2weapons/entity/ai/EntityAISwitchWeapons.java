package rafradek.TF2weapons.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.item.ItemFromData;
import rafradek.TF2weapons.item.ItemWeapon;
import rafradek.TF2weapons.util.PropertyType;

public class EntityAISwitchWeapons extends EntityAIBase {

	public EntityTF2Character host;
	public int counter;
	public EntityAISwitchWeapons(EntityTF2Character ent) {
		this.host = ent;
	}

	@Override
	public boolean shouldExecute() {
		return ++this.counter >2 && this.host.getDiff() > 2 && this.host.getAttackTarget() != null;
	}

	@Override
	public void resetTask() {
		
	}

	@Override
	public void updateTask() {
		this.counter=0;
		// System.out.println("Tick");
		EntityLivingBase target = this.host.getAttackTarget();
		ItemStack stack=this.host.getHeldItemMainhand();
		ItemStack stack2=this.host.getHeldItemMainhand();
		if(!stack.isEmpty() && stack.getItem() instanceof ItemWeapon){
			boolean outRange=ItemFromData.getData(stack).getFloat(PropertyType.EFFICIENT_RANGE) != 0 
					&& this.host.getDistanceSq(target)>ItemFromData.getData(stack).getFloat(PropertyType.EFFICIENT_RANGE);
			boolean outAmmo=((ItemWeapon)stack.getItem()).hasClip(stack) && stack.getItemDamage()==stack.getMaxDamage();
			if(this.host.usedSlot==0 && (outRange || outAmmo)){
				this.host.usedSlot=1;
			}
			else if(this.host.usedSlot==1 && (outRange || outAmmo)){
				this.host.usedSlot=0;
			}
		}
		// this.host.getLookHelper().setLookPosition(target.posX+x*2,target.posY+target.getEyeHeight(),target.posZ+z*2,
		// 90f, 90.0F);

	}
}
