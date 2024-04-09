package Main.galacta;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, worldX, worldY;
    private boolean mouseButtonPressed[] = new boolean[9];
    private boolean isDragging;
    private int buttonDown = 0;
    private static Vector2f gameViewerPos = new Vector2f();
    private static Vector2f gameViewerSize = new Vector2f();

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
    }

    public static void clear(){
        get().scrollX = 0.0;
        get().scrollY = 0.0;
        get().xPos = 0.0;
        get().yPos = 0.0;
        get().buttonDown = 0;
        get().isDragging = false;
        Arrays.fill(get().mouseButtonPressed, false);

    }

    public static MouseListener get() {
        if (instance == null) {
            instance = new MouseListener();
        }

        return instance;
    }

    public static void mousePosCallback(long window, double xpos, double ypos) {
        if(!Window.getImguiLayer().getGameViewer().getMouseWanter()){
            clear();
        }

        if(get().buttonDown > 0){
            get().isDragging = true;
        }
        get().xPos = xpos;
        get().yPos = ypos;
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            get().buttonDown++;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            get().buttonDown--;

            if (button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
    }

    public static float getX() {
        return (float)get().xPos;
    }

    public static float getY() {
        return (float)get().yPos;
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    public static Vector2f screenToWorld(Vector2f screenCoords) {
        Vector2f normalizedScreenCoords = new Vector2f(screenCoords.x / Window.getWidth(), screenCoords.y / Window.getHeight());
        normalizedScreenCoords.mul(2.0f).sub(new Vector2f(1.0f, 1.0f));
        Camera camera = Window.getScene().camera();
        Vector4f tmp = new Vector4f(normalizedScreenCoords.x, normalizedScreenCoords.y, 0, 1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }

    public static Vector2f worldToScreen(Vector2f worldCoords) {
        Camera camera = Window.getScene().camera();
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 1.0f, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        windowSpace.add(new Vector2f(1.0f, 1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));

        return windowSpace;
    }

    public static float getScreenX() {
        return getScreen().x;
    }

    public static float getScreenY() {
        return getScreen().y;
    }

    public static Vector2f getScreen(){
        float currentX = getX() - gameViewerPos.x;
        currentX = (currentX / gameViewerSize.x) * 2560.0f;
        float currentY = getY() - gameViewerPos.y;
        currentY = 1440.0f - ((currentY / gameViewerSize.y) * 1440.0f);
        return new Vector2f(currentX, currentY);
    }

    public static float calcWorldX(){
        return calcWorld().x;
    }

    public static float calcWorldY(){
        return calcWorld().y;
    }

    public static Vector2f calcWorld(){
        float currentX = getX() - gameViewerPos.x;
        currentX = (currentX /  gameViewerSize.x) * 2.0f - 1.0f;
        float currentY = getY() - gameViewerPos.y;
        currentY = -((currentY / gameViewerSize.y) * 2.0f - 1.0f);
        Vector4f tmp = new Vector4f(currentX, currentY, 0, 1);

        Camera camera = Window.getScene().camera;
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProj = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProj));

        return new Vector2f(tmp.x, tmp.y);
    }

    public static void setGameViewerPos(Vector2f gameViewerPos) {
        MouseListener.gameViewerPos.set(gameViewerPos);
    }

    public static void setGameViewerSize(Vector2f gameViewerSize) {
        MouseListener.gameViewerSize.set(gameViewerSize);
    }
}
