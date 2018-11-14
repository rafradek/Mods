package rafradek.TF2weapons.item.crafting;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public interface IRecipeTF2 {
	
	@Nonnull
	public ItemStack getSuggestion(int slot);
}
