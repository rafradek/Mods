package rafradek.TF2weapons.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.util.PropertyType;

public class ItemAmmo extends Item {

	public static final String[] AMMO_TYPES = new String[] { "none", "shotgun", "minigun", "pistol", "revolver", "smg",
			"sniper", "rocket", "grenade", "syringe", "fire", "sticky", "medigun", "flare", "ball", "custom" };
	public static final int[] AMMO_MAX_STACK = new int[] { 64, 64, 64, 64, 64, 64, 16, 32, 32, 64, 1, 32, 1, 64, 64, 64 };
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
	public void getSubItems(CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		// System.out.println(this.getCreativeTab());
		if(!this.isInCreativeTab(par2CreativeTabs))
			return;
		for (int i = 1; i < AMMO_TYPES.length-1; i++)
			if (i != 10 && i != 12 && i != 2 && i != 3 && i != 5 && i != 9)
				par3List.add(new ItemStack(this, 1, i));
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return AMMO_MAX_STACK[MathHelper.clamp(stack.getMetadata(),0,AMMO_MAX_STACK.length)];
	}

	public int consumeAmmo(EntityLivingBase living, ItemStack stack, int amount) {
		if (stack == STACK_FILL)
			return 0;
		// if(EntityDispenser.isNearDispenser(living.world, living)) return;
		if (amount > 0) {
			int left = Math.max(0, amount - stack.getCount());
			stack.shrink(amount);
			return left;

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
		return 0;
	}


	@Override
	public ActionResult<ItemStack> onItemRightClick( World world, EntityPlayer living, EnumHand hand) {
		if (!world.isRemote)
			FMLNetworkHandler.openGui(living, TF2weapons.instance, 0, world, 0, 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, living.getHeldItem(hand));
	}

	public int getAmount(ItemStack stack) {
		return stack.getCount();
	}
}
