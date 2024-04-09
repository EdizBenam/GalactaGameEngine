package Main.galacta;

import Main.Components.Component;
import Main.Components.ComponentSerialize;
import Main.Components.IPickable;
import Main.Components.SpriteRenderer;
import Main.utilities.AssetRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class GameObject implements IPickable {

    private static int idCounter = 0;
    private int uid = -1;

    public String name;
    private List<Component> components;
    public transient Transform transform;
    private boolean doSerialize = true;
    private boolean isDisposed = false;
    private boolean isPickable = true;



    public GameObject(String name){
        this.name = name;
        this.components = new ArrayList<>();
        this.uid = idCounter++;
    }

    public <T extends Component> T getComponent(Class<T> componentClass){
        for(Component c : components){
            if(componentClass.isAssignableFrom(c.getClass())){
                try{
                    return componentClass.cast(c);
                } catch (ClassCastException e){
                    e.printStackTrace();
                    assert false: "Error: Casting Component.";
                }
            }
        }

        return null;

    }

    public <T extends Component> void removeComponent(Class<T> componentClass){
        for(int i = 0; i < components.size(); i++){
            Component c = components.get(i);
            if(componentClass.isAssignableFrom(c.getClass())){
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c){
        c.generateID();
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(double dt) {
        for (int i=0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    public void editorUpdate(double dt){
        for(int i = 0; i < components.size(); i++){
            components.get(i).editorUpdate(dt);
        }
    }

    public void start() {
        for (int i=0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

    public void imgui(){
        for(Component c: components){
            if(ImGui.collapsingHeader(c.getClass().getSimpleName()))
                c.imgui();
        }
    }

    public void dispose(){
        this.isDisposed = true;
        for(int i = 0; i < components.size(); i++){
            components.get(i).dispose();
        }
    }

    public GameObject copy(){
        Gson gson = new GsonBuilder().registerTypeAdapter(Component.class,
                new ComponentSerialize()).registerTypeAdapter(GameObject.class, new GameObjectSerialize()).enableComplexMapKeySerialization().create();
        String objectAsJson = gson.toJson(this);
        GameObject obj = gson.fromJson(objectAsJson, GameObject.class);
        obj.generateUID();
        for (Component c: obj.getAllComponents()){
            c.generateID();
        }
        SpriteRenderer spr = obj.getComponent(SpriteRenderer.class);
        if(spr != null && spr.getTexture() != null){
            spr.setTexture(AssetRepository.getTexture(spr.getTexture().getFilepath()));
        }
        return obj;
    }

    public boolean isDisposed() {
        return this.isDisposed;
    }

    public static void init(int maxId){
        idCounter = maxId;
    }

    public int getUid() {
        return this.uid;
    }

    public List<Component> getAllComponents() {
        return this.components;
    }

    public void setNoSerialize(){
        this.doSerialize = false;
    }

    public boolean DoSerialize(){
        return this.doSerialize;
    }

    public void generateUID(){
        this.uid = idCounter++;
    }

    @Override
    public boolean isPickable() {
        return isPickable;
    }

    @Override
    public void setPickable(boolean pickable) {
        this.isPickable = pickable;
    }
}
