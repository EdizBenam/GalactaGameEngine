package Main.physics2D.components;

import Main.Components.Component;
import Main.galacta.Window;
import Main.physics2D.Physics2D;
import Main.physics2D.components.Box2DCollider;
import Main.physics2D.components.Circle2DCollider;
import org.joml.Vector2f;

public class PillBoxCollider extends Component {
    private transient Circle2DCollider topCircle = new Circle2DCollider();
    private transient Circle2DCollider bottomCircle = new Circle2DCollider();
    private transient Box2DCollider box2DCollider = new Box2DCollider();
    private transient boolean resetFixtureNextFrame = false; // No need to reset

    public float width = 0.1f;
    public float height = 0.2f;
    public Vector2f offset = new Vector2f();

    @Override
    public void start() {
        this.topCircle.gameObject = this.gameObject;
        this.bottomCircle.gameObject = this.gameObject;
        this.box2DCollider.gameObject = this.gameObject;
        reCalcColliders(); // No need to reCalculate
    }

    @Override
    public void editorUpdate(double dt) {
        topCircle.editorUpdate(dt);
        bottomCircle.editorUpdate(dt);
        box2DCollider.editorUpdate(dt);

        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    @Override
    public void update(double dt) {
        if (resetFixtureNextFrame) {
            resetFixture();
        }
    }

    public void setWidth(float width) {
        this.width = width;
        reCalcColliders();
        resetFixture();
    }

    public void setHeight(float height) {
        this.height = height;
        reCalcColliders();
        resetFixture();
    }

    // reCalcColliders not needed for now
    public void reCalcColliders() {
        float circleRad = width / 4.0f;
        float boxHeight = height - 2 * circleRad;
        topCircle.setRadius(circleRad);
        bottomCircle.setRadius(circleRad);
        topCircle.setOffset(new Vector2f(offset).add(0, boxHeight / 4.0f));
        bottomCircle.setOffset(new Vector2f(offset).sub(0, boxHeight / 4.0f));
        box2DCollider.setHalfSize(new Vector2f(width / 2.0f, boxHeight / 2.0f));
        box2DCollider.setOffset(offset);
    }

    public Circle2DCollider getTopCircle() {
        return topCircle;
    }

    public Circle2DCollider getBottomCircle() {
        return bottomCircle;
    }

    public Box2DCollider getBox2DCollider() {
        return box2DCollider;
    }

    public void resetFixture() {
        if (Window.getPhysics().locked()) {
            resetFixtureNextFrame = true;
            return;
        }
        resetFixtureNextFrame = false;

        if (gameObject != null) {
            Rigidbody2D rigidbody2D = gameObject.getComponent(Rigidbody2D.class);
            if (rigidbody2D != null) {
                Window.getPhysics().resetPillBoxCollider(rigidbody2D, this);
            }
        }
    }
}
