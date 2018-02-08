package rafradek.TF2weapons.building;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDispenser extends ContainerEnergy {

	public ContainerDispenser(EntityDispenser building, InventoryPlayer inv) {
		super(building, inv);
		for (int k = 0; k < 3; ++k)
			for (int i1 = 0; i1 < 3; ++i1)
				this.addSlotToContainer(new SlotItemHandler(building.items, i1 + k * 3, 80 + i1 * 18, 15 + k * 18));
	}

}
