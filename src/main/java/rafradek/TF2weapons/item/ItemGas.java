package rafradek.TF2weapons.item;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2ConfigVars;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.common.WeaponsCapability;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemGas extends ItemProjectileWeapon {

	public ItemGas() {
		super();
		this.setMaxStackSize(64);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab() {
		return TF2weapons.tabutilitytf2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean showDurabilityBar(ItemStack stack) {
		Integer value = Minecraft.getMinecraft().player.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool
				.get(getData(stack).getName());
		return value != null && value > 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getDurabilityForDisplay(ItemStack stack) {
		Integer value = Minecraft.getMinecraft().player.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool
				.get(getData(stack).getName());
		return (double) (value != null ? value : 0) / (double) 1600;
	}

	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		Integer value = living.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.get(getData(stack).getName());
		return (value == null || value <= 0) && super.canFire(world, living, stack)
				&& !(living instanceof EntityPlayer && ((EntityPlayer) living).getCooldownTracker().hasCooldown(this));
	}

	@Override
	public boolean doMuzzleFlash(ItemStack stack, EntityLivingBase attacker, EnumHand hand) {
		return false;
	}

	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand,
			PredictionMessage message) {
		if (super.use(stack, living, world, hand, message) && !world.isRemote) {
			if (living instanceof EntityPlayer && !((EntityPlayer) living).capabilities.isCreativeMode
					&& !TF2ConfigVars.freeUseItems)
				stack.shrink(1);
			if (living.hasCapability(TF2weapons.WEAPONS_CAP, null))
				WeaponsCapability.get(living).addEffectCooldown(getData(stack).getName(), 600);
			// ((EntityPlayer) living).getCooldownTracker().setCooldown(this, (int)
			// (this.getFiringSpeed(stack, living)/50 *
			// (TF2ConfigVars.fastItemCooldown ? 1f:
			// getData(stack).getFloat(PropertyType.COOLDOWN_LONG))));
		}
		return true;
	}
}
