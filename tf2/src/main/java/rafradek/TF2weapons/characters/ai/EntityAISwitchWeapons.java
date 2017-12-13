package rafradek.TF2weapons.characters.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntityTF2Character;
import rafradek.TF2weapons.weapons.ItemWeapon;

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
		// System.out.println("Stop");
		this.host.getNavigator().clearPathEntity();
		/*
		 * if(this.host.cloak.getTagCompound().getBoolean("Active")){
		 * ((ItemCloak)this.host.cloak.getItem()).altUse(this.host.cloak,
		 * this.host, this.host.world); }
		 */
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
					&& this.host.getDistanceSqToEntity(target)>ItemFromData.getData(stack).getFloat(PropertyType.EFFICIENT_RANGE);
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
