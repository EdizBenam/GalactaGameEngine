package Main.renderer;

import Main.Components.Sprite;
import Main.Components.SpriteRenderer;
import Main.galacta.GameObject;
import Main.galacta.Window;
import Main.utilities.AssetRepository;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {

    //Vertex
    //Pos                  Color               TexCoords              TexID

    private final int positionSize = 2;
    private final int colorSize = 4;
    private final int texCoordsSize = 2;
    private final int texIDSize = 1;
    private final int EntityIDSize = 1;

    private final int positionOffset = 0;
    private final int colorOffset = positionOffset + positionSize * Float.BYTES;
    private final int texCoordsOffset = colorOffset + colorSize * Float.BYTES;
    private final int texIDOffset = texCoordsOffset + texCoordsSize * Float.BYTES;
    private final int entityIDOffset = texIDOffset + texIDSize * Float.BYTES;
    private final int vertexSize = 10;
    private final int vertexSizeBytes = vertexSize * Float.BYTES;
    private SpriteRenderer[] sprites;
    private Renderer renderer;
    private int spriteCount;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};
    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex, Renderer renderer){
        this.zIndex = zIndex;
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;
        this.renderer = renderer;

        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * vertexSize];

        this.spriteCount = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start(){
        // Generate and bind vertex array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
        // Create and Upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        // Buffer attrib pointers
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, positionOffset);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, colorOffset);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, texCoordsSize, GL_FLOAT, false, vertexSizeBytes, texCoordsOffset);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, texIDSize, GL_FLOAT, false, vertexSizeBytes, texIDOffset);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, EntityIDSize, GL_FLOAT, false, vertexSizeBytes, entityIDOffset);
        glEnableVertexAttribArray(4);
    }

    public void addSprite(SpriteRenderer sprite){
        int index = this.spriteCount;
        this.sprites[index] = sprite;
        this.spriteCount++;

        if(sprite.getTexture() != null){
            if(!textures.contains(sprite.getTexture())){
                textures.add(sprite.getTexture());
            }

        }

        loadVertexProperties(index);

        if(spriteCount >= maxBatchSize){
            this.hasRoom = false;
        }

    }

    public void render(){
        boolean rebufferData = false;
        for(int i = 0; i < spriteCount; i++){

            SpriteRenderer spr = sprites[i];
            if(spr.isDirty()){
                if(!hasTex(spr.getTexture())){
                    this.renderer.disposeGameObject(spr.gameObject);
                    this.renderer.add(spr.gameObject);
                } else {
                    loadVertexProperties(i);
                    spr.setClean();
                    rebufferData = true;
                }
            }

            if(spr.gameObject.transform.zIndex != this.zIndex){
                disposeIfTrue(spr.gameObject);
                renderer.add(spr.gameObject);
                i--;
            }

        }

        if(rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use shader
        Shader shader = Renderer.getBoundShader();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        for(int i = 0; i < textures.size(); i++){
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glDrawElements(GL_TRIANGLES, this.spriteCount * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for(int i = 0; i < textures.size(); i++){
            textures.get(i).unbind();
        }

        shader.detach();

    }

    public boolean disposeIfTrue(GameObject go){
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        for (int i = 0; i < spriteCount; i++){
            if(sprites[i] == spr){
                for (int j = i; j < spriteCount - 1; j++){
                    sprites[j] = sprites[j +1];
                    sprites[j].setDirty();
                }
                spriteCount--;
                return true;
            }
        }
        return false;
    }

    private void loadVertexProperties(int index){
        SpriteRenderer sprite = this.sprites[index];

        // 4 vertices per Sprite
        int offset = index * 4 * vertexSize;
        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        int texID = 0;
        // Looping Tex Array
        if(sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if(textures.get(i).equals(sprite.getTexture())){
                    texID = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if(isRotated){
            transformMatrix.translate(sprite.gameObject.transform.position.x, sprite.gameObject.transform.position.y, 0);
            transformMatrix.rotate((float) Math.toRadians(sprite.gameObject.transform.rotation), 0,0,1);
            transformMatrix.scale(sprite.gameObject.transform.scale.x, sprite.gameObject.transform.scale.y, 1);
        }


        // Add vertices

        //Iteration 0
        //xAdd = 1.0
        //yAdd = 1.0
        //
        //Iteration 1
        //xAdd = 1.0
        //yAdd = 0.0
        //
        //Iteration 2
        //xAdd = 0.0
        //yAdd = 0.0
        //
        //Iteration 3
        //xAdd = 0.0
        //yAdd = 1.0
        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for(int i = 0; i < 4; i++){
            if(i == 1){
                yAdd = -0.5f;
            } else if (i == 2){
                xAdd = -0.5f;
            } else if (i == 3){
                yAdd = 0.5f;
            }

            Vector4f currentPos = new Vector4f(sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x),
                    sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y), 0,1);
            if(isRotated){
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }

            // Load position
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texCoords
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;


            // Load texID
            vertices[offset + 8] = texID;

            //Load entityID
            vertices[offset + 9] = sprite.gameObject.getUid() + 1;

            offset += vertexSize;

        }
    }


    private int[] generateIndices(){
        //6 indices per quad (3 indices per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for(int i = 0; i < maxBatchSize; i++){
            loadElementIndices(elements, i);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index){
        int arrayIndexOffset = 6 * index;
        int offset = 4 * index;

        //3, 2, 0, 0, 2, 1 Triangle
        elements[arrayIndexOffset] = offset + 3;
        elements[arrayIndexOffset + 1] = offset + 2;
        elements[arrayIndexOffset + 2] = offset + 0;

        elements[arrayIndexOffset + 3] = offset + 0;
        elements[arrayIndexOffset + 4] = offset + 2;
        elements[arrayIndexOffset + 5] = offset + 1;



    }

    public boolean getHasRoom(){
        return this.hasRoom;
    }

    public boolean hasTexRoom(){
        return this.textures.size() < 8;
    }

    public boolean hasTex(Texture tex){
        return this.textures.contains(tex);
    }

    public int zIndex(){
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
