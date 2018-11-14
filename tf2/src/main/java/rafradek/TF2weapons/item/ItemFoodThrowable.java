package rafradek.TF2weapons.item;

import com.google.common.collect.Iterables;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rafradek.TF2weapons.TF2ConfigVars;

public class ItemFoodThrowable extends ItemFood {

	public int waitTime;
	
	public ItemFoodThrowable(int amount, float saturation, boolean isWolfFood, int waitTime) {
		super(amount, saturation, isWolfFood);
		this.waitTime = waitTime;
		this.setAlwaysEdible();
		// TODO Auto-generated constructor stub
	}

	public boolean onEntityItemUpdate(EntityItem entityItem) {
		EntityLivingBase living = Iterables.getFirst(entityItem.world.getEntitiesWithinAABB(EntityLivingBase.class, entityItem.getEntityBoundingBox(), (test) -> {
			return !(test instanceof EntityPlayer) && test.getHealth() < test.getMaxHealth() && test.isEntityAlive();
		}), null);
		
		if(living != null) {
			living.heal(living.getMaxHealth()*this.getHealAmount(entityItem.getItem())/28f);
			entityItem.getItem().shrink(1);
		}
        return false;
    }
	
	/*@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		
		if (!TF2ConfigVars.fastItemCooldown) {
			if (player.getCooldownTracker().hasCooldown(this))
				i
			else
				player.getCooldownTracker().setCooldown(this, waitTime);
		}
		return true;
    }*/
	
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack previous = playerIn.getHeldItem(handIn);
		ActionResult<ItemStack> result = super.onItemRightClick(worldIn, playerIn, handIn);
		if (!worldIn.isRemote && !TF2ConfigVars.fastItemCooldown && result.getType() == EnumActionResult.SUCCESS) {
			playerIn.getCooldownTracker().setCooldown(this, waitTime);
		}
		if (TF2ConfigVars.freeUseItems)
			result.getResult().setCount(previous.getCount());
		return result;
    }
}
