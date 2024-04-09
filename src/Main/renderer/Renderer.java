package Main.renderer;

import Main.Components.SpriteRenderer;
import Main.galacta.GameObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Renderer {
        private final int maxBatchSize = 1000;
        private List<RenderBatch> Batches;
        private static Shader currentShader;

        public Renderer(){
            this.Batches = new ArrayList<>();
        }

        public void add(GameObject go){
            SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
            if(sprite != null){
                add(sprite);
            }

        }

        public void add(SpriteRenderer sprite){
            boolean added = false;
            for(RenderBatch batch : Batches){
                if(batch.getHasRoom() && batch.zIndex() == sprite.gameObject.transform.zIndex){
                    Texture tex = sprite.getTexture();
                    if(tex == null || (batch.hasTex(tex) || batch.hasTexRoom())) {
                        batch.addSprite(sprite);
                        added = true;
                        break;
                    }
                }
            }

            if(!added){
                RenderBatch batch = new RenderBatch(maxBatchSize, sprite.gameObject.transform.zIndex, this);
                batch.start();
                Batches.add(batch);
                batch.addSprite(sprite);
                Collections.sort(Batches);
            }
        }

        public void disposeGameObject(GameObject go){
            if(go.getComponent(SpriteRenderer.class) == null) return;
            for (RenderBatch batch: Batches){
                if(batch.disposeIfTrue(go)){
                    return;
                }
            }
        }

        public static void bindShader(Shader shader){
            currentShader = shader;
        }

        public static Shader getBoundShader(){
            return currentShader;
        }

        public void render(){
            currentShader.use();
            for(int i = 0; i < Batches.size(); i++){
                RenderBatch batch = Batches.get(i);
                batch.render();
            }
        }
}
