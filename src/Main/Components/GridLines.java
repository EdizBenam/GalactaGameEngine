package Main.Components;

import Main.galacta.Camera;
import Main.galacta.Window;
import Main.renderer.DebugDraw;
import Main.utilities.Settings;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class GridLines extends Component{

    @Override
    public void editorUpdate(double dt){
        Camera camera = Window.getScene().camera;
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();

        float firstX = ((int)(cameraPos.x / Settings.gridWidth)) * Settings.gridHeight;
        float firstY = ((int)(cameraPos.y / Settings.gridHeight)) * Settings.gridWidth;

        int verticalLineCount = (int)(projectionSize.x * camera.getZoom() / Settings.gridWidth) + 2;
        int horizontalLineCount = (int)(projectionSize.y * camera.getZoom() / Settings.gridHeight) + 2;

        float width = (int)(projectionSize.x * camera.getZoom()) + ( 5 * Settings.gridWidth);
        float height = (int)(projectionSize.y * camera.getZoom()) + ( 5 * Settings.gridHeight);

        int maxLines = Math.max(verticalLineCount, horizontalLineCount);
        Vector3f color = new Vector3f(0.2f,0.2f,0.2f);
        for(int i = 0; i < maxLines; i++){
            float x = firstX + (Settings.gridWidth * i);
            float y = firstY + (Settings.gridHeight * i);

            if(i < verticalLineCount){
                DebugDraw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if(i < horizontalLineCount){
                DebugDraw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }


        }


    }



}
