package rafradek.rig;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.base.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class RIGUpgrade {

	
	public static ArrayList<RIGUpgrade> upgrades = new ArrayList<>();
	public static HashMap<String, RIGUpgrade> upgradesMap = new HashMap<>();
	public int id;
	public String name;
	public Predicate<ItemStack> predicate;
	
	public static final Predicate<ItemStack> RIG = new Predicate<ItemStack>() {

		@Override
		public boolean apply(ItemStack input) {
			// TODO Auto-generated method stub
			return input.getItem() instanceof ItemRIG;
		}
		
	};
	
	static {
		new RIGUpgrade(0, "Health", RIG);
		new RIGUpgrade(1, "Air", RIG);
	}
	
	public RIGUpgrade(int id, String name,Predicate<ItemStack> predicate) {
		upgrades.add(this);
		upgradesMap.put(name, this);
		this.id=id;
		this.name=name;
		this.predicate=predicate;
	}
	
	public static ArrayList<RIGUpgrade> getUpgrades(ItemStack stack) {
		ArrayList<RIGUpgrade> upgrades=new ArrayList<>();
		for(RIGUpgrade upgrade : RIGUpgrade.upgrades) {
			if(upgrade.predicate.apply(stack)) {
				upgrades.add(upgrade);
			}
		}
		return upgrades;
	}
	
	public int getAttributeValue(ItemStack stack) {
		if(!stack.isEmpty() && stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getOrCreateSubCompound("Upgrades");
			return tag.getInteger(Integer.toString(this.id));
		}
		return 0;
	}
}
