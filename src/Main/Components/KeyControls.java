package Main.Components;

import Main.editor.PropViewer;
import Main.galacta.GameObject;
import Main.galacta.KeyListener;
import Main.galacta.Window;
import Main.renderer.PhaseManager;
import Main.scenes.Scene;
import Main.utilities.Settings;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component{
        float debounce = 0.0f;
        float debounceTime = 0.1f;

        @Override
        public void editorUpdate(double dt) {
            debounce -= dt;

            PropViewer propertiesWindow = Window.getImguiLayer().getPropWindow();
            GameObject activeGameObject = propertiesWindow.getActiveGameObject();
            List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();
            float multiplier = KeyListener.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 0.1f : 1.0f;

            if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                    KeyListener.keyBeginPressed(GLFW_KEY_D) && activeGameObject != null) {
                GameObject newObj = activeGameObject.copy();
                Window.getScene().addGameObjectToScene(newObj);
                newObj.transform.position.add(0.25f, 0.0f);
                propertiesWindow.setActiveGameObject(newObj);
                if(newObj.getComponent(PhaseManager.class) != null){
                    newObj.getComponent(PhaseManager.class).refreshTextures();
                }
            } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                    KeyListener.keyBeginPressed(GLFW_KEY_D) && activeGameObjects.size() > 1) {
                List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
                propertiesWindow.disposeSelected();
                for (GameObject go : gameObjects) {
                    GameObject copy = go.copy();
                    Window.getScene().addGameObjectToScene(copy);
                    propertiesWindow.addActiveGameObject(copy);
                    if(copy.getComponent(PhaseManager.class) != null){
                        copy.getComponent(PhaseManager.class).refreshTextures();
                    }
                }
            } else if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_DOWN) && debounce < 0) {
                debounce = debounceTime;
                for (GameObject go : activeGameObjects) {
                    go.transform.zIndex--;
                }
            } else if (KeyListener.isKeyPressed(GLFW_KEY_PAGE_UP) && debounce < 0) {
                debounce = debounceTime;
                for (GameObject go : activeGameObjects) {
                    go.transform.zIndex++;
                }
            } else if (KeyListener.keyBeginPressed(GLFW_KEY_DELETE)) {
                for (GameObject go : activeGameObjects) {
                    go.dispose();
                }
                propertiesWindow.disposeSelected();
            } else if (KeyListener.isKeyPressed(GLFW_KEY_UP) && debounce < 0) {
                debounce = debounceTime;
                for (GameObject go : activeGameObjects) {
                    go.transform.position.y += Settings.gridHeight * multiplier;
                }
            } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN) && debounce < 0) {
                debounce = debounceTime;
                for (GameObject go : activeGameObjects) {
                    go.transform.position.y -= Settings.gridHeight * multiplier;
                }
            } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) && debounce < 0) {
                debounce = debounceTime;
                for (GameObject go : activeGameObjects) {
                    go.transform.position.x -= Settings.gridWidth * multiplier;
                }
            } else if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) && debounce < 0) {
                debounce = debounceTime;
                for (GameObject go : activeGameObjects) {
                    go.transform.position.x += Settings.gridWidth * multiplier;
                }
            } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) && KeyListener.isKeyPressed(GLFW_KEY_S)) {
                Window.getScene().save();
            }
        }
    }
