package rafradek.TF2weapons.weapons;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.building.EntityDispenser;
import rafradek.TF2weapons.characters.EntityTF2Character;

public class ItemAmmo extends Item {

	public static final String[] AMMO_TYPES = new String[] { "none", "shotgun", "minigun", "pistol", "revolver", "smg",
			"sniper", "rocket", "grenade", "syringe", "fire", "sticky", "medigun", "flare", "ball" };
	public static final int[] AMMO_MAX_STACK = new int[] { 64, 64, 64, 64, 64, 64, 16, 32, 32, 64, 1, 32, 1, 64, 64 };
	public static ItemStack STACK_FILL;

	public ItemAmmo() {
		this.setHasSubtypes(true);
	}

	public String getType(ItemStack stack) {
		return AMMO_TYPES[this.getTypeInt(stack)];
	}

	public int getTypeInt(ItemStack stack) {
		return stack.getMetadata();
	}

	public boolean isValidForWeapon(ItemStack ammo, ItemStack weapon) {
		return getTypeInt(ammo) == ItemFromData.getData(weapon).getInt(PropertyType.AMMO_TYPE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab() {
		return TF2weapons.tabsurvivaltf2;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.tf2ammo." + getType(stack);
	}

	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		// System.out.println(this.getCreativeTab());
		for (int i = 1; i < AMMO_TYPES.length; i++)
			if (i != 10 && i != 12)
				par3List.add(new ItemStack(this, 1, i));
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return AMMO_MAX_STACK[stack.getMetadata()];
	}

	public void consumeAmmo(EntityLivingBase living, ItemStack stack, int amount) {
		if (stack == STACK_FILL)
			return;
		// if(EntityDispenser.isNearDispenser(living.world, living)) return;
		if (amount > 0) {
			stack.shrink(amount);

			/*if (stack.isEmpty() && living instanceof EntityPlayer) {
				
				if (living.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3) != null){
					IItemHandlerModifiable invAmmo = (IItemHandlerModifiable) living.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3)
							.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
					
					for (int i = 0; i < invAmmo.getSlots(); i++) {
						ItemStack stackInv = invAmmo.getStackInSlot(i);
						if (stack == stackInv) {
							invAmmo.setStackInSlot(i, null);
							return;
						}
					}
				}
				((EntityPlayer) living).inventory.deleteStack(stack);
			}*/
		}
	}

	public static void consumeAmmoGlobal(EntityLivingBase living, ItemStack stack, int amount) {
		if (EntityDispenser.isNearDispenser(living.world, living))
			return;
		if (!(living instanceof EntityPlayer))
			return;
		if (TF2Attribute.getModifier("Metal Ammo", stack, 0, living) != 0) {
			living.getCapability(TF2weapons.WEAPONS_CAP, null).setMetal(living.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal()-amount);
		}
		if (amount > 0) {
			amount = ((ItemWeapon) stack.getItem()).getActualAmmoUse(stack, living, amount);
			// int
			// type=ItemFromData.getData(stack).getInt(PropertyType.AMMO_TYPE);

			// stack.getCount()-=amount;
			ItemStack stackAmmo;
			while (amount > 0 && !(stackAmmo = searchForAmmo(living, stack)).isEmpty()) {
				int inStack;
				if(stackAmmo.getMaxDamage()!=0)
					inStack = stackAmmo.getMaxDamage()-stackAmmo.getItemDamage();
				else
					inStack = stackAmmo.getCount();
				((ItemAmmo) stackAmmo.getItem()).consumeAmmo(living, stackAmmo, amount);
				amount -= inStack;
			}
		}
	}

	public static ItemStack searchForAmmo(EntityLivingBase owner, ItemStack stack) {
		if (EntityDispenser.isNearDispenser(owner.world, owner))
			return STACK_FILL;

		if (!(owner instanceof EntityPlayer) || ((EntityPlayer)owner).capabilities.isCreativeMode)
			return STACK_FILL;

		int type = ((ItemUsable) stack.getItem()).getAmmoType(stack);

		if (type == 0)
			return STACK_FILL;

		int metalammo = (int) TF2Attribute.getModifier("Metal Ammo", stack, 0, owner);
		if (metalammo != 0 && owner.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal() >= metalammo) 
			return STACK_FILL;
		
		if (!owner.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3).isEmpty()){
			IItemHandler inv=owner.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3)
					.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			//System.out.println("Ammo Search: "+inv.getSlots());
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stackCap = inv.getStackInSlot(i);
				//System.out.println("Stack: "+stackCap);
				if (stackCap != null && stackCap.getItem() instanceof ItemAmmo
						&& ((ItemAmmo) stackCap.getItem()).getTypeInt(stackCap) == type){
					//System.out.println("Found: "+i);
					return stackCap;
				}
			}
		}
		for (int i = 0; i < ((EntityPlayer) owner).inventory.mainInventory.size(); i++) {
			ItemStack stackInv = ((EntityPlayer) owner).inventory.mainInventory.get(i);
			if (stackInv != null && stackInv.getItem() instanceof ItemAmmo
					&& ((ItemAmmo) stackInv.getItem()).getTypeInt(stackInv) == type)
				return stackInv;
		}
		return ItemStack.EMPTY;
	}

	public static int getAmmoAmount(EntityLivingBase owner, ItemStack stack) {
		if (EntityDispenser.isNearDispenser(owner.world, owner) || (owner instanceof EntityPlayer && ((EntityPlayer)owner).capabilities.isCreativeMode))
			return 999;

		if (owner instanceof EntityTF2Character)
			return ((EntityTF2Character) owner).ammoLeft;

		if(TF2Attribute.getModifier("Ball Release", stack, 0, owner)>0)
			stack=ItemFromData.getNewStack("sandmanball");
		int type = ItemFromData.getData(stack).getInt(PropertyType.AMMO_TYPE);

		if (TF2Attribute.getModifier("Metal Ammo", stack, 0, owner) != 0) {
			return owner.getCapability(TF2weapons.WEAPONS_CAP, null).getMetal();
		}
		
		if (type == 0)
			return 999;

		int ammoCount = 0;

		if (!owner.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3).isEmpty()){
			IItemHandler inv=owner.getCapability(TF2weapons.INVENTORY_CAP, null).getStackInSlot(3)
			.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			for (int i = 0; i < inv.getSlots(); i++) {
				ItemStack stackCap = inv.getStackInSlot(i);
				if (stackCap != null && stackCap.getItem() instanceof ItemAmmo
						&& ((ItemAmmo) stackCap.getItem()).getTypeInt(stackCap) == type){
					//System.out.println("Found: "+i);
					if(stackCap.getMaxDamage()!=0)
						ammoCount += stackCap.getMaxDamage()-stackCap.getItemDamage();
					else
						ammoCount += stackCap.getCount();
				}
			}
		}
		for (int i = 0; i < ((EntityPlayer) owner).inventory.mainInventory.size(); i++) {
			ItemStack stackInv = ((EntityPlayer) owner).inventory.mainInventory.get(i);
			if (stackInv != null && stackInv.getItem() instanceof ItemAmmo
					&& ((ItemAmmo) stackInv.getItem()).getTypeInt(stackInv) == type)
				if(stackInv.getMaxDamage()!=0)
					ammoCount += stackInv.getMaxDamage()-stackInv.getItemDamage();
				else
					ammoCount += stackInv.getCount();
		}
		return (int) (ammoCount / TF2Attribute.getModifier("Ammo Eff", stack, 1, owner));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick( World world, EntityPlayer living, EnumHand hand) {
		if (!world.isRemote)
			FMLNetworkHandler.openGui(living, TF2weapons.instance, 0, world, 0, 0, 0);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, living.getHeldItem(hand));
	}
}
