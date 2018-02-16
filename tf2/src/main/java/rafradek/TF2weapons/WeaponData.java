package rafradek.TF2weapons;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.config.Property.Type;

public class WeaponData {

	public static final Gson DESERIALIZER = new Gson();

	public static PropertyType[] propertyTypes = new PropertyType[256];

	public static class Serializer implements JsonDeserializer<WeaponData> {

		@Override
		public WeaponData deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {

			return null;
		}

	}

	public static class PropertyType {

		public static final PropertyType FIRE_SOUND = new PropertyType(3, "Fire sound", Type.STRING);
		public static final PropertyType RENDER = new PropertyType(2, "Render", Type.STRING);
		public static final PropertyType SLOT = new PropertyType(1, "Slot", Type.INTEGER);
		public static final PropertyType NAME = new PropertyType(0, "Name", Type.STRING);
		public static final PropertyType RELOAD_SOUND = new PropertyType(4, "Reload sound", Type.STRING);
		public static final PropertyType FIRE_SPEED = new PropertyType(5, "Firing speed", Type.INTEGER);
		public static final PropertyType PELLETS = new PropertyType(6, "Pellets", Type.INTEGER);
		public static final PropertyType DAMAGE = new PropertyType(7, "Damage", Type.DOUBLE);
		public static final PropertyType MAX_DAMAGE = new PropertyType(8, "Max damage", Type.DOUBLE);
		public static final PropertyType MIN_DAMAGE = new PropertyType(9, "Min damage", Type.DOUBLE);
		public static final PropertyType RANDOM_CRITS = new PropertyType(10, "Random crits", Type.BOOLEAN);
		public static final PropertyType RAPIDFIRE_CRITS = new PropertyType(11, "Rapidfire crits", Type.BOOLEAN);
		public static final PropertyType DAMAGE_FALOFF = new PropertyType(12, "Damage falloff", Type.DOUBLE);
		public static final PropertyType RELOADS_CLIP = new PropertyType(13, "Reloads clip", Type.BOOLEAN);
		public static final PropertyType RELOADS_FULL_CLIP = new PropertyType(14, "Reloads full clip", Type.BOOLEAN);
		public static final PropertyType RELOAD_TIME_FIRST = new PropertyType(15, "Reload time first", Type.INTEGER);
		public static final PropertyType RELOAD_TIME = new PropertyType(16, "Reload time", Type.INTEGER);
		public static final PropertyType CLIP_SIZE = new PropertyType(17, "Clip size", Type.INTEGER);
		public static final PropertyType KNOCKBACK = new PropertyType(18, "Knockback", Type.INTEGER);
		public static final PropertyType SPREAD_RECOVERY = new PropertyType(19, "Spread recovery", Type.BOOLEAN);
		public static final PropertyType DUAL_WIELD_SPEED = new PropertyType(20, "Dual wield speed", Type.DOUBLE);
		public static final PropertyType FIRE_LOOP_SOUND = new PropertyType(21, "Fire loop sound", Type.STRING);
		public static final PropertyType SPIN_SOUND = new PropertyType(22, "Spin sound", Type.STRING);
		public static final PropertyType WIND_UP_SOUND = new PropertyType(23, "Wind up sound", Type.STRING);
		public static final PropertyType WIND_DOWN_SOUND = new PropertyType(24, "Wind down sound", Type.STRING);
		public static final PropertyType FIRE_START_SOUND = new PropertyType(25, "Fire start sound", Type.STRING);
		public static final PropertyType FIRE_STOP_SOUND = new PropertyType(26, "Fire stop sound", Type.STRING);
		public static final PropertyType AIRBLAST_SOUND = new PropertyType(27, "Airblast sound", Type.STRING);
		public static final PropertyType AIRBLAST_ROCKET_SOUND = new PropertyType(37, "Airblast rocket sound",
				Type.STRING);
		public static final PropertyType HIDDEN = new PropertyType(38, "Hidden", Type.BOOLEAN);
		public static final PropertyType CLOAK_SOUND = new PropertyType(28, "Cloak sound", Type.STRING);
		public static final PropertyType DECLOAK_SOUND = new PropertyType(29, "Decloak sound", Type.STRING);
		public static final PropertyType BUILD_HIT_SUCCESS_SOUND = new PropertyType(30, "Build hit success sound",
				Type.STRING);
		public static final PropertyType BUILD_HIT_FAIL_SOUND = new PropertyType(31, "Build hit fail sound",
				Type.STRING);
		public static final PropertyType EFFECT_TYPE = new PropertyType(32, "Effect type", Type.STRING);
		public static final PropertyType DURATION = new PropertyType(33, "Duration", Type.INTEGER);
		public static final PropertyType COOLDOWN = new PropertyType(34, "Cooldown", Type.INTEGER);
		public static final PropertyType HEAL_START_SOUND = new PropertyType(35, "Heal start sound", Type.STRING);
		public static final PropertyType NO_TARGET_SOUND = new PropertyType(36, "No target sound", Type.STRING);
		public static final PropertyType CHARGED_SOUND = new PropertyType(39, "Charged sound", Type.STRING);
		public static final PropertyType UBER_START_SOUND = new PropertyType(40, "Uber start sound", Type.STRING);
		public static final PropertyType UBER_STOP_SOUND = new PropertyType(41, "Uber stop sound", Type.STRING);
		public static final PropertyType HEAL = new PropertyType(42, "Heal", Type.DOUBLE);
		public static final PropertyType MAX_OVERHEAL = new PropertyType(43, "Max overheal", Type.DOUBLE);
		public static final PropertyType RANGE = new PropertyType(44, "Range", Type.DOUBLE);
		public static final PropertyType PROJECTILE_SPEED = new PropertyType(45, "Projectile speed", Type.DOUBLE);
		public static final PropertyType PROJECTILE = new PropertyType(46, "Projectile", Type.STRING);
		public static final PropertyType BASED_ON = new PropertyType(47, "Based on", Type.STRING);
		public static final PropertyType RENDER_BACKSTAB = new PropertyType(48, "Render backstab", Type.STRING);
		public static final PropertyType CHARGE_SOUND = new PropertyType(49, "Charge sound", Type.STRING);
		public static final PropertyType DETONATE_SOUND = new PropertyType(50, "Detonate sound", Type.STRING);
		public static final PropertyType CLASS = new PropertyType(51, "Class", Type.STRING);
		public static final PropertyType SPREAD = new PropertyType(52, "Spread", Type.DOUBLE);
		public static final PropertyType HIT_SOUND = new PropertyType(53, "Hit sound", Type.STRING);
		public static final PropertyType AMMO_TYPE = new PropertyType(54, "Ammo type", Type.INTEGER);
		public static final PropertyType ROLL_HIDDEN = new PropertyType(55, "Roll hidden", Type.INTEGER);
		public static final PropertyType MOB_TYPE = new PropertyType(56, "Mobs", Type.STRING);
		public static final PropertyType RECOIL = new PropertyType(57, "Recoil", Type.DOUBLE);
		public static final PropertyType DROP_CHANCE = new PropertyType(58, "Drop chance", Type.DOUBLE);
		public static final PropertyType COST = new PropertyType(59, "Cost", Type.INTEGER);
		public static final PropertyType WEAR = new PropertyType(60, "Wear flags", Type.INTEGER);
		public static final PropertyType ARMOR_IMAGE = new PropertyType(61, "Overlay", Type.STRING);
		public static final PropertyType HEAD_MODEL = new PropertyType(62, "Head model", Type.STRING);
		public static final PropertyType BODY_MODEL = new PropertyType(63, "Body model", Type.STRING);
		public static final PropertyType ARMOR = new PropertyType(64, "Armor", Type.DOUBLE);
		public static final PropertyType ARMOR_TOUGHNESS = new PropertyType(65, "Armor toughness", Type.DOUBLE);
		public static final PropertyType HORN_RED_SOUND = new PropertyType(66, "Horn red sound", Type.STRING);
		public static final PropertyType HORN_BLU_SOUND = new PropertyType(67, "Horn blu sound", Type.STRING);
		public static final PropertyType DESC = new PropertyType(68, "Description", Type.STRING);
		public static final PropertyType HIT_WORLD_SOUND = new PropertyType(69, "Hit world sound", Type.STRING);
		public static final PropertyType HIT_LOOP_SOUND = new PropertyType(70, "Hit loop sound", Type.STRING);
		public static final PropertyType EFFICIENT_RANGE = new PropertyType(71, "Efficient range", Type.DOUBLE);
		public static final PropertyType STOCK = new PropertyType(72, "Stock", Type.BOOLEAN);
		public static final PropertyType NO_FIRE_SOUND = new PropertyType(73, "No fire sound", Type.STRING);
		public static final PropertyType CHARGED_FIRE_SOUND = new PropertyType(73, "Charged fire sound", Type.STRING);
		public static final PropertyType PENETRATE = new PropertyType(74, "Penetrate", Type.BOOLEAN);
		public static final PropertyType OVERRIDE = new PropertyType(75, "Weapon override", Type.STRING);
		public static final PropertyType MAX_AMMO = new PropertyType(76, "Max ammo", Type.INTEGER);
		public Type type;
		public int id;
		public String name;

