package Main.renderer;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private int fboID = 0;
    private Texture texture = null;


    public FrameBuffer(int width, int height){
        //FrameBuffer generator
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        //Texture to render Data to Framebuffer
        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getID(), 0);

        //RenderBuffer / Depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
            assert false: "Error: FrameBuffer is not Complete!";
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getFboID() {
        return this.fboID;
    }

    public int getTextureID() {
        return this.texture.getID();
    }
}
