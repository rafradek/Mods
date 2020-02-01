package rafradek.TF2weapons.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public interface IEntityConfigurable {

	public NBTTagCompound writeConfig(NBTTagCompound tag);
	public void readConfig(NBTTagCompound tag);

	public EntityOutputManager getOutputManager();
	default public void activateOutput(String output) {
		this.activateOutput(output, 1f, 1);
	}
	default public void activateOutput(String output, float power,int minTime) {
		this.getOutputManager().activateOutput(output, power, minTime);
	};
	
	default public String getLinkName() {
		return this.getOutputManager().name;
	}
	
	public String[] getOutputs();
	
	default public String[] getAllowedValues(String attribute) {
		return null;
	}
}
