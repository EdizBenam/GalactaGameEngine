package Main.Components;

import Main.galacta.KeyListener;
import Main.galacta.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoStation extends Component{
    private SpriteSheet gizmos;
    private int gizmoUsed = 0;

    public GizmoStation(SpriteSheet gizmoSprs){
        gizmos = gizmoSprs;
    }

    @Override
    public void start(){
        gameObject.addComponent(new GizmoTranslator(gizmos.getSprite(1), Window.getImguiLayer().getPropWindow()));
        gameObject.addComponent(new GizmoScaler(gizmos.getSprite(2), Window.getImguiLayer().getPropWindow()));
    }

    @Override
    public void editorUpdate(double dt){
        if(gizmoUsed == 0){
            gameObject.getComponent(GizmoTranslator.class).setUsing();
            gameObject.getComponent(GizmoScaler.class).setNotUsing();
        } else if (gizmoUsed == 1) {
            gameObject.getComponent(GizmoTranslator.class).setNotUsing();
            gameObject.getComponent(GizmoScaler.class).setUsing();
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_E)){
            gizmoUsed = 0;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_R)) {
            gizmoUsed = 1;
        }
    }
}
