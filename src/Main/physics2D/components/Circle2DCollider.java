package Main.physics2D.components;

import Main.Components.Component;
import Main.renderer.DebugDraw;
import org.joml.Vector2f;

public class Circle2DCollider extends Component {
    private float radius = 1f;
    protected Vector2f offset = new Vector2f();

    public float getRadius() {
        return radius;
    }

    public Vector2f getOffset(){
        return this.offset;
    }

    public void setOffset(Vector2f offset) {
        this.offset.set(offset);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void editorUpdate(double dt){
        Vector2f center = new Vector2f(this.gameObject.transform.position).add(this.offset);
        DebugDraw.addCircle2D(center, this.radius);
    }
}
