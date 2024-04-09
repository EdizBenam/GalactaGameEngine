package Main.editor;

import Main.Components.SpriteRenderer;
import Main.galacta.GameObject;
import Main.galacta.MouseListener;
import Main.physics2D.components.Box2DCollider;
import Main.physics2D.components.Circle2DCollider;
import Main.physics2D.components.Rigidbody2D;
import Main.renderer.TexturePicker;
import Main.scenes.Scene;
import imgui.ImGui;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropViewer {
    private List<GameObject> activeGameObjects = null;
    private List<Vector4f> activeObjsColor;
    private GameObject activeGameObject;
    private TexturePicker texPicker;

    public PropViewer(TexturePicker texPicker){
        this.activeGameObjects = new ArrayList<>();
        this.texPicker = texPicker;
        this.activeObjsColor = new ArrayList<>();
    }


    public void imgui() {
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");

            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add Rigidbody")) {
                    if (activeGameObject.getComponent(Rigidbody2D.class) == null) {
                        activeGameObject.addComponent(new Rigidbody2D());
                    }
                }

                if (ImGui.menuItem("Add Box Collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(Circle2DCollider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                if (ImGui.menuItem("Add Circle Collider")) {
                    if (activeGameObject.getComponent(Circle2DCollider.class) == null &&
                            activeGameObject.getComponent(Box2DCollider.class) == null) {
                        activeGameObject.addComponent(new Circle2DCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) : null;
    }

    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    public void disposeSelected(){
        if(activeObjsColor.size() > 0){
            int i = 0;
            for (GameObject go: activeGameObjects){
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if(spr != null){
                    spr.setColor(activeObjsColor.get(i));
                }
                i++;
            }
        }
        this.activeGameObjects.clear();
        this.activeObjsColor.clear();
    }

    public void setActiveGameObject(GameObject go) {
        if(go != null){
            disposeSelected();
            this.activeGameObjects.add(go);
        }
    }

    public void addActiveGameObject(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if(spr != null){
            this.activeObjsColor.add(new Vector4f(spr.getColor()));
            spr.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.activeObjsColor.add(new Vector4f());
        }
        this.activeGameObjects.add(go);
    }

    public TexturePicker getTexturePicker() {
        return texPicker;
    }
}
