package rafradek.TF2weapons.weapons;

import java.util.List;

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

	public static int[] ammoPackageMin=new int[]{0,2,6,6,2,12,1,2,2,12,0,2,0,1,1};
	public static int[] ammoPackageMax=new int[]{0,2,6,6,3,13,1,1,1,13,0,1,0,2,1};
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
			stack.setItemDamage((int)(stack.getMaxDamage()*(0.4f+player.getRNG().nextFloat()*0.4f)));
			return stack;
		}
		else if(stack.getMetadata()==12){
			stack=new ItemStack(TF2weapons.itemAmmoMedigun,1,(int)(stack.getMaxDamage()*(0.4f+player.getRNG().nextFloat()*0.4f)));
			stack.setItemDamage((int)(stack.getMaxDamage()*(0.4f+player.getRNG().nextFloat()*0.4f)));
			return stack;
		}
		else{
			return new ItemStack(TF2weapons.itemAmmo,ItemAmmoPackage.ammoPackageMin[stack.getMetadata()]+player.getRNG().nextInt(ItemAmmoPackage.ammoPackageMax[stack.getMetadata()]+1),stack.getMetadata());
		}
	}
}
