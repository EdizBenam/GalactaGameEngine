package Main.editor;

import Main.galacta.MouseListener;
import Main.galacta.Window;
import Main.watchdogs.BroadcastSystem;
import Main.watchdogs.broadcasts.Broadcast;
import Main.watchdogs.broadcasts.BroadcastType;
import imgui.ImGui;
import imgui.ImGuiWindowClass;
import imgui.ImVec2;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;

public class GameViewer {

    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying = false;

    public void imgui() {
        ImGui.begin("Game Viewer", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                | ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();
        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            BroadcastSystem.notif(null, new Broadcast(BroadcastType.GameEngineActivate));
        }
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            BroadcastSystem.notif(null, new Broadcast(BroadcastType.GameEngineDeactivate));
        }
        ImGui.endMenuBar();

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;

        int textureId = Window.getFramebuffer().getTextureID();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        MouseListener.setGameViewerPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setGameViewerSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    public boolean getMouseWanter() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }
}
