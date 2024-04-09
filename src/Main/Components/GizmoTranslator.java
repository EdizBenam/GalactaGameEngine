package Main.Components;

import Main.editor.PropViewer;
import Main.galacta.MouseListener;

public class GizmoTranslator extends Gizmo{
    public GizmoTranslator(Sprite arrowSprite, PropViewer propViewer) {
        super(arrowSprite, propViewer);
    }

    @Override
    public void editorUpdate(double dt) {

        if(activeGameObject != null){
            if(xAxisActive && !yAxisActive){
                activeGameObject.transform.position.x -= MouseListener.calcWorldX();
            } else if (yAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.calcWorldY();
            }
        }
        super.editorUpdate(dt);
    }
}
