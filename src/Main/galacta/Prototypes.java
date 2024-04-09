package Main.galacta;

import Main.Components.PlayerController;
import Main.Components.Sprite;
import Main.Components.SpriteRenderer;
import Main.Components.SpriteSheet;
import Main.physics2D.components.PillBoxCollider;
import Main.physics2D.components.Rigidbody2D;
import Main.physics2D.enums.BodyType;
import Main.renderer.Animator;
import Main.renderer.PhaseManager;
import Main.utilities.AssetRepository;

public class Prototypes {

    public static GameObject generateSprObject(Sprite sprite, float sizeX, float sizeY) {
        GameObject block = Window.getScene().createGameObj("Sprite_Object_Gen");
        SpriteRenderer renderer = new SpriteRenderer();
        renderer.setSprite(sprite);
        block.transform.scale.x = sizeX;
        block.transform.scale.y = sizeY;
        block.addComponent(renderer);

        return block;
    }

    public static GameObject generateEnchantress() {
        SpriteSheet playerWalk = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Walk.png");
        SpriteSheet playerRun = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Run.png");
        SpriteSheet playerJump = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Jump.png");
        SpriteSheet playerIdle = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Idle.png");
        SpriteSheet playerHurt = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Hurt.png");
        SpriteSheet playerDead = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Dead.png");

        SpriteSheet playerAttack_1 = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Attack_1.png");
        SpriteSheet playerAttack_2 = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Attack_2.png");
        SpriteSheet playerAttack_3 = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Attack_3.png");
        SpriteSheet playerAttack_4 = AssetRepository.getSprSheet("assets/Animation Sets/Enchantress/Attack_4.png");

        GameObject enchantress = generateSprObject(playerIdle.getSprite(0), 0.75f, 0.75f);

        Animator idle = new Animator();
        idle.label = "Idle";
        float idelDef = 0.35f;
        idle.addFrame(playerIdle.getSprite(0), idelDef);
        idle.addFrame(playerIdle.getSprite(1), idelDef);
        idle.addFrame(playerIdle.getSprite(2), idelDef);
        idle.setLoops(true);

        Animator walk = new Animator();
        walk.label = "Walk";
        float defaultFTime = 0.2f;
        walk.addFrame(playerWalk.getSprite(0), defaultFTime);
        walk.addFrame(playerWalk.getSprite(1), defaultFTime);
        walk.addFrame(playerWalk.getSprite(2), defaultFTime);
        walk.addFrame(playerWalk.getSprite(3), defaultFTime);
        walk.addFrame(playerWalk.getSprite(4), defaultFTime);
        walk.addFrame(playerWalk.getSprite(5), defaultFTime);
        walk.addFrame(playerWalk.getSprite(6), defaultFTime);
        walk.addFrame(playerWalk.getSprite(7), defaultFTime);
        walk.setLoops(true);

        Animator run = new Animator();
        run.label = "Run";
        run.addFrame(playerRun.getSprite(0), defaultFTime);
        run.addFrame(playerRun.getSprite(1), defaultFTime);
        run.addFrame(playerRun.getSprite(2), defaultFTime);
        run.addFrame(playerRun.getSprite(3), defaultFTime);
        run.addFrame(playerRun.getSprite(4), defaultFTime);
        run.addFrame(playerRun.getSprite(5), defaultFTime);
        run.addFrame(playerRun.getSprite(6), defaultFTime);
        run.addFrame(playerRun.getSprite(7), defaultFTime);
        run.setLoops(true);

        Animator switchSideWalk = new Animator();
        switchSideWalk.label = "switchSideWalk";
        switchSideWalk.addFrame(playerWalk.getSprite(3), defaultFTime);
        switchSideWalk.setLoops(false);

        Animator jump = new Animator();
        jump.label = "Jump";
        jump.addFrame(playerJump.getSprite(0), defaultFTime);
        jump.addFrame(playerJump.getSprite(1), defaultFTime);
        jump.addFrame(playerJump.getSprite(2), defaultFTime);
        jump.addFrame(playerJump.getSprite(3), defaultFTime);
        jump.addFrame(playerJump.getSprite(4), defaultFTime);
        jump.addFrame(playerJump.getSprite(5), defaultFTime);
        jump.addFrame(playerJump.getSprite(6), defaultFTime);
        jump.addFrame(playerJump.getSprite(7), defaultFTime);
        jump.setLoops(false);

        Animator hurt = new Animator();
        hurt.label = "Hurt";
        hurt.addFrame(playerHurt.getSprite(0), defaultFTime);
        hurt.addFrame(playerHurt.getSprite(1), defaultFTime);
        hurt.setLoops(false);

        Animator dead = new Animator();
        dead.label = "Dead";
        dead.addFrame(playerDead.getSprite(0), defaultFTime);
        dead.addFrame(playerDead.getSprite(1), defaultFTime);
        dead.addFrame(playerDead.getSprite(2), defaultFTime);
        dead.addFrame(playerDead.getSprite(3), defaultFTime);
        dead.addFrame(playerDead.getSprite(4), defaultFTime);
        dead.setLoops(false);

        Animator attack1 = new Animator();
        attack1.label = "Attack1";
        attack1.addFrame(playerAttack_1.getSprite(0), defaultFTime);
        attack1.addFrame(playerAttack_1.getSprite(1), defaultFTime);
        attack1.addFrame(playerAttack_1.getSprite(2), defaultFTime);
        attack1.addFrame(playerAttack_1.getSprite(3), defaultFTime);
        attack1.addFrame(playerAttack_1.getSprite(4), defaultFTime);
        attack1.addFrame(playerAttack_1.getSprite(5), defaultFTime);
        attack1.setLoops(false);

        Animator attack2 = new Animator();
        attack2.label = "playerAttack_2";
        attack2.addFrame(playerAttack_2.getSprite(0), defaultFTime);
        attack2.addFrame(playerAttack_2.getSprite(1), defaultFTime);
        attack2.addFrame(playerAttack_2.getSprite(2), defaultFTime);
        attack2.setLoops(false);

        Animator attack3 = new Animator();
        attack3.label = "playerAttack_3";
        attack3.addFrame(playerAttack_3.getSprite(0), defaultFTime);
        attack3.addFrame(playerAttack_3.getSprite(1), defaultFTime);
        attack3.addFrame(playerAttack_3.getSprite(2), defaultFTime);
        attack3.setLoops(false);

        Animator attack4 = new Animator();
        attack4.label = "playerAttack_4";
        attack4.addFrame(playerAttack_4.getSprite(0), defaultFTime);
        attack4.addFrame(playerAttack_4.getSprite(1), defaultFTime);
        attack4.addFrame(playerAttack_4.getSprite(2), defaultFTime);
        attack4.addFrame(playerAttack_4.getSprite(3), defaultFTime);
        attack4.addFrame(playerAttack_4.getSprite(4), defaultFTime);
        attack4.addFrame(playerAttack_4.getSprite(5), defaultFTime);
        attack4.addFrame(playerAttack_4.getSprite(6), defaultFTime);
        attack4.addFrame(playerAttack_4.getSprite(7), defaultFTime);
        attack4.addFrame(playerAttack_4.getSprite(8), defaultFTime);
        attack4.addFrame(playerAttack_4.getSprite(9), defaultFTime);
        attack4.setLoops(false);

        PhaseManager phaseManager = new PhaseManager();
        phaseManager.addPhase(walk);
        phaseManager.addPhase(run);
        phaseManager.addPhase(jump);
        phaseManager.addPhase(idle);
        phaseManager.addPhase(hurt);
        phaseManager.addPhase(dead);
        phaseManager.addPhase(attack1);
        phaseManager.addPhase(attack2);
        phaseManager.addPhase(attack3);
        phaseManager.addPhase(attack4);
        phaseManager.addPhase(switchSideWalk);

        phaseManager.setDefaultPhase(walk.label);
        phaseManager.addPhase(run.label, idle.label, "stopRunning");
        phaseManager.addPhase(walk.label, idle.label, "stopWalking");
        phaseManager.addPhase(run.label, jump.label, "jump");
        phaseManager.addPhase(walk.label, jump.label, "jump");
        phaseManager.addPhase(switchSideWalk.label, jump.label, "jump");
        phaseManager.addPhase(idle.label, jump.label, "jump");
        phaseManager.addPhase(idle.label, run.label, "startRunning");
        phaseManager.addPhase(idle.label, walk.label, "startWalking");
        phaseManager.addPhase(jump.label, idle.label, "stopJump");

        phaseManager.addPhase(switchSideWalk.label, walk.label, "startWalking");
        phaseManager.addPhase(switchSideWalk.label, run.label, "startRunning");
        phaseManager.addPhase(switchSideWalk.label, idle.label, "stopWalking");
        phaseManager.addPhase(switchSideWalk.label, jump.label, "jump");
        phaseManager.addPhase(switchSideWalk.label, dead.label, "dead");
        phaseManager.addPhase(switchSideWalk.label, hurt.label, "hurt");
        phaseManager.addPhase(run.label, switchSideWalk.label, "switchSideWalk");
        phaseManager.addPhase(walk.label, switchSideWalk.label, "switchSideWalk");
        phaseManager.addPhase(idle.label, switchSideWalk.label, "switchSideWalk");

        phaseManager.addPhase(run.label, hurt.label, "Hurt");
        phaseManager.addPhase(walk.label, hurt.label, "Hurt");
        phaseManager.addPhase(jump.label, hurt.label, "Hurt");
        phaseManager.addPhase(idle.label, hurt.label, "Hurt");
        phaseManager.addPhase(attack1.label, hurt.label, "Hurt");
        phaseManager.addPhase(attack2.label, hurt.label, "Hurt");
        phaseManager.addPhase(attack3.label, hurt.label, "Hurt");
        phaseManager.addPhase(attack4.label, hurt.label, "Hurt");

        phaseManager.addPhase(run.label, hurt.label, "Dead");
        phaseManager.addPhase(walk.label, hurt.label, "Dead");
        phaseManager.addPhase(jump.label, hurt.label, "Dead");
        phaseManager.addPhase(idle.label, hurt.label, "Dead");
        phaseManager.addPhase(hurt.label, hurt.label, "Dead");
        phaseManager.addPhase(attack1.label, hurt.label, "Dead");
        phaseManager.addPhase(attack2.label, hurt.label, "Dead");
        phaseManager.addPhase(attack3.label, hurt.label, "Dead");
        phaseManager.addPhase(attack4.label, hurt.label, "Dead");

        phaseManager.addPhase(run.label, attack1.label, "Attack1");
        phaseManager.addPhase(walk.label, attack1.label, "Attack1");
        phaseManager.addPhase(idle.label, attack1.label, "Attack1");

        phaseManager.addPhase(run.label, attack2.label, "Attack2");
        phaseManager.addPhase(walk.label, attack2.label, "Attack2");
        phaseManager.addPhase(idle.label, attack2.label, "Attack2");

        phaseManager.addPhase(run.label, attack3.label, "Attack3");
        phaseManager.addPhase(walk.label, attack3.label, "Attack3");
        phaseManager.addPhase(idle.label, attack3.label, "Attack3");

        phaseManager.addPhase(run.label, attack4.label, "Attack4");
        phaseManager.addPhase(walk.label, attack4.label, "Attack4");
        phaseManager.addPhase(idle.label, attack4.label, "Attack4");

        enchantress.addComponent(phaseManager);

        PillBoxCollider pillBoxCollider = new PillBoxCollider();
        pillBoxCollider.width = 0.55f;
        pillBoxCollider.height = 0.5f;
        Rigidbody2D rigidbody2D = new Rigidbody2D();
        rigidbody2D.setBodyType(BodyType.Dynamic);
        rigidbody2D.setContinuousCollision(false);
        rigidbody2D.setFixedRotation(true);
        rigidbody2D.setMass(25.0f);

        enchantress.addComponent(rigidbody2D);
        enchantress.addComponent(pillBoxCollider);
        enchantress.addComponent(new PlayerController());

        return enchantress;
    }

    public static GameObject generateChest() {
        SpriteSheet Chest = AssetRepository.getSprSheet("assets/Animation Sets/3 Animated objects/Chest.png");
        GameObject chestObj = generateSprObject(Chest.getSprite(0), 0.5f, 0.5f);

        Animator open = new Animator();
        open.label = "ChestAnim";
        float defaultFTime = 0.3f;
        open.addFrame(Chest.getSprite(0), defaultFTime);
        open.addFrame(Chest.getSprite(1), defaultFTime);
        open.addFrame(Chest.getSprite(2), defaultFTime);
        open.addFrame(Chest.getSprite(3), defaultFTime);
        open.addFrame(Chest.getSprite(4), defaultFTime);
        open.addFrame(Chest.getSprite(5), defaultFTime);
        open.loops = true;

        PhaseManager phaseManager = new PhaseManager();
        phaseManager.addPhase(open);
        phaseManager.setDefaultPhase(open.label);
        chestObj.addComponent(phaseManager);

        return chestObj;
    }
}
