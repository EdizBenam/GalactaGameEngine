package Main.Components;

import Main.galacta.Camera;
import Main.galacta.GameObject;
import Main.galacta.Window;
import org.joml.Vector4f;

public class PlayCamera extends Component{
    private transient GameObject player;
    private transient Camera sceneCamera;
    private transient float topX = Float.MIN_VALUE;
    private transient float basementY = 0.0f;
    private transient float cameraBuffer = 1.5f;
    private transient float playerBuffer = 0.25f;
    private Vector4f skyColor = new Vector4f(35 / 255.0f, 63 / 255.0f, 112 / 255.0f, 0.8f / 255.0f);
    private Vector4f basementColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    public PlayCamera(Camera sceneCamera){
        this.sceneCamera = sceneCamera;
    }

    @Override
    public void start(){
        this.player = Window.getScene().getGameObjectWith(PlayerController.class);
        this.sceneCamera.clearColor.set(skyColor);
        this.basementY = this.sceneCamera.position.y - this.sceneCamera.getProjectionSize().y - this.cameraBuffer;
    }

    @Override
    public void update(double dt){
        if (player != null && !player.getComponent(PlayerController.class).won()){

            sceneCamera.position.x = player.transform.position.x - 2.5f;
            sceneCamera.position.y = player.transform.position.y - 2.0f;
            sceneCamera.setZoom(1.5f);

            if(player.transform.position.y < -playerBuffer){
                this.sceneCamera.clearColor.set(basementColor);
            } else if (player.transform.position.y >= 0.0f) {
                this.sceneCamera.clearColor.set(skyColor);
            }
        }
    }

}
