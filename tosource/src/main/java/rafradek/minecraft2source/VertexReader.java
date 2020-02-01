package rafradek.minecraft2source;

import java.nio.ByteBuffer;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraftforge.fml.common.FMLLog;

public class VertexReader extends WorldVertexBufferUploader {
	
	public void draw(BufferBuilder bufferBuilder) {
        
        VertexFormat format = bufferBuilder.getVertexFormat();
        int stride = format.getNextOffset();
        ByteBuffer buffer = bufferBuilder.getByteBuffer();
        List<VertexFormatElement> list = format.getElements();

        for (int j = 0; j < list.size(); ++j)
        {
            VertexFormatElement attr = list.get(j);
            VertexFormatElement.EnumUsage attrType = attr.getUsage();
            int l = attr.getIndex();

            // moved to VertexFormatElement.preDraw
            int count = attr.getElementCount();
            int constant = attr.getType().getGlConstant();
            buffer.position(format.getOffset(j));
            switch(attrType)
            {
                case POSITION:
                	System.out.println("pos:" +buffer.getFloat()+" "+buffer.getFloat()+" "+buffer.getFloat());
                	
                    //GlStateManager.glVertexPointer(count, constant, stride, buffer);
                    //GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
                    break;
                case NORMAL:
                    //if(count != 3)
                    //{
                    //    throw new IllegalArgumentException("Normal attribute should have the size 3: " + attr);
                    //}
                   // GlStateManager.glNormalPointer(constant, stride, buffer);
                    //GlStateManager.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                    break;
                case COLOR:
                    //GlStateManager.glColorPointer(count, constant, stride, buffer);
                    //GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
                    break;
                case UV:
                	System.out.println("uv:" +buffer.getFloat()+" "+buffer.getFloat());
                    //OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + attr.getIndex());
                    //GlStateManager.glTexCoordPointer(count, constant, stride, buffer);
                    //GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
                   //OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                    break;
                case PADDING:
                    break;
                case GENERIC:
                    //GL20.glEnableVertexAttribArray(attr.getIndex());
                    //GL20.glVertexAttribPointer(attr.getIndex(), count, constant, false, stride, buffer);
                default:
                    FMLLog.log.fatal("Unimplemented vanilla attribute upload: {}", attrType.getDisplayName());
            }
        }
    }
}
