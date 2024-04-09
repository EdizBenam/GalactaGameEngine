package Main.Components;

import Main.editor.GImGui;
import Main.galacta.Transform;
import Main.renderer.Texture;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1,1,1,1);
    private Sprite sprite = new Sprite();
    private transient Transform lastTransform;
    private transient boolean isDirty = true;

    public Texture getTexture(){
        return sprite.getTex();
    }

    public Vector2f[] getTexCoords(){
        return sprite.texCoords();

    }


    @Override
    public void start(){
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(double dt) {
        if(!this.lastTransform.equals(this.gameObject.transform)){
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void editorUpdate(double dt) {
        if(!this.lastTransform.equals(this.gameObject.transform)){
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    public Vector4f getColor(){

        return this.color;
    }

    @Override
    public void imgui(){
        if (GImGui.colorPicker("Color picker:", this.color)){
            this.isDirty = true;
        }
    }

    public void setDirty(){
        this.isDirty = true;
    }

    public void setSprite (Sprite sprite){
        this.sprite = sprite;
        this.isDirty = true;
    }

    public void setColor(Vector4f color){
        if(!this.color.equals(color)) {
            this.isDirty = true;
            this.color.set(color);
        }
    }

    public boolean isDirty(){
        return this.isDirty;
    }

    public void setClean(){
        this.isDirty = false;
    }

    public void setTexture(Texture tex){
        this.sprite.setTex(tex);
    }
}
