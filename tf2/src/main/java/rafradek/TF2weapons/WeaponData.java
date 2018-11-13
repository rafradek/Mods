package rafradek.TF2weapons;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import rafradek.TF2weapons.weapons.ItemKillstreakKit;

public class WeaponData implements ICapabilityProvider {

	private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(WeaponData.class, new WeaponData.Serializer()).create();

	
	public static PropertyType<?>[] propertyTypes = new PropertyType[256];
	public static Map<String, JsonDeserializer<ICapabilityProvider>> propertyDeserializers;
	
	public static class Serializer implements JsonDeserializer<WeaponData> {

		@Override
		public WeaponData deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			WeaponData data = new WeaponData();
			//Map<ResourceLocation, ICapabilityProvider> providers = new HashMap<>();
			
			for (Entry<String, JsonElement> property : json.getAsJsonObject().entrySet())
				/*if (propertyDeserializers.containsKey(property.getKey()))
					providers.put(new ResourceLocation(property.getKey()), propertyDeserializers.get(property.getKey()).deserialize(property.getValue(), typeOfT, context));
				else
					data.addProperty(property.getKey(), property.getValue().getAsString());*/
				data.addProperty(property.getKey(), property.getValue(), context);
			
			//data.addCapabilities(providers);
			return data;
		}

	}
	
	public static abstract class SpecialProperty implements ICapabilityProvider {
		
		public abstract void serialize(DataOutput buf, WeaponData data) throws IOException;
		public abstract void deserialize(DataInput buf, WeaponData data) throws IOException;
	}
	
	public static class PropertyType<T> implements JsonDeserializer<T>{

		public static final PropertyType<String> FIRE_SOUND = new PropertyType<>(3, "Fire sound", String.class);
		public static final PropertyType<String> RENDER = new PropertyType<>(2, "Render", String.class);
		public static final PropertyType<Integer> SLOT = new PropertyType<>(1, "Slot", Integer.class);
		public static final PropertyType<String> NAME = new PropertyType<>(0, "Name", String.class);
		public static final PropertyType<String> RELOAD_SOUND = new PropertyType<>(4, "Reload sound", String.class);
		public static final PropertyType<Integer> FIRE_SPEED = new PropertyType<>(5, "Firing speed", Integer.class);
		public static final PropertyType<Integer> PELLETS = new PropertyType<>(6, "Pellets", Integer.class);
		public static final PropertyType<Float> DAMAGE = new PropertyType<>(7, "Damage", Float.class);
		public static final PropertyType<Float> MAX_DAMAGE = new PropertyType<>(8, "Max damage", Float.class);
		public static final PropertyType<Float> MIN_DAMAGE = new PropertyType<>(9, "Min damage", Float.class);
		public static final PropertyType<Boolean> RANDOM_CRITS = new PropertyType<>(10, "Random crits", Boolean.class);
		public static final PropertyType<Boolean> RAPIDFIRE_CRITS = new PropertyType<>(11, "Rapidfire crits", Boolean.class);
		public static final PropertyType<Float> DAMAGE_FALOFF = new PropertyType<>(12, "Damage falloff", Float.class);
		public static final PropertyType<Boolean> RELOADS_CLIP = new PropertyType<>(13, "Reloads clip", Boolean.class);
		public static final PropertyType<Boolean> RELOADS_FULL_CLIP = new PropertyType<>(14, "Reloads full clip", Boolean.class);
		public static final PropertyType<Integer> RELOAD_TIME_FIRST = new PropertyType<>(15, "Reload time first", Integer.class);
		public static final PropertyType<Integer> RELOAD_TIME = new PropertyType<>(16, "Reload time", Integer.class);
		public static final PropertyType<Integer> CLIP_SIZE = new PropertyType<>(17, "Clip size", Integer.class);
		public static final PropertyType<Integer> KNOCKBACK = new PropertyType<>(18, "Knockback", Integer.class);
		public static final PropertyType<Boolean> SPREAD_RECOVERY = new PropertyType<>(19, "Spread recovery", Boolean.class);
		public static final PropertyType<Float> DUAL_WIELD_SPEED = new PropertyType<>(20, "Dual wield speed", Float.class);
		public static final PropertyType<String> FIRE_LOOP_SOUND = new PropertyType<>(21, "Fire loop sound", String.class);
		public static final PropertyType<String> SPIN_SOUND = new PropertyType<>(22, "Spin sound", String.class);
		public static final PropertyType<String> WIND_UP_SOUND = new PropertyType<>(23, "Wind up sound", String.class);
		public static final PropertyType<String> WIND_DOWN_SOUND = new PropertyType<>(24, "Wind down sound", String.class);
		public static final PropertyType<String> FIRE_START_SOUND = new PropertyType<>(25, "Fire start sound", String.class);
		public static final PropertyType<String> FIRE_STOP_SOUND = new PropertyType<>(26, "Fire stop sound", String.class);
		public static final PropertyType<String> AIRBLAST_SOUND = new PropertyType<>(27, "Airblast sound", String.class);
		public static final PropertyType<String> AIRBLAST_ROCKET_SOUND = new PropertyType<>(37, "Airblast rocket sound",
				String.class);
		public static final PropertyType<Boolean> HIDDEN = new PropertyType<>(38, "Hidden", Boolean.class);
		public static final PropertyType<String> CLOAK_SOUND = new PropertyType<>(28, "Cloak sound", String.class);
		public static final PropertyType<String> DECLOAK_SOUND = new PropertyType<>(29, "Decloak sound", String.class);
		public static final PropertyType<String> BUILD_HIT_SUCCESS_SOUND = new PropertyType<>(30, "Build hit success sound",
				String.class);
		public static final PropertyType<String> BUILD_HIT_FAIL_SOUND = new PropertyType<>(31, "Build hit fail sound",
				String.class);
		public static final PropertyType<String> EFFECT_TYPE = new PropertyType<>(32, "Effect type", String.class);
		public static final PropertyType<Integer> DURATION = new PropertyType<>(33, "Duration", Integer.class);
		public static final PropertyType<Integer> COOLDOWN = new PropertyType<>(34, "Cooldown", Integer.class);
		public static final PropertyType<String> HEAL_START_SOUND = new PropertyType<>(35, "Heal start sound", String.class);
		public static final PropertyType<String> NO_TARGET_SOUND = new PropertyType<>(36, "No target sound", String.class);
		public static final PropertyType<String> CHARGED_SOUND = new PropertyType<>(39, "Charged sound", String.class);
		public static final PropertyType<String> UBER_START_SOUND = new PropertyType<>(40, "Uber start sound", String.class);
		public static final PropertyType<String> UBER_STOP_SOUND = new PropertyType<>(41, "Uber stop sound", String.class);
		public static final PropertyType<Float> HEAL = new PropertyType<>(42, "Heal", Float.class);
		public static final PropertyType<Float> MAX_OVERHEAL = new PropertyType<>(43, "Max overheal", Float.class);
		public static final PropertyType<Float> RANGE = new PropertyType<>(44, "Range", Float.class);
		public static final PropertyType<Float> PROJECTILE_SPEED = new PropertyType<>(45, "Projectile speed", Float.class);
		public static final PropertyType<String> PROJECTILE = new PropertyType<>(46, "Projectile", String.class);
		public static final PropertyType<String> BASED_ON = new PropertyType<>(47, "Based on", String.class);
		public static final PropertyType<String> RENDER_BACKSTAB = new PropertyType<>(48, "Render backstab", String.class);
		public static final PropertyType<String> CHARGE_SOUND = new PropertyType<>(49, "Charge sound", String.class);
		public static final PropertyType<String> DETONATE_SOUND = new PropertyType<>(50, "Detonate sound", String.class);
		public static final PropertyType<String> CLASS = new PropertyType<>(51, "Class", String.class);
		public static final PropertyType<Float> SPREAD = new PropertyType<>(52, "Spread", Float.class);
		public static final PropertyType<String> HIT_SOUND = new PropertyType<>(53, "Hit sound", String.class);
		public static final PropertyType<Integer> AMMO_TYPE = new PropertyType<>(54, "Ammo type", Integer.class);
		public static final PropertyType<Integer> ROLL_HIDDEN = new PropertyType<>(55, "Roll hidden", Integer.class);
		public static final PropertyType<String> MOB_TYPE = new PropertyType<>(56, "Mobs", String.class);
		public static final PropertyType<Float> RECOIL = new PropertyType<>(57, "Recoil", Float.class);
		public static final PropertyType<Float> DROP_CHANCE = new PropertyType<>(58, "Drop chance", Float.class);
		public static final PropertyType<Integer> COST = new PropertyType<>(59, "Cost", Integer.class);
		public static final PropertyType<Integer> WEAR = new PropertyType<>(60, "Wear flags", Integer.class);
		public static final PropertyType<String> ARMOR_IMAGE = new PropertyType<>(61, "Overlay", String.class);
		public static final PropertyType<String> HEAD_MODEL = new PropertyType<>(62, "Head model", String.class);
		public static final PropertyType<String> BODY_MODEL = new PropertyType<>(63, "Body model", String.class);
		public static final PropertyType<Float> ARMOR = new PropertyType<>(64, "Armor", Float.class);
		public static final PropertyType<Float> ARMOR_TOUGHNESS = new PropertyType<>(65, "Armor toughness", Float.class);
		public static final PropertyType<String> HORN_RED_SOUND = new PropertyType<>(66, "Horn red sound", String.class);
		public static final PropertyType<String> HORN_BLU_SOUND = new PropertyType<>(67, "Horn blu sound", String.class);
		public static final PropertyType<String> DESC = new PropertyType<>(68, "Description", String.class);
		public static final PropertyType<String> HIT_WORLD_SOUND = new PropertyType<>(69, "Hit world sound", String.class);
		public static final PropertyType<String> HIT_LOOP_SOUND = new PropertyType<>(70, "Hit loop sound", String.class);
		public static final PropertyType<Float> EFFICIENT_RANGE = new PropertyType<>(71, "Efficient range", Float.class);
		public static final PropertyType<Boolean> STOCK = new PropertyType<>(72, "Stock", Boolean.class);
		public static final PropertyType<String> NO_FIRE_SOUND = new PropertyType<>(73, "No fire sound", String.class);
		public static final PropertyType<String> CHARGED_FIRE_SOUND = new PropertyType<>(73, "Charged fire sound", String.class);
		public static final PropertyType<Boolean> PENETRATE = new PropertyType<>(74, "Penetrate", Boolean.class);
		public static final PropertyType<String> OVERRIDE = new PropertyType<>(75, "Weapon override", String.class);
		public static final PropertyType<Integer> MAX_AMMO = new PropertyType<>(76, "Max ammo", Integer.class);
		public static final PropertyType<String> EXPLOSION_SOUND = new PropertyType<>(77, "Explosion sound", String.class);
		public static final PropertyType<Float> ARMOR_PEN_SCALE= new PropertyType<>(78, "Armor penetration scale", Float.class);
		public static final PropertyType<String> SPECIAL_1_SOUND = new PropertyType<>(79, "Special 1 sound", String.class);
		public static final PropertyType<String> SPECIAL_2_SOUND = new PropertyType<>(80, "Special 2 sound", String.class);
		public static final PropertyType<ItemFromData.AttributeProvider> ATTRIBUTES = new ItemFromData.PropertyAttribute(81, "Attributes", ItemFromData.AttributeProvider.class);
		public static final PropertyType<ItemCrate.CrateContent> CONTENT = new ItemCrate.PropertyContent(82, "Content", ItemCrate.CrateContent.class);
		public Class<T> type;
		public int id;
		public String name;

		public PropertyType(int id, String name, Class<T> type) {
			this.name = name;
			this.id = id;
			this.type = type;
			propertyTypes[id] = this;
			MapList.propertyTypes.put(name, this);
		}

		public static void init() {

		}

		public void serialize(DataOutput buf, WeaponData data, T value) throws IOException {
			
			if (this.type == Boolean.class)
				buf.writeBoolean((Boolean) value);
			else if (this.type == Integer.class)
				buf.writeInt((Integer) value);
			else if (this.type == Float.class)
				buf.writeFloat((Float) value);
			else if (this.type == String.class)
				buf.writeUTF((String) value);
		}

		public T deserialize(DataInput buf, WeaponData data) throws IOException {
			T prop = null;
			if (this.type == Boolean.class)
				prop = type.cast(buf.readBoolean());
			else if (this.type == Integer.class)
				prop = type.cast(buf.readInt());
			else if (this.type == Float.class)
				prop = type.cast(buf.readFloat());
			else if (this.type == String.class)
				prop = type.cast(buf.readUTF());
			return prop;
			//data.properties.put(this, prop);
		}

		/*public Object fromString(String string) {
			// TODO Auto-generated method stub
			Object property = null;
			try {
				switch (type) {
				case BOOLEAN:
					property = string.equals("true");
					break;
				case INTEGER:
					property = Integer.parseInt(string);
					break;
				case STRING:
					property = string;
					break;
				case DOUBLE:
					property = Float.parseFloat(string);
					break;
				default:
				}
			} catch (NumberFormatException e) {
				System.err.println("Failed to parse property value: " + string + " key: " + this.name);
			}
			return property;
		}*/

		public boolean hasKey(WeaponData data) {
			return data.properties.get(this) != null;
		}

		@Override
		public T deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			
			if (type == Boolean.class)
				return type.cast(json.getAsBoolean());
			else if (type == Integer.class)
				return type.cast(json.getAsInt());
			else if (type == String.class)
				return type.cast(json.getAsString());
			else if (type == Float.class)
				return type.cast(json.getAsFloat());
			return null;
		}
	}

	public HashMap<PropertyType<?>, Object> properties;

	public CapabilityDispatcher capabilities;

	public int maxCrateValue;
	private String name;

	public WeaponData() {
		this.properties = new HashMap<>();
		
	}
	
	public WeaponData(String name) {
		this();
		this.name = name;
	}

	public void addCapabilities(Map<ResourceLocation, ICapabilityProvider> map) {
		this.capabilities = new CapabilityDispatcher(map);
	}
	
	public static class Property<T> {

		public float floatValue;
		public int intValue;
		public String stringValue = "";
		public boolean booleanValue;

		public T value;
		/*
		 * public Property(){ setValue() } public abstract T getValue(); public
		 * abstract void setValue(T value); public abstract void
		 * fromString(String string); public abstract void serialize(ByteBuf
		 * buf); public abstract void deserialize(ByteBuf buf);
		 */
	}

	public int getInt(PropertyType<Integer> propType) {
		Integer property = (Integer) this.properties.get(propType);
		if (property != null)
			return property;
		return 0;
	}

	public String getString(PropertyType<String> propType) {
		String property = (String) this.properties.get(propType);
		if (property != null)
			return property;
		return "";
	}

	public boolean getBoolean(PropertyType<Boolean> propType) {
		Boolean property = (Boolean) this.properties.get(propType);
		if (property != null)
			return property;
		return false;
	}

	public float getFloat(PropertyType<Float> propType) {
		Float property = (Float) this.properties.get(propType);
		if (property != null)
			return property;
		return 0f;
	}

	@SuppressWarnings("unchecked")
	public <A> A get(PropertyType<A> propType) {
		A property = (A) (this.properties.get(propType));
		return property;
	}
	
	@SuppressWarnings("unchecked")
	public <A> A get(PropertyType<A> propType, A def) {
		A property = (A) (this.properties.get(propType));
		if (property != null)
			return property;
		return def;
	}
	
	public boolean hasProperty(PropertyType<?> property) {
		return this.properties.containsKey(property);
	}
	/*
	 * public static class PropertyDouble implements Property<Double>{ private
	 * double value; PropertyDouble(double value){ this.value=value; }
	 * 
	 * @Override public Double getValue() { // TODO Auto-generated method stub
	 * return value; }
	 * 
	 * @Override public void fromString(String string) { // TODO Auto-generated
	 * method stub value } }
	 * 
	 * public static class PropertyInt implements Property<Integer>{ private int
	 * value;
	 * 
	 * PropertyInt(int value){ this.value=value; }
	 * 
	 * @Override public Integer getValue() { // TODO Auto-generated method stub
	 * return value; } }
	 * 
	 * public static class PropertyString implements Property<String>{ private
	 * String value;
	 * 
	 * PropertyString(String value){ this.value=value; }
	 * 
	 * @Override public String getValue() { // TODO Auto-generated method stub
	 * return value; } }
	 * 
	 * public static class PropertyBoolean implements Property{ private Boolean
	 * value;
	 * 
	 * PropertyBoolean(Boolean value){ this.value=value; }
	 * 
	 * @Override public Boolean getValue() { // TODO Auto-generated method stub
	 * return value; } }
	 */

	public void addProperty(String name, JsonElement element, JsonDeserializationContext context) {

		PropertyType<?> propType = MapList.propertyTypes.get(name);
		this.properties.put(propType, propType.deserialize(element, propType.type, context));
		//this.properties.put(type, type.fromString(string));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static ArrayList<WeaponData> parseFile(File file) {
		ArrayList<WeaponData> list = new ArrayList<>();
		
		try {
			String s = Files.toString(file, Charsets.UTF_8);
			JsonObject tree = new JsonParser().parse(s).getAsJsonObject();
			for (Entry<String, JsonElement> entry : tree.getAsJsonObject().entrySet()) {
				WeaponData data = GSON.fromJson(entry.getValue(), WeaponData.class);
				data.name = entry.getKey();
				
				list.add(data);
			}
		} catch (Exception e) {
			TF2weapons.LOGGER.error("Skipped reading weapon data from file: {}", file.getName());
			TF2weapons.LOGGER.catching(Level.ERROR, e);
		}
		return list;
	}
	public static class WeaponDataCapability implements ICapabilityProvider{

		public WeaponData inst=ItemFromData.BLANK_DATA;
		public HashMap<String, Float> cachedAttrMult = new HashMap<>();
		public HashMap<String, Float> cachedAttrAdd = new HashMap<>();
		public boolean cached=false;
		public int active;
		public int usedClass = -1;
		public int fire1Cool = 0;
		public int fire2Cool = 0;
		/*public static WeaponData get(ItemStack stack) {
			WeaponData value=ItemFromData.BLANK_DATA;
			if(!stack.isEmpty() && stack.hasCapability(TF2weapons.WEAPONS_DATA_CAP, null)) {
				value=stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).inst;
				if (value == ItemFromData.BLANK_DATA && stack.hasTagCompound() && MapList.nameToData.containsKey(stack.getTagCompound().getString("Type")))
					value = stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null).inst = MapList.nameToData.get(stack.getTagCompound().getString("Type"));
			}
			return value;
		}*/
		/*@Override
		public NBTTagByte serializeNBT() {
			// TODO Auto-generated method stub
			if(inst!=ItemFromData.BLANK_DATA)
				return new NBTTagString(inst.getName());
			return new NBTTagString("toloadfiles");
		}

		@Override
		public void deserializeNBT(NBTTagByte nbt) {
			if(nbt != null && !nbt.getString().equals("toloadfiles"))
				inst=MapList.nameToData.get(nbt.getString());
			if(inst==null)
				inst=ItemFromData.BLANK_DATA;
		}*/

		public float getAttributeValue(ItemStack stack,String nameattr, float initial) {
			if(!cached) {
				NBTTagCompound attributelist;
				cachedAttrMult.clear();
				cachedAttrAdd.clear();
				attributelist = MapList.buildInAttributes.get(ItemFromData.getData(stack).getName());
				if(attributelist != null)
					for(String name : attributelist.getKeySet()) {
						NBTBase tag = attributelist.getTag(name);
						if (tag instanceof NBTTagFloat) {
							TF2Attribute attribute = TF2Attribute.attributes[Integer.parseInt(name)];
							
							if (attribute.typeOfValue == TF2Attribute.Type.ADDITIVE) {
								if (!cachedAttrAdd.containsKey(attribute.effect))
									cachedAttrAdd.put(attribute.effect, 0f);
								cachedAttrAdd.put(attribute.effect, cachedAttrAdd.get(attribute.effect)+((NBTTagFloat) tag).getFloat());
							}
							else {
								if (!cachedAttrMult.containsKey(attribute.effect))
									cachedAttrMult.put(attribute.effect, 1f);
								cachedAttrMult.put(attribute.effect, cachedAttrMult.get(attribute.effect)*((NBTTagFloat) tag).getFloat());
							}
						}
					}
				if(stack.hasTagCompound()) {
					attributelist=stack.getTagCompound().getCompoundTag("Attributes");
					for(String name : attributelist.getKeySet()) {
						NBTBase tag = attributelist.getTag(name);
						if (tag instanceof NBTTagFloat) {
							TF2Attribute attribute = TF2Attribute.attributes[Integer.parseInt(name)];
							
							if (attribute.typeOfValue == TF2Attribute.Type.ADDITIVE) {
								if (!cachedAttrAdd.containsKey(attribute.effect))
									cachedAttrAdd.put(attribute.effect, 0f);
								cachedAttrAdd.put(attribute.effect, cachedAttrAdd.get(attribute.effect)+((NBTTagFloat) tag).getFloat());
							}
							else {
								if (!cachedAttrMult.containsKey(attribute.effect))
									cachedAttrMult.put(attribute.effect, 1f);
								cachedAttrMult.put(attribute.effect, cachedAttrMult.get(attribute.effect)*((NBTTagFloat) tag).getFloat());
							}
								
						}
					}
					if (stack.getTagCompound().hasKey(NBTLiterals.STREAK_ATTRIB)) {
						TF2Attribute attribute = TF2Attribute.attributes[stack.getTagCompound().getShort(NBTLiterals.STREAK_ATTRIB)];
						float value = ItemKillstreakKit.getKillstreakBonus(attribute, stack.getTagCompound().getByte(NBTLiterals.STREAK_LEVEL),
								stack.getTagCompound().getInteger(NBTLiterals.STREAK_KILLS));
						if (attribute.typeOfValue == TF2Attribute.Type.ADDITIVE) {
							if (!cachedAttrAdd.containsKey(attribute.effect))
								cachedAttrAdd.put(attribute.effect, 0f);
							cachedAttrAdd.put(attribute.effect, cachedAttrAdd.get(attribute.effect)+value);
						}
						else {
							if (!cachedAttrMult.containsKey(attribute.effect))
								cachedAttrMult.put(attribute.effect, 1f);
							if (value > attribute.defaultValue)
								cachedAttrMult.put(attribute.effect, cachedAttrMult.get(attribute.effect) + value - attribute.defaultValue);
							else
								cachedAttrMult.put(attribute.effect, cachedAttrMult.get(attribute.effect)*value);
						}
					}
				}
				this.cached=true;
			}
			Float valueadd=cachedAttrAdd.get(nameattr);
			Float valuemult=cachedAttrMult.get(nameattr);
			if(valueadd == null)
				valueadd=Float.valueOf(0f);
			if(valuemult == null)
				valuemult=Float.valueOf(1f);
			return (initial+valueadd)*valuemult;
		}
		
		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			// TODO Auto-generated method stub
			//System.out.println("capinit: "+TF2weapons.WEAPONS_DATA_CAP);
			return TF2weapons.WEAPONS_DATA_CAP != null && capability == TF2weapons.WEAPONS_DATA_CAP;
		}

		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (TF2weapons.WEAPONS_DATA_CAP != null && capability == TF2weapons.WEAPONS_DATA_CAP)
				return TF2weapons.WEAPONS_DATA_CAP.cast(this);
			return null;
		}
		
	}
	
	public static WeaponDataCapability getCapability(ItemStack stack) {
		return stack.getCapability(TF2weapons.WEAPONS_DATA_CAP, null);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return capabilities.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		// TODO Auto-generated method stub
		return capabilities.getCapability(capability, facing);
	}
}
