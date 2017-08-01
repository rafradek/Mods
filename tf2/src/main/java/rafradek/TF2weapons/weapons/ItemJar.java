package rafradek.TF2weapons.weapons;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.message.TF2Message.PredictionMessage;

public class ItemJar extends ItemProjectileWeapon {

	public ItemJar() {
		super();
		this.setMaxStackSize(64);
		this.addPropertyOverride(new ResourceLocation("empty"), new IItemPropertyGetter() {
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				if (stack.getTagCompound().getBoolean("IsEmpty"))
					return 1;
				return 0;
			}
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab() {
		return TF2weapons.tabutilitytf2;
	}

	@Override
	public boolean canFire(World world, EntityLivingBase living, ItemStack stack) {
		return !stack.getTagCompound().getBoolean("IsEmpty") && super.canFire(world, living, stack);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		String string = super.getItemStackDisplayName(stack);
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("IsEmpty"))
			string = "Empty Jar - ".concat(string);
		return string;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip,
			ITooltipFlag advanced) {
		if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("IsEmpty"))
			tooltip.add("Right click to fill the container");
		super.addInformation(stack, world, tooltip, advanced);
	}

	@Override
	public boolean use(ItemStack stack, EntityLivingBase living, World world, EnumHand hand,
			PredictionMessage message) {
		if (super.use(stack, living, world, hand, message) && !world.isRemote) {
			stack.shrink(1);
		}
		return true;
	}

	/*@Override
	@SideOnly(Side.CLIENT)
	public boolean showDurabilityBar(ItemStack stack) {
		Integer value = Minecraft.getMinecraft().player.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool
				.get(getData(stack).getName());
		return stack.getTagCompound().getBoolean("IsEmpty") && value != null && value > 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getDurabilityForDisplay(ItemStack stack) {
		Integer value = Minecraft.getMinecraft().player.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool
				.get(getData(stack).getName());
		return (double) (value != null ? value : 0) / (double) 1200;
	}*/

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 40;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}

	@Override
	@Nullable
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		if (!(entityLiving instanceof EntityPlayer && ((EntityPlayer) entityLiving).capabilities.isCreativeMode))
			stack.shrink(1);

		if (entityLiving instanceof EntityPlayer) {
			((EntityPlayer) entityLiving).getCooldownTracker().setCooldown(this, 1360);
			//entityLiving.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool.put(getData(stack).getName(), 1700);
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			ItemStack newStack = stack.copy();
			newStack.setCount( 1);
			newStack.getTagCompound().setBoolean("IsEmpty", false);
			if (!entityplayer.inventory.addItemStackToInventory(newStack))
				entityplayer.dropItem(newStack, true);
		}

		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn,
			EnumHand hand) {
		ItemStack itemStackIn= playerIn.getHeldItem(hand);
		/*Integer value = playerIn.getCapability(TF2weapons.WEAPONS_CAP, null).effectsCool
				.get(getData(itemStackIn).getName());*/
		if (itemStackIn.getTagCompound().getBoolean("IsEmpty") /*&& (value == null || value <= 0)*/) {
			playerIn.setActiveHand(hand);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
	}

	@Override
	public boolean doMuzzleFlash(ItemStack stack, EntityLivingBase attacker, EnumHand hand) {
		return false;
	}
}
