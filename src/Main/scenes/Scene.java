package Main.scenes;

import Main.Components.Component;
import Main.Components.ComponentSerialize;
import Main.galacta.Camera;
import Main.galacta.GameObject;
import Main.galacta.GameObjectSerialize;
import Main.galacta.Transform;
import Main.physics2D.Physics2D;
import Main.renderer.Renderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import org.joml.Vector2f;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

    private Renderer renderer;

    public Camera camera;
    private boolean isRunning;
    private List<GameObject> gameObjects;
    private SceneInit sceneInit;
    private Physics2D physics2D;

    public Scene(SceneInit sceneInit) {
        this.sceneInit = sceneInit;
        this.physics2D = new Physics2D();
        this.renderer = new Renderer();
        this.gameObjects = new ArrayList<>();
        this.isRunning = false;
    }

    public Physics2D getPhysics(){
        return this.physics2D;
    }

    public void init() {
        this.camera = new Camera(new Vector2f());
        this.sceneInit.loadResources(this);
        this.sceneInit.init(this);
    }

    public void start(){
        for(int i = 0; i < gameObjects.size(); i++){
            GameObject go = gameObjects.get(i);
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
        isRunning = true;

    }

    public void addGameObjectToScene(GameObject go){
        if(!isRunning){
            gameObjects.add(go);
        } else{
            gameObjects.add(go);
            go.start();
            this.renderer.add(go);
            this.physics2D.add(go);
        }
    }

    public void dispose(){
        for(GameObject go: gameObjects){
            go.dispose();
        }
    }

    public <T extends Component> GameObject getGameObjectWith(Class<T> room) {
        return gameObjects.stream()
                .filter(go -> go.getComponent(room) != null)
                .findFirst()
                .orElse(null);
    }

    public List<GameObject> getGameObjects(){
        return this.gameObjects;
    }

    public GameObject getGameObject(int GOID){
        Optional<GameObject> result = this.gameObjects.stream().filter(gameObject -> gameObject.getUid() == GOID).findFirst();
        return result.orElse(null);
    }

    public void editorUpdate(double dt){
        this.camera.adjustProjection();
        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.editorUpdate(dt);

            if(go.isDisposed()){
                gameObjects.remove(i);
                this.renderer.disposeGameObject(go);
                this.physics2D.disposeGameObject(go);
                i--;
            }
        }
    }

    public void update(double dt){
        this.camera.adjustProjection();
        this.physics2D.update(dt);


        for (int i = 0; i < gameObjects.size(); i++) {
            GameObject go = gameObjects.get(i);
            go.update(dt);

            if(go.isDisposed()){
                gameObjects.remove(i);
                this.renderer.disposeGameObject(go);
                this.physics2D.disposeGameObject(go);
                i--;
            }
        }
    }
    public void render(){
        this.renderer.render();
    }

    public Camera camera(){
        return this.camera;
    }

    public void imgui(){
        this.sceneInit.imgui();
    }

    public GameObject createGameObj(String name){
        GameObject go = new GameObject(name);
        go.addComponent(new Transform());
        go.transform = go.getComponent(Transform.class);
        return go;
    }

    public void save(){
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class,
                new ComponentSerialize()).registerTypeAdapter(GameObject.class, new GameObjectSerialize()).enableComplexMapKeySerialization().create();

        try{
            FileWriter writer = new FileWriter("level.txt");
            List<GameObject> objectsToSerialize = new ArrayList<>();
            for (GameObject object: this.gameObjects){
                if (object.DoSerialize()){
                    objectsToSerialize.add(object);
                }
            }
            writer.write(gson.toJson(objectsToSerialize));
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }


    public void load(){
        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Component.class,
                new ComponentSerialize()).registerTypeAdapter(GameObject.class, new GameObjectSerialize()).enableComplexMapKeySerialization().create();
        String inFile = "";

        try{
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e){
            e.printStackTrace();
        }

        if(!inFile.equals("")){
            int maxGoID = -1;
            int maxCompID = -1;
            GameObject[] objects = gson.fromJson(inFile, GameObject[].class);
            for(int i = 0; i < objects.length; i++){
                addGameObjectToScene(objects[i]);

                for (Component c : objects[i].getAllComponents()){
                    if(c.getUid() > maxCompID){
                        maxCompID = c.getUid();
                    }
                }

                if(objects[i].getUid() > maxGoID){
                    maxGoID = objects[i].getUid();
                }
            }
            maxGoID++;
            maxCompID++;
            GameObject.init(maxGoID);
            Component.init(maxCompID);
        }
    }
}
