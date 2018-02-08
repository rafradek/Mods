package rafradek.TF2weapons.weapons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import rafradek.TF2weapons.ItemFromData;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData.PropertyType;

public class ItemAmmoPackage extends Item{

	public static final int[] AMMO_PACKAGE_MIN=new int[]{0,3,9,9,3,18,1,2,2,18,0,2,0,2,1};
	public static final int[] AMMO_PACKAGE_MAX=new int[]{0,3,9,9,4,20,2,3,3,20,0,3,0,3,2};
	public ItemAmmoPackage() {
		this.setHasSubtypes(true);
		this.setMaxStackSize(1);
		this.setCreativeTab(TF2weapons.tabsurvivaltf2);
	}
	@Override
	public void getSubItems( CreativeTabs par2CreativeTabs, NonNullList<ItemStack> par3List) {
		if(!this.isInCreativeTab(par2CreativeTabs))
			return;
		par3List.add(new ItemStack(this,1,1));
	}
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		ItemStack itemStackIn=playerIn.getHeldItem(hand);
		if(!worldIn.isRemote){
			for(ItemStack stack:playerIn.inventory.mainInventory){
				if(!stack.isEmpty() && stack.getItem() instanceof ItemFromData&& ItemFromData.getData(stack).getInt(PropertyType.AMMO_TYPE)!=0){
					itemStackIn.setItemDamage(ItemFromData.getData(stack).getInt(PropertyType.AMMO_TYPE));
					break;
				}
			}
			ItemStack out=convertPackage(itemStackIn,playerIn);
			if(playerIn.inventory.addItemStackToInventory(out))
				itemStackIn.shrink(1);
	    }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }
	public static ItemStack convertPackage(ItemStack stack,EntityPlayer player){
		
		if(stack.getMetadata()==10){
			stack=new ItemStack(TF2weapons.itemAmmoFire,1);
			stack.setItemDamage((int)(stack.getMaxDamage()*(0.3f+player.getRNG().nextFloat()*0.3f)));
			return stack;
		}
		else if(stack.getMetadata()==12){
			stack=new ItemStack(TF2weapons.itemAmmoMedigun,1,(int)(stack.getMaxDamage()*(0.4f+player.getRNG().nextFloat()*0.4f)));
			stack.setItemDamage((int)(stack.getMaxDamage()*(0.3f+player.getRNG().nextFloat()*0.3f)));
			return stack;
		}
		else{
			return new ItemStack(TF2weapons.itemAmmo,AMMO_PACKAGE_MIN[stack.getMetadata()]+player.getRNG().nextInt(AMMO_PACKAGE_MAX[stack.getMetadata()]+1),stack.getMetadata());
		}
	}
}