		private PropertyType(int id, String name, Type type) {
			this.name = name;
			this.id = id;
			this.type = type;
			propertyTypes[id] = this;
			MapList.propertyTypes.put(name, this);
		}

		public static void init() {

		}

		public int getInt(WeaponData data) {
			return data.properties.get(this).intValue;
		}

		public String getString(WeaponData data) {
			return data.properties.get(this).stringValue;
		}

		public boolean getBoolean(WeaponData data) {
			return data.properties.get(this).booleanValue;
		}

		public float getFloat(WeaponData data) {
			return data.properties.get(this).floatValue;
		}

		public void serialize(DataOutput buf, WeaponData data) throws IOException {
			buf.writeByte(this.id);
			switch (type) {
			case BOOLEAN:
				buf.writeBoolean(getBoolean(data));
				break;
			case INTEGER:
				buf.writeInt(getInt(data));
				break;
			case DOUBLE:
				buf.writeFloat(getFloat(data));
				break;
			case STRING:
				buf.writeUTF(getString(data));
				break;
			default:
			}
		}

		public void deserialize(DataInput buf, WeaponData data) throws IOException {
			Property prop = new Property();
			data.properties.put(this, prop);
			switch (type) {
			case BOOLEAN:
				prop.booleanValue = buf.readBoolean();
				break;
			case INTEGER:
				prop.intValue = buf.readInt();
				break;
			case DOUBLE:
				prop.floatValue = buf.readFloat();
				break;
			case STRING:
				prop.stringValue = buf.readUTF();
			default:
			}
		}

