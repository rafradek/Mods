package rafradek.TF2weapons.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.TF2Attribute;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;
import rafradek.TF2weapons.message.TF2Message;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemMeleeWeapon extends ItemBulletWeapon {

	@Override
	public float getMaxRange(ItemStack stack) {
		return TF2Attribute.getModifier("Range", stack, 2.4f, null);
	}

	public float getBulletSize() {
		return 0.35f;
	}

	@Override
	public boolean showTracer(ItemStack stack) {
		return false;
	}

	@Override
	public short getAltFiringSpeed(ItemStack item, EntityLivingBase player) {
		if (TF2Attribute.getModifier("Ball Release", item, 0, player) > 0) {
			if (player instanceof EntityTF2Character) {
				return (short) this.getFiringSpeed(getNewStack("sandmanball"), player);
			}
			else
				return 2000;
		}
		return super.getAltFiringSpeed(item, player);
	}
	
	public boolean canAltFire(World worldObj, EntityLivingBase player, ItemStack item) {
		return super.canAltFire(worldObj, player, item) && !(player instanceof EntityPlayer && ((EntityPlayer)player).getCooldownTracker().hasCooldown(this));
	}
	
	@Override
	public void altUse(ItemStack stack, EntityLivingBase living, World world) {
		if (TF2Attribute.getModifier("Ball Release", stack, 0, living) > 0) {
			ItemStack ballStack = getNewStack("sandmanball");
			if (!this.searchForAmmo(living, ballStack).isEmpty()) {
				if (living instanceof EntityPlayer)
					((EntityPlayer)living).getCooldownTracker().setCooldown(this, TF2ConfigVars.fastItemCooldown ? this.getFiringSpeed(ballStack, living)/50 : 200);
				ItemStack oldHeldItem = living.getHeldItemMainhand();
				living.setHeldItem(EnumHand.MAIN_HAND, ballStack);
				((ItemProjectileWeapon) ballStack.getItem()).use(ballStack, living, world, EnumHand.MAIN_HAND, null);
				living.setHeldItem(EnumHand.MAIN_HAND, oldHeldItem);
			}
		}
	}
	public void draw(WeaponsCapability weaponsCapability, ItemStack stack, EntityLivingBase living, World world) {
		super.draw(weaponsCapability, stack, living, world);
		if(living instanceof EntityPlayerMP)
			TF2weapons.network.sendTo(new TF2Message.UseMessage(stack.getItemDamage(), 
					false,this.getAmmoAmount(living, getNewStack("sandmanball")), EnumHand.MAIN_HAND),(EntityPlayerMP) living);
	}
	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand,
			PredictionMessage message) {
		ItemWeapon.shouldSwing = true;
		living.swingArm(hand);
		ItemWeapon.shouldSwing = false;
		return super.use(stack, living, world, hand, message);
	}

	@Override
	public float critChance(ItemStack stack, Entity entity) {
		float chance = 0.15f;
		if (ItemUsable.lastDamage.containsKey(entity))
			for (int i = 0; i < 20; i++)
				chance += ItemUsable.lastDamage.get(entity)[i] / 177;
		return Math.min(chance, 0.6f);
	}

	@Override
	public boolean doMuzzleFlash(ItemStack stack, EntityLivingBase attacker, EnumHand hand) {
		return false;
	}
	public boolean showInfoBox(ItemStack stack, EntityPlayer player){
		return TF2Attribute.getModifier("Ball Release", stack, 0, player) > 0 || TF2Attribute.getModifier("Kill Count", stack, 0, player) > 0;
	}
	public String[] getInfoBoxLines(ItemStack stack, EntityPlayer player){
		String[] result=new String[2];
		if(TF2Attribute.getModifier("Kill Count", stack, 0, player) > 0){
			result[0]="HEADS";
			result[1]=Integer.toString(player.getCapability(TF2weapons.WEAPONS_CAP, null).getHeads());
		}
		else{
			result[0]="BALLS";
			//ItemStack ballStack = getNewStack("sandmanball");
			int ammoLeft=player.getCapability(TF2weapons.PLAYER_CAP, null).cachedAmmoCount[14];
			result[1]=Integer.toString(ammoLeft);
		}
		
		return result;
	}
}
