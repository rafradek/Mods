package rafradek.TF2weapons;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemArmorTF2 extends ItemArmor {

	public String description;
	public ItemArmorTF2(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn,String description) {
		super(materialIn, renderIndexIn, equipmentSlotIn);
		this.description=description;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par2List,
			boolean par4) {
		par2List.add(description);
	}

}
