package Main.Components;

import Main.editor.PropViewer;
import Main.galacta.MouseListener;

public class GizmoScaler extends Gizmo {
    public GizmoScaler(Sprite scaleSprite, PropViewer propViewer) {
        super(scaleSprite, propViewer);
    }

    @Override
    public void editorUpdate(double dt) {

        if(activeGameObject != null){
            if(xAxisActive && !yAxisActive){
                activeGameObject.transform.scale.x -= MouseListener.calcWorldX();
            } else if (yAxisActive) {
                activeGameObject.transform.scale.y -= MouseListener.calcWorldY();
            }
        }
        super.editorUpdate(dt);
    }
}