		public Property fromString(String string) {
			// TODO Auto-generated method stub
			Property property = new Property();
			try {
				switch (type) {
				case BOOLEAN:
					property.booleanValue = string.equals("true");
					break;
				case INTEGER:
					property.intValue = Integer.parseInt(string);
					break;
				case STRING:
					property.stringValue = string;
					break;
				case DOUBLE:
					property.floatValue = Float.parseFloat(string);
					break;
				default:
				}
			} catch (NumberFormatException e) {
				System.err.println("Failed to parse property value: " + string + " key: " + this.name);
			}
			return property;
		}

		public boolean hasKey(WeaponData data) {
			return data.properties.get(this) != null;
		}
	}

	public HashMap<PropertyType, Property> properties;

	public HashMap<TF2Attribute, Float> attributes;
	public HashMap<String, Integer> crateContent;

	public int maxCrateValue;
	private String name;

	public WeaponData(String name) {
		this.name = name;
		this.properties = new HashMap<>();
		this.attributes = new HashMap<>();
		this.crateContent = new HashMap<>();
	}

	public static class Property {

		public float floatValue;
		public int intValue;
		public String stringValue = "";
		public boolean booleanValue;

		/*
		 * public Property(){ setValue() } public abstract T getValue(); public
		 * abstract void setValue(T value); public abstract void
		 * fromString(String string); public abstract void serialize(ByteBuf
		 * buf); public abstract void deserialize(ByteBuf buf);
		 */
	}

	public int getInt(PropertyType propType) {
		Property property = this.properties.get(propType);
		if (property != null)
			return property.intValue;
		return 0;
	}

	public String getString(PropertyType propType) {
		Property property = this.properties.get(propType);
		if (property != null)
			return property.stringValue;
		return "";
	}

