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

public class LevelSceneInit extends SceneInit {
    public LevelSceneInit() {

    }

    @Override
    public void init(Scene scene) {
        SpriteSheet sprites = AssetRepository.getSprSheet("assets/images/TilesetDark.png");

        GameObject LCamera = scene.createGameObj("SceneCamera");
        LCamera.addComponent(new PlayCamera(scene.camera()));
        LCamera.start();
        scene.addGameObjectToScene(LCamera);
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
    public void imgui() {

    }

}