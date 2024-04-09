package Main.Components;

import Main.galacta.Camera;
import Main.galacta.KeyListener;
import Main.galacta.MouseListener;
import java.lang.Math;
import org.joml.Vector2f;

import static org.lwjgl.bgfx.BGFX.Functions.reset;
import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component {

    private Camera levelEditorC;
    private Vector2f clickSource;
    private boolean reset = false;
    private boolean isDragging = false;
    private float lerper = 0.0f;
    private float resetLerperSpeed = 0.1f; // Adjust this value for slower reset


    private float dragSensitivity = 80.0f; // Adjust this value for faster dragging
    private float scrollSensitivity = 0.1f; // Adjust this value for faster zooming

    public EditorCamera(Camera levelEditorC){
        this.levelEditorC = levelEditorC;
        this.clickSource = new Vector2f();
    }

    @Override
    public void editorUpdate(double dt){
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && !isDragging){
            this.clickSource = MouseListener.calcWorld();
            isDragging = true;
        }

        if(isDragging && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            Vector2f mousePos = MouseListener.calcWorld();
            Vector2f mouseDistance = new Vector2f(mousePos);
            levelEditorC.position.sub(mouseDistance.sub(clickSource).mul((float) dt).mul(dragSensitivity));
            this.clickSource.set(mousePos);
        }

        if (isDragging && !MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)){
            isDragging = false;
        }

        if(MouseListener.getScreenY() != 0.0f){
            float addVal = (float) Math.pow(Math.abs(MouseListener.getScrollY() * scrollSensitivity), 1.0 / levelEditorC.getZoom());
            addVal *= -Math.signum(MouseListener.getScrollY());
            levelEditorC.addZoom(addVal);
        }
        if (KeyListener.isKeyPressed(GLFW_KEY_HOME) && !reset) {
            this.reset = true;
        }
        if (reset) {
            levelEditorC.position.lerp(new Vector2f(), lerper);
            levelEditorC.setZoom(this.levelEditorC.getZoom() + ((1.0f - levelEditorC.getZoom()) * lerper));
            this.lerper += resetLerperSpeed * (float) dt;
            if(Math.abs(levelEditorC.position.x) <= 5.0f && Math.abs(levelEditorC.position.y) <= 5.0f){
                this.lerper = 0;
                levelEditorC.position.set(0f, 0f);
                this.levelEditorC.setZoom(1.0f);
                reset = false;
            }
        }
    }
}