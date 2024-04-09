package Main.Components;

import Main.renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {

    private Texture tex;
    private List<Sprite> sprites;

    public SpriteSheet(Texture tex, int spriteWidth, int spriteHeight, int spriteNum, int spacing){
        this.sprites = new ArrayList<>();
        this.tex = tex;
        int currentX = 0;
        int currentY = tex.getHeight() - spriteHeight;

        for(int i = 0; i < spriteNum; i++){
            float topY = (currentY + spriteHeight) / (float)tex.getHeight();
            float rightX = (currentX + spriteWidth) / (float)tex.getWidth();
            float leftX = currentX / (float)tex.getWidth();
            float bottomY = currentY / (float)tex.getHeight();

            Vector2f[] texCoords = {
                    new Vector2f(rightX,topY),
                    new Vector2f(rightX,bottomY),
                    new Vector2f(leftX,bottomY),
                    new Vector2f(leftX,topY)
            };
            Sprite sprite = new Sprite();
            sprite.setTex(this.tex);
            sprite.setTexCoords(texCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            this.sprites.add(sprite);

            currentX += spriteWidth + spacing;
            if(currentX >= tex.getWidth()){
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    public Sprite getSprite(int index){
        return this.sprites.get(index);
    }
    public int size(){
        return sprites.size();
    }
}

