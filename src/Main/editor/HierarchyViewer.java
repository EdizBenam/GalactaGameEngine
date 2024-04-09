package Main.editor;

import Main.galacta.GameObject;
import Main.galacta.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import java.util.List;

public class HierarchyViewer {

    private static String payLoadddType = "HierarchyViewer";

    public void imgui() {
        ImGui.begin("Scene Hierarchy");

        List<GameObject> gameObjects = Window.getScene().getGameObjects();
        int index = 0;
        for (GameObject object : gameObjects) {
            if (!object.DoSerialize()) {
                continue;
            }
            boolean openTreeNode = doTreeNode(object, index);
            if (openTreeNode) {
                ImGui.treePop();
            }
            index++;
        }
        ImGui.end();
    }


    private boolean doTreeNode(GameObject object, int index){
        ImGui.pushID(index);
        boolean openTreeNode = ImGui.treeNodeEx(object.name, ImGuiTreeNodeFlags.DefaultOpen | ImGuiTreeNodeFlags.FramePadding|
                ImGuiTreeNodeFlags.OpenOnArrow|ImGuiTreeNodeFlags.SpanAvailWidth, object.name);
        ImGui.popID();

        if(ImGui.beginDragDropSource()){
            ImGui.setDragDropPayload(payLoadddType, object);
            ImGui.text(object.name);

            ImGui.endDragDropSource();
        }

        if(ImGui.beginDragDropTarget()){
            Object plobj = ImGui.acceptDragDropPayload(payLoadddType);
            if(plobj != null){
                if(plobj.getClass().isAssignableFrom(GameObject.class)){
                    GameObject playerGo = (GameObject)plobj;
                    System.out.println("Payload accepted '" + playerGo.name + "'");
                }
            }

            ImGui.endDragDropTarget();
        }
        return openTreeNode;
    }
}
