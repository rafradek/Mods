package rafradek.TF2weapons.weapons;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class ItemHorn extends Item {

	public ItemHorn() {
		this.setMaxStackSize(1);
		this.setCreativeTab(TF2weapons.tabutilitytf2);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
		ItemStack backpack = getBackpack(entityLiving);
		if (!backpack.isEmpty() && this.getMaxItemUseDuration(stack) - timeLeft >= ItemFromData.getData(backpack)
				.getInt(PropertyType.FIRE_SPEED) && backpack.getTagCompound().getFloat("Rage") >= 1)
			backpack.getTagCompound().setBoolean("Active", true);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		ItemStack backpack = getBackpack(playerIn);
		if (!backpack.isEmpty() && (backpack.getTagCompound().getFloat("Rage") >= 1 || playerIn.isCreative())) {
			playerIn.setActiveHand(hand);
			if (TF2weapons.getTeamForDisplay(playerIn) == 1)
				playerIn.playSound(ItemFromData.getSound(backpack, PropertyType.HORN_BLU_SOUND), 0.8f, 1f);
			else
				playerIn.playSound(ItemFromData.getSound(backpack, PropertyType.HORN_RED_SOUND), 0.8f, 1f);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean showDurabilityBar(ItemStack stack) {
		if (getBackpack(Minecraft.getMinecraft().player).isEmpty())
			return false;
		return getBackpack(Minecraft.getMinecraft().player).getTagCompound().getFloat("Rage") != 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getDurabilityForDisplay(ItemStack stack) {
		if (getBackpack(Minecraft.getMinecraft().player).isEmpty())
			return 0;
		return 1 - getBackpack(Minecraft.getMinecraft().player).getTagCompound().getFloat("Rage");
	}

	public static ItemStack getBackpack(EntityLivingBase living) {
		return !living.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty()
				&& living.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemSoldierBackpack
						? living.getItemStackFromSlot(EntityEquipmentSlot.CHEST) : ItemStack.EMPTY;
	}
}
