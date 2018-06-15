package rafradek.TF2weapons.weapons;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2Util;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.ItemToken;
import rafradek.TF2weapons.message.TF2Message;

public class ItemJetpack extends ItemBackpack {

	public ItemJetpack() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return stack.getTagCompound().getShort("Charge") > 0;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return ( stack.getTagCompound().getShort("Charge"))/this.getCooldown(stack, null);
	}
	
	@Override
	public void onArmorTickAny(World world, EntityLivingBase player, ItemStack itemStack) {
		super.onArmorTickAny(world, player, itemStack);
		if (!world.isRemote) {
			if (itemStack.getTagCompound().getShort("Charge") > 0) {
				itemStack.getTagCompound().setShort("Charge", (short) (itemStack.getTagCompound().getInteger("Charge") - 1));
				if (itemStack.getTagCompound().getShort("Charge") == 0) {
					itemStack.getTagCompound().setShort("Charges", (short) (itemStack.getTagCompound().getShort("Charges") + 1));
				}
			}
			if (itemStack.getTagCompound().getBoolean("Active")) {
				if (player.onGround)
					itemStack.getTagCompound().setBoolean("Active", false);
				TF2Util.stomp(player);
			}
			if (itemStack.getTagCompound().getByte("Load") > 0) {
				itemStack.getTagCompound().setByte("Load", (byte) (itemStack.getTagCompound().getByte("Load") - 1));
				if (itemStack.getTagCompound().getByte("Load") == 0) {
					itemStack.getTagCompound().setByte("Charges", (byte) (itemStack.getTagCompound().getByte("Charges") - 1));
					Vec3d vel = player.getLook(1);
					vel = vel.scale(TF2Attribute.getModifier("Self Push Force", itemStack, 1, player));
					WeaponsCapability.get(player).setExpJump(true);
					player.motionX = vel.x;
					player.motionY = vel.y + (player instanceof EntityPlayer ? 0.1 : 0.25);
					player.motionZ = vel.z;
					itemStack.getTagCompound().setBoolean("Active", true);
					if (player instanceof EntityPlayerMP)
						((EntityPlayerMP)player).connection.sendPacket(new SPacketEntityVelocity(player));
				}
			}
			if (itemStack.getTagCompound().getShort("Charge") == 0 && itemStack.getTagCompound().getByte("Charges") < this.getMaxCharges(itemStack, player)) {
				itemStack.getTagCompound().setShort("Charge", (short) this.getCooldown(itemStack, player));
			}
		}
	}
	
	public int getCooldown(ItemStack stack, EntityLivingBase living) {
		return (int) ((TF2ConfigVars.fastItemCooldown ? getData(stack).getInt(PropertyType.COOLDOWN) : 300)/TF2Attribute.getModifier("Charge", stack, 1, living));
	}
	
	public int getAmmoType(ItemStack stack) {
		return TF2ConfigVars.freeUseItems ? 0 : super.getAmmoType(stack);
	}
	
	public boolean canActivate(ItemStack stack, EntityLivingBase player) {
		return ItemToken.allowUse(player, "pyro") && (player.getHeldItemMainhand().getItem() instanceof ItemJetpackTrigger || TF2Attribute.getModifier("Jetpack", stack, 0f, player) >= 2f)
				&& (!stack.getTagCompound().getBoolean("Active") || TF2Attribute.getModifier("Jetpack", stack, 0f, player) > 0f) 
				&& stack.getTagCompound().getByte("Load") <= 0 && stack.getTagCompound().getByte("Charges") > 0 
				&& (player.world.isRemote || this.getAmmoAmount(player, stack) > this.getActualAmmoUse(stack, player, 20));
	}
	
	public int getMaxCharges(ItemStack stack, EntityLivingBase player) {
		return 2;
	}
	
	public void activateJetpack(ItemStack stack, EntityLivingBase player, boolean setTimer) {
		player.motionY = Math.max(player.motionY, 0) + 0.5;
		player.isAirBorne = true;
		player.fallDistance = 0;
		this.consumeAmmoGlobal(player, stack, this.getActualAmmoUse(stack, player, 20));
		if (!player.world.isRemote) {
			WeaponsCapability.get(player).setExpJump(true);
			stack.getTagCompound().setByte("Load", (byte) 12);
			if (player instanceof EntityPlayerMP)
				TF2weapons.network.sendTo(new TF2Message.ActionMessage(30, player), (EntityPlayerMP) player);
		}
		if (setTimer && !(player.getHeldItemMainhand().getItem() instanceof ItemJetpackTrigger)) {
			WeaponsCapability.get(player).fire1Cool = 1500;
			WeaponsCapability.get(player).fire2Cool = 1500;
		}
	}
}
