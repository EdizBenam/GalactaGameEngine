package Main.Components;

import Main.renderer.Texture;
import org.joml.Vector2f;


public class Sprite {

    private Texture tex = null;

    private float width, height;
    private Vector2f[] texCoords = {
            new Vector2f(1,1),
            new Vector2f(1,0),
            new Vector2f(0,0),
            new Vector2f(0,1)

    };

    public Texture getTex() {
        return this.tex;
    }

    public Vector2f[] texCoords(){
        return this.texCoords;
    }

    public void setTex(Texture tex){
        this.tex = tex;
    }

    public void setTexCoords(Vector2f[] texCoords){
        this.texCoords = texCoords;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getTexId(){
        return tex == null ? -1 : tex.getID();
    }
}
