package rafradek.TF2weapons.weapons;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2EventsCommon;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.characters.EntitySpy;

public class ItemCloak extends ItemFromData {

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
		super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
		if (par1ItemStack.getTagCompound().getBoolean("Active")
				&& par3Entity.getDataManager().get(TF2EventsCommon.ENTITY_INVIS)) {
			// System.out.println("uncharge");
			par1ItemStack.setItemDamage(Math.min(600, par1ItemStack.getItemDamage() + 3));
			if (par1ItemStack.getTagCompound().getBoolean("Strange")) {
				par1ItemStack.getTagCompound().setInteger("CloakTicks",
						par1ItemStack.getTagCompound().getInteger("CloakTicks") + 1);
				if (par1ItemStack.getTagCompound().getInteger("CloakTicks") % 20 == 0)
					TF2EventsCommon.onStrangeUpdate(par1ItemStack, (EntityLivingBase) par3Entity);
			}
			if (par1ItemStack.getItemDamage() >= 600) {
				par1ItemStack.setItemDamage(600);
				this.setCloak(false, par1ItemStack, (EntityLivingBase) par3Entity, par2World);
			}
		} else if (par1ItemStack.getTagCompound().getBoolean("Active")
				&& !par3Entity.getDataManager().get(TF2EventsCommon.ENTITY_INVIS))
			par1ItemStack.getTagCompound().setBoolean("Active", false);
		else
			par1ItemStack.setItemDamage(Math.max(par1ItemStack.getItemDamage() - 1, 0));
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return 600;
	}

	public boolean canAltFire(World worldObj, EntityLivingBase player, ItemStack item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer living, EnumHand hand) {
		ItemStack stack=living.getHeldItem(hand);
		if (living.isInvisible() || stack.getItemDamage() < 528) {
			this.setCloak(!living.getDataManager().get(TF2EventsCommon.ENTITY_INVIS), stack, living, world);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		if (item.getTagCompound().getBoolean("Active"))
			this.setCloak(false, item, player, player.world);
		return super.onDroppedByPlayer(item, player);
	}

	public static ItemStack searchForWatches(EntityLivingBase living) {
		if (living instanceof EntitySpy)
			return ((EntitySpy) living).loadout.get(3);
		if (living instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) living;
			if (!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ItemCloak
					&& player.getHeldItemOffhand().getTagCompound().getBoolean("Active"))
				// System.out.println("Found offhand");
				return player.getHeldItemOffhand();
			for (ItemStack stack : player.inventory.mainInventory)
				if (!stack.isEmpty() && stack.getItem() instanceof ItemCloak
						&& stack.getTagCompound().getBoolean("Active"))
					// System.out.println("Found hand");
					return stack;

		}
		return ItemStack.EMPTY;
	}

	public void setCloak(boolean active, ItemStack stack, EntityLivingBase living, World world) {
		// System.out.println("set active: "+active);
		if (!active || !(living instanceof EntityPlayer) || searchForWatches(living).isEmpty()) {
			if (!active) {
				living.setInvisible(false);
				living.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks = 20;
			}
			// System.out.println("ok: "+active);
			stack.getTagCompound().setBoolean("Active", active);
			living.getDataManager().set(TF2EventsCommon.ENTITY_INVIS, active);

			// setInvisiblity(living);
			if (active)
				living.playSound(ItemFromData.getSound(stack, PropertyType.CLOAK_SOUND), 1.5f, 1);
			else
				living.playSound(ItemFromData.getSound(stack, PropertyType.DECLOAK_SOUND), 1.5f, 1);
			if (!world.isRemote) {
				// TF2weapons.sendTracking(new
				// TF2Message.PropertyMessage("IsCloaked",
				// (byte)(active?1:0),living),living);
			}
		}
	}

	public static void setInvisiblity(EntityLivingBase living) {
		boolean cloaked = living.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks >= 20;
		boolean disguised = living.getDataManager().get(TF2EventsCommon.ENTITY_DISGUISED);
		living.setInvisible(cloaked || (disguised && living.getCapability(TF2weapons.WEAPONS_CAP, null).invisTicks == 0
				&& !living.getDataManager().get(TF2EventsCommon.ENTITY_INVIS)));
	}

	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par2List,
			boolean par4) {
		super.addInformation(par1ItemStack, par2EntityPlayer, par2List, par4);

		par2List.add("Charge: " + (100 - par1ItemStack.getItemDamage() / 6) + "%");
	}
	public boolean showInfoBox(ItemStack stack, EntityPlayer player){
		return true;
	}
	public String[] getInfoBoxLines(ItemStack stack, EntityPlayer player){
		return new String[]{"CLOAK",(100 - stack.getItemDamage() / 6) + "%"};
	}
}
