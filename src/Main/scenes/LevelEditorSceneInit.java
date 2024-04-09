package Main.scenes;

import Main.Components.*;
import Main.galacta.*;
import Main.physics2D.components.Box2DCollider;
import Main.physics2D.components.Rigidbody2D;
import Main.physics2D.enums.BodyType;
import Main.renderer.PhaseManager;
import Main.utilities.AssetRepository;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

import java.io.File;
import java.util.Collection;

public class LevelEditorSceneInit extends SceneInit {

    private SpriteSheet sprites, gizmoSprites;
    private GameObject LEItems;
    public LevelEditorSceneInit() {

    }

    @Override
    public void init(Scene scene) {
        gizmoSprites = AssetRepository.getSprSheet("assets/images/ArrowGizmo.png");
        sprites = AssetRepository.getSprSheet("assets/images/TilesetDark.png");

        LEItems = scene.createGameObj("levelEditor");
        LEItems.setNoSerialize();
        LEItems.addComponent(new MouseControls());
        LEItems.addComponent(new KeyControls());
        LEItems.addComponent(new GridLines());
        LEItems.addComponent(new EditorCamera(scene.camera()));
        LEItems.addComponent(new GizmoStation(gizmoSprites));
        scene.addGameObjectToScene(LEItems);
    }
    @Override
    public void loadResources(Scene scene) {
        AssetRepository.getShader("assets/shaders/default.glsl");

        // Load the sprite sheet

        AssetRepository.addSprSheet("assets/images/TilesetDark.png",
                new SpriteSheet(AssetRepository.getTexture("assets/images/TilesetDark.png"),
                        32, 32, 81, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Walk.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Walk.png"),
                        128, 128, 8, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Run.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Run.png"),
                        128, 128, 8, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Jump.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Jump.png"),
                        128, 128, 8, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Idle.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Idle.png"),
                        128, 128, 5, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Hurt.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Hurt.png"),
                        128, 128, 2, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Dead.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Dead.png"),
                        128, 128, 5, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Attack_4.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Attack_4.png"),
                        128, 128, 10, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Attack_3.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Attack_3.png"),
                        128, 128, 3, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Attack_2.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Attack_2.png"),
                        128, 128, 3, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/Enchantress/Attack_1.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/Enchantress/Attack_1.png"),
                        128, 128, 6, 0));

        AssetRepository.addSprSheet("assets/Animation Sets/3 Animated objects/Chest.png",
                new SpriteSheet(AssetRepository.getTexture("assets/Animation Sets/3 Animated objects/Chest.png"),
                        48, 48, 6, 0));

        AssetRepository.addSprSheet("assets/images/ArrowGizmo.png",
                new SpriteSheet(AssetRepository.getTexture("assets/images/ArrowGizmo.png"),
                        24, 48, 3, 0));

        AssetRepository.addAudio("assets/Audio/Town-Of-Hope-And-Despair.ogg", false);
        AssetRepository.addAudio("assets/Audio/Town-Of-Hope-And-Despair-End.ogg", false);
        AssetRepository.addAudio("assets/Audio/Town-Of-Hope-And-Despair-Loop.ogg", true);
        AssetRepository.addAudio("assets/Audio/Jump.ogg", false);


        for(GameObject g:scene.getGameObjects()){
            if(g.getComponent(SpriteRenderer.class) != null){
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if(spr.getTexture() != null){
                    spr.setTexture(AssetRepository.getTexture(spr.getTexture().getFilepath()));
                }
            }
                if(g.getComponent(PhaseManager.class) != null){
                    PhaseManager phaseManager = g.getComponent(PhaseManager.class);
                    phaseManager.refreshTextures();
                }
        }
    }

    @Override
    public void imgui(){

        ImGui.begin("Options");
        LEItems.imgui();
        ImGui.end();

        ImGui.begin("Items");

        if(ImGui.beginTabBar("ViewerBar")) {
            if(ImGui.beginTabItem("Items")) {
                ImVec2 windowPos = new ImVec2();
                ImVec2 windowSize = new ImVec2();
                ImVec2 itemSpacing = new ImVec2();

                ImGui.getWindowPos(windowPos);
                ImGui.getWindowSize(windowSize);
                ImGui.getStyle().getItemSpacing(itemSpacing);

                float windowX2 = windowPos.x + windowSize.x;
                for (int i = 0; i < sprites.size(); i++) {
                    if(i >= 34) continue;
                    if(i >= 4 && i <= 7) continue;
                    Sprite sprite = sprites.getSprite(i);
                    float spriteWidth = sprite.getWidth() * 1.5f;
                    float spriteHeight = sprite.getHeight() * 1.5f;

                    int id = sprite.getTexId();
                    Vector2f[] texCoords = sprite.texCoords();

                    ImGui.pushID(i);
                    if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                        GameObject object = Prototypes.generateSprObject(sprite, 0.25f, 0.25f);
                        Rigidbody2D rigidbody2D = new Rigidbody2D();
                        rigidbody2D.setBodyType(BodyType.Static);
                        object.addComponent(rigidbody2D);
                        Box2DCollider box2DCollider = new Box2DCollider();
                        box2DCollider.setHalfSize(new Vector2f(0.25f, 0.25f));
                        object.addComponent(box2DCollider);
                        object.addComponent(new Floor());
                        // Attach to mouse cursor
                        LEItems.getComponent(MouseControls.class).pickupObject(object);
                    }
                    ImGui.popID();

                    ImVec2 lastButtonPos = new ImVec2();
                    ImGui.getItemRectMax(lastButtonPos);
                    float lastButtonX2 = lastButtonPos.x;
                    float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;
                    if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }

            if(ImGui.beginTabItem("Prototypes")){


                // 1st Animator Start
                SpriteSheet playerSpr = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Walk.png");
                Sprite sprite = playerSpr.getSprite(0);
                float spriteWidth = sprite.getWidth() * 1.5f;
                float spriteHeight = sprite.getHeight() * 1.5f;
                int id = sprite.getTexId();
                Vector2f[] texCoords = sprite.texCoords();
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prototypes.generateEnchantress();
                    LEItems.getComponent(MouseControls.class).pickupObject(object);

                }

                ImGui.sameLine();

                // 2nd Animator Start
                SpriteSheet Chest = AssetRepository.getSprSheet("assets/Animation Sets/3 Animated objects/Chest.png");

                sprite = Chest.getSprite(0);
                id = sprite.getTexId();
                texCoords = sprite.texCoords();
                if (ImGui.imageButton(id, spriteWidth, spriteHeight, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                    GameObject object = Prototypes.generateChest();
                    LEItems.getComponent(MouseControls.class).pickupObject(object);

                }

                // Animator end

                ImGui.endTabItem();
            }

            if(ImGui.beginTabItem("Audios")){
                Collection<Audio> audios = AssetRepository.getAllAudios();
                for(Audio audio: audios){
                    audio.setGain(0.2f);
                    File tmp = new File(audio.getFilepath());
                    if(ImGui.button(tmp.getName())){
                        if(!audio.isPlaying()){
                            audio.play();
                        } else {
                            audio.stop();
                        }
                    }
                    if(ImGui.getContentRegionAvailX() > 100){
                        ImGui.sameLine();
                    }
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }

}