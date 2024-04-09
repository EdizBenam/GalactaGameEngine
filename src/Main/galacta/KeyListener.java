package Main.galacta;

import java.util.Arrays;
import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instance;
    private Boolean keyPressed[] = new Boolean[350];
    private Boolean keyBeginPress[] = new Boolean[350];

    private KeyListener() {
        Arrays.fill(keyPressed, false);
        Arrays.fill(keyBeginPress, false);
    }

    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        try {
            if (key >= 0 && key < get().keyPressed.length) {
                if (action == GLFW_PRESS) {
                    get().keyPressed[key] = true;
                    get().keyBeginPress[key] = true;
                } else if (action == GLFW_RELEASE) {
                    get().keyPressed[key] = false;
                    get().keyBeginPress[key] = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void endFrame() {
        try {
            Arrays.fill(get().keyBeginPress, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        try {
            return get().keyPressed[keyCode];
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception appropriately, e.g., log it or show an error message.
            return false; // Return a default value to prevent crashes
        }
    }

    public static boolean keyBeginPressed(int keyCode) {
        try {
            return get().keyBeginPress[keyCode];
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception appropriately, e.g., log it or show an error message.
            return false; // Return a default value to prevent crashes
        }
    }
}
