package Main.renderer;

import org.joml.Vector2i;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

public class TexturePicker {
    private int texPickerID;
    private int fbo;
    private int depthTex;

    public TexturePicker(int width, int height){
        if(!init(width, height)){
            assert false: "Error initializing TexturePicker!";
        }
    }

    public boolean init(int width, int height){
        //FrameBuffer generator
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        //Texture to render Data to Framebuffer
        texPickerID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texPickerID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texPickerID, 0);

        //Depth buffer texID
        glEnable(GL_DEPTH_TEST);
        depthTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0,
                GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D, depthTex, 0);

        // Reading disabler
        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);
        glDisable(GL_DEPTH_TEST);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false: "Error: FrameBuffer is not Complete!";
            return false;
        }

        // Unbind FrameBuffer and Texture
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return true;
    }

    public void writingEnabler(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    public void writingDisabler(){
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public int pixelReader(int x, int y){
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float[] pixels = new float[3];
        glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

        return (int)(pixels[0]) - 1;
    }

    public float[] mulPixelReader(Vector2i begin, Vector2i end){
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        Vector2i size = new Vector2i(end).sub(begin).absolute();
        int pixelCount = size.x * size.y;
        float[] pixels = new float[3 * pixelCount];
        glReadPixels(begin.x, begin.y, size.x, size.y, GL_RGB, GL_FLOAT, pixels);

        for(int i = 0; i < pixels.length; i++){
            pixels[i] -= 1;
        }
        return pixels;
    }
}