	public boolean getBoolean(PropertyType propType) {
		Property property = this.properties.get(propType);
		if (property != null)
			return property.booleanValue;
		return false;
	}

	public float getFloat(PropertyType propType) {
		Property property = this.properties.get(propType);
		if (property != null)
			return property.floatValue;
		return 0;
	}

	public boolean hasProperty(PropertyType property) {
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

	public void addProperty(String name, String string) {

		PropertyType type = MapList.propertyTypes.get(name);
		this.properties.put(type, type.fromString(string));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * public static ArrayList<WeaponData> parseFile(File file){
	 * 
	 * ArrayList<WeaponData> list=new ArrayList<>(); BufferedReader buffer =
	 * null; UnicodeInputStreamReader input = null; try { if (file.canRead()) {
	 * input = new UnicodeInputStreamReader(new FileInputStream(file), "UTF-8");
	 * buffer = new BufferedReader(input);
	 * 
	 * String line; WeaponData currentData = null; //Property.Type type = null;
	 * ArrayList<String> tmpList = null; int lineNum = 0; String name = null;
	 * 
	 * while (true) { lineNum++; line = buffer.readLine();
	 * 
	 * if (line == null) { break; }
	 * 
	 * int nameStart = -1, nameEnd = -1; boolean skip = false; boolean quoted =
	 * false; boolean isFirstNonWhitespaceCharOnLine = true;
	 * 
	 * for (int i = 0; i < line.length() && !skip; ++i) { if
	 * (Character.isLetterOrDigit(line.charAt(i)) ||
	 * Configuration.ALLOWED_CHARS.indexOf(line.charAt(i)) != -1 || (quoted &&
	 * line.charAt(i) != '"')) { if (nameStart == -1) { nameStart = i; }
	 * 
	 * nameEnd = i; isFirstNonWhitespaceCharOnLine = false; } else if
	 * (Character.isWhitespace(line.charAt(i))) { // ignore space characters }
	 * else { switch (line.charAt(i)) { case '#': if (tmpList != null) // allow
	 * special characters as part of string lists break; skip = true; continue;
	 * 
	 * case '"': if (tmpList != null) // allow special characters as part of
	 * string lists break; if (quoted) { quoted = false; } if (!quoted &&
	 * nameStart == -1) { quoted = true; } break;
	 * 
	 * case '{': if (tmpList != null) // allow special characters as part of
	 * string lists break; name = line.substring(nameStart, nameEnd + 1); name =
	 * name.toLowerCase(Locale.ENGLISH); currentData = new WeaponData(name);
	 * list.add(currentData); name = null;
	 * 
	 * break;
	 * 
	 * case '}': if (tmpList != null) // allow special characters as part of
	 * string lists break; if (currentData == null) { throw new
	 * RuntimeException(String.
	 * format("Config file corrupt, attempted to close to many categories '%s:%d'"
	 * , file.getName(), lineNum)); } currentData=null; break;
	 * 
	 * case '=': if (tmpList != null) // allow special characters as part of
	 * string lists break; name = line.substring(nameStart, nameEnd + 1);
	 * 
	 * if (currentData == null) { throw new
	 * RuntimeException(String.format("'%s' has no scope in '%s:%d'", name,
	 * file.getName(), lineNum)); }
	 * 
	 * //MapList.propertyTypes.get(name).fromString(line.substring(i + 1));
	 * currentData.addProperty(name, line.substring(i + 1)); //Property prop =
	 * new Property(); i = line.length();
	 * 
	 * 
	 * 
	 * break;
	 * 
	 * case ':': if (tmpList != null) // allow special characters as part of
	 * string lists break; //type =
	 * Property.Type.tryParse(line.substring(nameStart, nameEnd + 1).charAt(0));
	 * nameStart = nameEnd = -1; break;
	 * 
	 * case '<': if ((tmpList != null && i + 1 == line.length()) || (tmpList ==
	 * null && i + 1 != line.length())) { throw new
	 * RuntimeException(String.format("Malformed list property \"%s:%d\"",
	 * file.getName(), lineNum)); } else if (i + 1 == line.length()) { name =
	 * line.substring(nameStart, nameEnd + 1);
	 * 
	 * if (currentData == null) { throw new
	 * RuntimeException(String.format("'%s' has no scope in '%s:%d'", name,
	 * file.getName(), lineNum)); }
	 * 
	 * tmpList = new ArrayList<String>();
	 * 
	 * skip = true; }
	 * 
	 * break;
	 * 
	 * case '>': if (tmpList == null) { throw new
	 * RuntimeException(String.format("Malformed list property \"%s:%d\"",
	 * file.getName(), lineNum)); }
	 * 
	 * if (isFirstNonWhitespaceCharOnLine) { name = null; tmpList = null; } //
	 * else allow special characters as part of string lists break;
	 * 
	 * default: if (tmpList != null) // allow special characters as part of
	 * string lists break; throw new
	 * RuntimeException(String.format("Unknown character '%s' in '%s:%d'",
	 * line.charAt(i), file, lineNum)); } isFirstNonWhitespaceCharOnLine =
	 * false; } }
	 * 
	 * if (quoted) { throw new
	 * RuntimeException(String.format("Unmatched quote in '%s:%d'", file,
	 * lineNum)); } else if (tmpList != null && !skip) { String[]
	 * strTable=line.trim().split(":");
	 * 
	 * String attributeName=strTable[0]; String attributeValue=strTable[1];
	 * Iterator<String> iterator2 = MapList.nameToAttribute.keySet().iterator();
	 * //System.out.println("to je"+attributeName+" "+attributeValue); boolean
	 * has=false;
	 * 
	 * while(iterator2.hasNext()) { if(iterator2.next().equals(attributeName)){
	 * currentData.attributes.put(MapList.nameToAttribute.get(attributeName),
	 * Float.parseFloat(attributeValue)); has=true; } } if(has==false){
	 * currentData.attributes.put(TF2Attribute.attributes[Integer.parseInt(
	 * attributeName)], Float.parseFloat(attributeValue)); } } } } } catch
	 * (IOException e) { e.printStackTrace(); } finally { if (buffer != null) {
	 * try { buffer.close(); } catch (IOException e){} } if (input != null) {
	 * try { input.close(); } catch (IOException e){} } } return list; }
	 */
	public static ArrayList<WeaponData> parseFile(File file) {
		ArrayList<WeaponData> list = new ArrayList<>();
		try {
			String s = Files.toString(file, Charsets.UTF_8);
			JsonObject tree = new JsonParser().parse(s).getAsJsonObject();
			for (Entry<String, JsonElement> entry : tree.getAsJsonObject().entrySet()) {
				WeaponData data = new WeaponData(entry.getKey());
				for (Entry<String, JsonElement> property : entry.getValue().getAsJsonObject().entrySet())
					if (property.getKey().equals("Attributes") && property.getValue().isJsonObject())
						for (Entry<String, JsonElement> attribute : property.getValue().getAsJsonObject().entrySet()) {
							String attributeName = attribute.getKey();
							float attributeValue = attribute.getValue().getAsFloat();
							Iterator<String> iterator2 = MapList.nameToAttribute.keySet().iterator();
							// System.out.println("to je"+attributeName+"
							// "+attributeValue);
							boolean has = false;

							while (iterator2.hasNext())
								if (iterator2.next().equals(attributeName)) {
									data.attributes.put(MapList.nameToAttribute.get(attributeName), attributeValue);
									has = true;
								}
							if (has == false)
								data.attributes.put(TF2Attribute.attributes[Integer.parseInt(attributeName)],
										attributeValue);
						}
					else if (property.getKey().equals("Content") && property.getValue().isJsonObject())
						for (Entry<String, JsonElement> attribute : property.getValue().getAsJsonObject().entrySet()) {
							String itemName = attribute.getKey();
							int chance = attribute.getValue().getAsInt();
							data.crateContent.put(itemName, chance);
							data.maxCrateValue+=chance;
						}
					else
						data.addProperty(property.getKey(), property.getValue().getAsString());
				list.add(data);
			}
		} catch (Exception e) {
			System.err.println("Skipped reading weapon data from file: " + file.getName());
			e.printStackTrace();
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
				}
				attributelist=MapList.buildInAttributes.get(ItemFromData.getData(stack).getName());
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
}
