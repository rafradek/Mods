package rafradek.TF2weapons.entity.mercenary;

import net.minecraft.entity.IEntityLivingData;

public class TF2CharacterAdditionalData implements IEntityLivingData {

	public int team;
	public boolean natural;
	public boolean spawnDay;
	public boolean noEquipment;
	public boolean allowGiant = true;
	public boolean isGiant;
}
