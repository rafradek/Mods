package rafradek.TF2weapons.message;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import rafradek.TF2weapons.ClientProxy;
import rafradek.TF2weapons.MapList;
import rafradek.TF2weapons.TF2Attribute;
import rafradek.TF2weapons.TF2weapons;
import rafradek.TF2weapons.WeaponData;
import rafradek.TF2weapons.WeaponData.PropertyType;
import rafradek.TF2weapons.message.TF2Message.WeaponDataMessage;

public class TF2WeaponDataHandler implements IMessageHandler<TF2Message.WeaponDataMessage, IMessage> {

	public static int size;

	@Override
	public IMessage onMessage(final WeaponDataMessage message, MessageContext ctx) {
		DataInputStream input;
		try {
			input = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(message.bytes, 0, message.bytes.length))));
			
			MapList.nameToData.clear();
			MapList.buildInAttributes.clear();
			
			while(input.available()>0){
				WeaponData weapon = new WeaponData(input.readUTF());
				//System.out.println("Weapon: "+weapon.getName());
				int propertyCount = input.readByte();
				for (int i = 0; i < propertyCount; i++) {
					int propId = input.readByte();
					PropertyType prop = WeaponData.propertyTypes[propId];
					//System.out.println(prop.name);
					prop.deserialize(input, weapon);
		
					// System.out.println("Property: "+prop.name+"
					// "+weapon.properties.get(prop).stringValue);
					/*
					 * stringLength=buf.readByte(); String
					 * propName=buf.toString(buf.readerIndex(),stringLength,
					 * StandardCharsets.UTF_8);
					 * buf.readerIndex(buf.readerIndex()+stringLength);
					 * Property.Type type=Property.Type.values()[buf.readByte()];
					 * int listLength=buf.readByte();
					 * 
					 * if(listLength>0){ String[] values=new String[listLength];
					 * for(int i=0;i<listLength;i++){ stringLength=buf.readByte();
					 * values[i]=buf.toString(buf.readerIndex(),stringLength,
					 * StandardCharsets.UTF_8);
					 * buf.readerIndex(buf.readerIndex()+stringLength); }
					 * weapon.put(propName, new Property(propName,values,type)); }
					 * else{ stringLength=buf.readByte(); weapon.put(propName, new
					 * Property(propName,buf.toString(buf.readerIndex(),
					 * stringLength, StandardCharsets.UTF_8),type));
					 * buf.readerIndex(buf.readerIndex()+stringLength); }
					 */
				}
				int attributeCount = input.readByte();
				for (int i = 0; i < attributeCount; i++) {
					if (weapon.getString(PropertyType.CLASS).equals("crate")) {
						String entry = input.readUTF();
						weapon.crateContent.put(entry, input.readInt());
		
					} else
						weapon.attributes.put(TF2Attribute.attributes[input.readByte()], input.readFloat());
				}
		
				TF2weapons.loadWeapon(weapon.getName(), weapon);
				ClientProxy.RegisterWeaponData(weapon);
				
			}
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		return null;
	}

}
