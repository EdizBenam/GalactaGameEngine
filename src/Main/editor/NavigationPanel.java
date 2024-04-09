package Main.editor;

import Main.watchdogs.BroadcastSystem;
import Main.watchdogs.broadcasts.Broadcast;
import Main.watchdogs.broadcasts.BroadcastType;
import imgui.ImGui;

public class NavigationPanel {

    public void imgui() {
        ImGui.beginMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", "Ctrl+S")) {
                BroadcastSystem.notif(null, new Broadcast(BroadcastType.SaveLevel));
            }

            if (ImGui.menuItem("Load", "Ctrl+O")) {
                BroadcastSystem.notif(null, new Broadcast(BroadcastType.LoadLevel));
            }

            ImGui.endMenu();
        }

        ImGui.endMenuBar();
    }
}
