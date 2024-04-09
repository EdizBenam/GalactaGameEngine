package Main.galacta;
import Main.physics2D.Physics2D;
import Main.renderer.*;
import Main.scenes.LevelEditorSceneInit;
import Main.scenes.LevelSceneInit;
import Main.scenes.Scene;
import Main.scenes.SceneInit;
import Main.utilities.AssetRepository;
import Main.watchdogs.BroadcastSystem;
import Main.watchdogs.WatchDog;
import Main.watchdogs.broadcasts.Broadcast;
import org.joml.Vector4f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.opengl.GL11.*;

public class Window implements WatchDog {

    int width, height;

    String title;
    private long glfwWindow;
    private long audioContext;
    private long audioDevice;
    private boolean isEditorActive = false;

    private static Window window = null;

    private static Scene currentScene;
    private IMGUILayer imguiLayer;

    private FrameBuffer frameBuffer;
    private TexturePicker texPicker;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Galacta Engine";
        BroadcastSystem.addWatchDog(this);
    }

    public static void changeScene(SceneInit sceneInitializer) {
        if (currentScene != null) {
            currentScene.dispose();
        }

        getImguiLayer().getPropWindow().setActiveGameObject(null);

        currentScene = new Scene(sceneInitializer);
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }

    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }

        return Window.window;
    }

    public static Physics2D getPhysics(){return currentScene.getPhysics();}

    public static Scene getScene() {
        return currentScene;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        //Dispose audio context
        alcDestroyContext(audioContext);
        alcCloseDevice(audioDevice);

        // Free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and the free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        //Disabled because of a bug
        //Bug weirdly fixed?
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make window visible
        glfwShowWindow(glfwWindow);

        // Audio device initializer
        String defDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defDeviceName);

        int[] attribs = {0};
        audioContext = alcCreateContext(audioDevice, attribs);
        alcMakeContextCurrent(audioContext);
        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if(!alCapabilities.OpenAL10){
            assert false: "Audio lib not supported!";
        }

        GL.createCapabilities();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        //Screen change
        this.frameBuffer = new FrameBuffer(2560, 1440);
        this.texPicker = new TexturePicker(2560, 1440);
        glViewport(0, 0, 2560, 1440);

        this.imguiLayer = new IMGUILayer(glfwWindow, texPicker);
        this.imguiLayer.initImGui();

        Window.changeScene(new LevelEditorSceneInit());
    }

    public void loop() {
        float beginTime = (float) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        Shader defaultShader = AssetRepository.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetRepository.getShader("assets/shaders/pickingShader.glsl");

        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            //1 Render to texPicker
            glDisable(GL_BLEND);
            texPicker.writingEnabler();


            //Screen change
            glViewport(0, 0, 2560, 1440);
            glClearColor(0, 0, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            texPicker.writingDisabler();
            glEnable(GL_BLEND);

            //2 Render game
            DebugDraw.beginFrame();

            this.frameBuffer.bind();
            Vector4f clearColor = currentScene.camera().clearColor;
            glClearColor(clearColor.x, clearColor.y, clearColor.z, clearColor.w);
            glClear(GL_COLOR_BUFFER_BIT);

            if (dt >= 0) {
                Renderer.bindShader(defaultShader);
                if (isEditorActive) {
                    currentScene.update(dt);
                } else {
                    currentScene.editorUpdate(dt);
                }
                currentScene.render();
                DebugDraw.draw();
            }
            this.frameBuffer.unbind();

            this.imguiLayer.update((float) dt, currentScene);

            KeyListener.endFrame();
            MouseListener.endFrame();
            glfwSwapBuffers(glfwWindow);

            endTime = (float)glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }



    //Screen change
    public static int getWidth() {
        return 2560;
        //get().width;
    }

    public static int getHeight() {
        return 1440;
        //get().height;
    }

    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    public static FrameBuffer getFramebuffer() {
        return get().frameBuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    public static IMGUILayer getImguiLayer() {
        return get().imguiLayer;
    }

    @Override
    public void onNotif(GameObject object, Broadcast event) {

        Audio loopAudio = AssetRepository.getAudio("assets/Audio/Town-Of-Hope-And-Despair-Loop.ogg");

        switch (event.type) {
            case GameEngineActivate:
                this.isEditorActive = true;
                loopAudio.play();
                loopAudio.setGain(0.8f);
                currentScene.save();
                Window.changeScene(new LevelSceneInit());
                break;
            case GameEngineDeactivate:
                this.isEditorActive = false;
                loopAudio.stop();
                Window.changeScene(new LevelEditorSceneInit());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInit());
                break;
            case SaveLevel:
                currentScene.save();
                break;
        }
    }
}
