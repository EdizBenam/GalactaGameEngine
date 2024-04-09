package Main.Components;

import Main.editor.PropViewer;
import Main.galacta.GameObject;
import Main.galacta.KeyListener;
import Main.galacta.MouseListener;
import Main.galacta.Window;
import Main.renderer.DebugDraw;
import Main.renderer.PhaseManager;
import Main.renderer.TexturePicker;
import Main.scenes.Scene;
import Main.utilities.Settings;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class MouseControls extends Component implements IPickable {
    GameObject holdingObject = null;
    private float debounceTimer = 0.2f;
    private double debounce = debounceTimer;
    private boolean setSelected = false;
    private Vector2f setSelectBegin = new Vector2f();
    private Vector2f setSelectEnd = new Vector2f();

    public void pickupObject(GameObject go) {
        if (this.holdingObject != null) {
            this.holdingObject.dispose();
        }

        this.holdingObject = go;
        this.holdingObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0.8f, 0.8f, 0.8f, 0.7f));
        this.holdingObject.setPickable(false);
        Window.getScene().addGameObjectToScene(go);
    }

    public void place() {
        GameObject obj = this.holdingObject.copy();
        if (obj.getComponent(PhaseManager.class) != null) {
            obj.getComponent(PhaseManager.class).refreshTextures();
        }
        obj.getComponent(SpriteRenderer.class).setColor(new Vector4f(1, 1, 1, 1));
        obj.setPickable(true);
        Window.getScene().addGameObjectToScene(obj);
    }

    @Override
    public void editorUpdate(double dt) {
        debounce -= dt;
        TexturePicker texPicker = Window.getImguiLayer().getPropWindow().getTexturePicker();
        Scene currentScene = Window.getScene();

        if (holdingObject != null) {
            holdingObject.transform.position.x = MouseListener.calcWorldX();
            holdingObject.transform.position.y = MouseListener.calcWorldY();
            holdingObject.transform.position.x = ((int) Math.floor(holdingObject.transform.position.x / Settings.gridWidth) * Settings.gridWidth) + Settings.gridWidth / 2.0f;
            holdingObject.transform.position.y = ((int) Math.floor(holdingObject.transform.position.y / Settings.gridHeight) * Settings.gridHeight) + Settings.gridHeight / 2.0f;

            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                float halfWidth = Settings.gridWidth / 2.0f;
                float halfHeight = Settings.gridHeight / 2.0f;
                if(MouseListener.isDragging() && !itemInGrid(holdingObject.transform.position.x - halfWidth, holdingObject.transform.position.y - halfHeight)){
                    place();
                } else if (!MouseListener.isDragging() && debounce < 0){
                    place();
                    debounce = debounceTimer;
                }
            }
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
                holdingObject.dispose();
                holdingObject = null;
            }
        } else if (!MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectID = texPicker.pixelReader(x, y);
            GameObject pickedObj = currentScene.getGameObject(gameObjectID);
            if (pickedObj != null && pickedObj.isPickable()) {
                Window.getImguiLayer().getPropWindow().setActiveGameObject(pickedObj);
            } else if (pickedObj == null && !MouseListener.isDragging()) {
                Window.getImguiLayer().getPropWindow().disposeSelected();
            }
            this.debounce = 0.2f;
        } else if (MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            if (!setSelected) {
                Window.getImguiLayer().getPropWindow().disposeSelected();
                setSelectBegin = MouseListener.getScreen();
                setSelected = true;
            }
            setSelectEnd = MouseListener.getScreen();
            Vector2f setSelectedBeginWorld = MouseListener.screenToWorld(setSelectBegin);
            Vector2f setSelectedEndWorld = MouseListener.screenToWorld(setSelectEnd);
            Vector2f halfSize = (new Vector2f(setSelectedEndWorld).sub(setSelectedBeginWorld)).mul(0.5f);
            DebugDraw.addBox2D((new Vector2f(setSelectedBeginWorld).add(halfSize)), new Vector2f(halfSize).mul(2.0f), 0.0f);
        } else if (setSelected) {
            int screenStartX = (int) setSelectBegin.x;
            int screenStartY = (int) setSelectBegin.y;
            int screenEndX = (int) setSelectEnd.x;
            int screenEndY = (int) setSelectEnd.y;
            setSelected = false;
            setSelectBegin.zero();
            setSelectEnd.zero();
            if (screenEndX < screenStartX) {
                int tmp = screenStartX;
                screenStartX = screenEndX;
                screenEndX = tmp;
            }
            if (screenEndY < screenStartY) {
                int tmp = screenStartY;
                screenStartY = screenEndY;
                screenEndY = tmp;
            }
            float[] gameObjectIDs = texPicker.mulPixelReader(
                    new Vector2i(screenStartX, screenStartY),
                    new Vector2i(screenEndX, screenEndY)
            );
            Set<Integer> uniqueGameObjID = new HashSet<>();
            for (float objID: gameObjectIDs){
                uniqueGameObjID.add((int)objID);
            }
            for (Integer gameObjID: uniqueGameObjID){
                GameObject pickedObj = Window.getScene().getGameObject(gameObjID);
                if(pickedObj != null && pickedObj.isPickable()){
                    Window.getImguiLayer().getPropWindow().addActiveGameObject(pickedObj);
                }
            }
        }
    }

    private boolean itemInGrid(float x, float y) {
        PropViewer propViewer = Window.getImguiLayer().getPropWindow();
        Vector2f start = new Vector2f(x, y);
        Vector2f end = new Vector2f(start).add(new Vector2f(Settings.gridWidth, Settings.gridHeight));
        Vector2f startScreenf = MouseListener.worldToScreen(start);
        Vector2f endScreenf = MouseListener.worldToScreen(end);
        Vector2i startScreen = new Vector2i((int)(startScreenf.x) + 2, (int)(startScreenf.y) + 2);
        Vector2i endScreen = new Vector2i((int)(endScreenf.x) - 2, (int)(endScreenf.y) - 2);
        float[] gameObjectIds = propViewer.getTexturePicker().mulPixelReader(startScreen, endScreen);

        for (int i = 0; i < gameObjectIds.length; i++) {
            if (gameObjectIds[i] >= 0) {
                GameObject pickedObj = Window.getScene().getGameObject((int)gameObjectIds[i]);
                if (pickedObj.getComponent(NonPickable.class) == null) {
                    return true;
                }
            }
        }

        return false;
    }




    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public void setPickable(boolean pickable) {

    }
}

