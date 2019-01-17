package rafradek.TF2weapons.inventory;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import rafradek.TF2weapons.TF2PlayerCapability;
import rafradek.TF2weapons.entity.mercenary.EntityTF2Character;

public class InventoryLoadout extends ItemStackHandler {

	EntityLivingBase living;
	public InventoryLoadout(int size, EntityLivingBase ent) {
		super(size);
		living = ent;
	}
	
	@Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
		ItemStack old = this.getStackInSlot(slot);
		super.setStackInSlot(slot, stack);
		if (!old.isEmpty())
			living.getAttributeMap().removeAttributeModifiers(old.getAttributeModifiers(old.getItem().getEquipmentSlot(old)));
		if (!stack.isEmpty()) {
			Multimap<String, AttributeModifier> modifiers = stack.getAttributeModifiers(stack.getItem().getEquipmentSlot(stack));
			modifiers.removeAll(SharedMonsterAttributes.ARMOR.getName());
			modifiers.removeAll(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName());
			living.getAttributeMap().applyAttributeModifiers(modifiers);
		}
    }
}
