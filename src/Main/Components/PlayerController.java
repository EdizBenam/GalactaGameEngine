package Main.Components;

import Main.galacta.GameObject;
import Main.galacta.KeyListener;
import Main.galacta.Window;

import Main.physics2D.RaycastInfo;
import Main.renderer.DebugDraw;
import Main.utilities.AssetRepository;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import Main.physics2D.components.Rigidbody2D;
import Main.renderer.PhaseManager;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class PlayerController extends Component {

    private enum PlayerState {
        Enchantress,
        Wizard
    }


    public float walkSpeed = 1.9f;
    public float jumpBoost = 1.0f;
    public float jumpImpulse = 3.0f;
    public float slowDownForce = 0.05f;
    public Vector2f terminalVelocity = new Vector2f(2.1f, 3.1f);

    private PlayerState playerState = PlayerState.Enchantress;
    public transient boolean onFloor = false;
    private transient float floorDebounce = 0.0f;
    private transient float floorDebounceTime = 0.1f;
    private transient Rigidbody2D rb;
    private transient PhaseManager phaseManager;
    private transient float playerWidth = 0.75f;
    private transient int jumpTime = 0;
    private transient Vector2f acceleration = new Vector2f();
    private transient Vector2f velocity = new Vector2f();
    private transient boolean isDead = false;
    private transient int enemyBounce = 0;



    @Override
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        this.phaseManager = gameObject.getComponent(PhaseManager.class);
        this.rb.setGravityScale(0.0f);
    }

    @Override
    public void update(double dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT) || KeyListener.isKeyPressed(GLFW_KEY_D)) {
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = walkSpeed;

            if (this.velocity.x < 0) {
                this.phaseManager.signal("switchSideWalk");
                this.velocity.x += slowDownForce;
            } else {
                this.phaseManager.signal("startWalking");
            }
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT) || KeyListener.isKeyPressed(GLFW_KEY_A)) {
            this.gameObject.transform.scale.x = -playerWidth;
            this.acceleration.x = -walkSpeed;

            if (this.velocity.x > 0) {
                this.phaseManager.signal("switchSideWalk");
                this.velocity.x -= slowDownForce;
            } else {
                this.phaseManager.signal("startWalking");
            }
        } else {
            this.acceleration.x = 0;
            if (this.velocity.x > 0) {
                this.velocity.x = Math.max(0, this.velocity.x - slowDownForce);
            } else if (this.velocity.x < 0) {
                this.velocity.x = Math.min(0, this.velocity.x + slowDownForce);
            }

            if (this.velocity.x == 0) {
                this.phaseManager.signal("stopWalking");
            }
        }

        checkOnFloor();

        if (KeyListener.isKeyPressed(GLFW_KEY_SPACE) && (jumpTime > 0 || onFloor || floorDebounce > 0)) {
            if ((onFloor || floorDebounce > 0) && jumpTime == 0) {
                AssetRepository.getAudio("assets/Audio/Jump.ogg").play();
                AssetRepository.getAudio("assets/Audio/Jump.ogg").setGain(0.2f);
                jumpTime = 20;
                this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {

                this.velocity.y = jumpImpulse + ((jumpTime / 0.5f) * jumpBoost);
                jumpTime--;
            } else {
                this.velocity.y = 0;
            }
            floorDebounce = 0;
        } else if (!onFloor) {
            if (this.jumpTime > 0) {

                this.velocity.y *= 0.95f;
                this.jumpTime = 0;
            }
            floorDebounce -= dt;
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.6f;
        } else {
            this.velocity.y = 0;
            this.acceleration.y = 0;
            floorDebounce = floorDebounceTime;
        }

        // Apply gravity and update velocity
        this.acceleration.y = Window.getPhysics().getGravity().y * 0.6f;

        this.velocity.x += this.acceleration.x * dt;
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
        this.rb.setVelocity(this.velocity);
        this.rb.setAngularVelocity(0);

        if (!onFloor) {
            phaseManager.signal("jump");
        } else {
            phaseManager.signal("stopJump");
        }
    }

    public void checkOnFloor() {
        Vector2f raycastStart = new Vector2f(this.gameObject.transform.position);

        float playerCoreWidth = this.playerWidth * 0.6f;

        raycastStart.sub(playerCoreWidth / 11.0f, 0.24f);
        float yValue = playerState == PlayerState.Enchantress ? -0.14f : -0.24f;
        Vector2f raycastEnd = new Vector2f(raycastStart).add(0.0f, yValue);

        RaycastInfo info = Window.getPhysics().raycast(gameObject, raycastStart, raycastEnd);

        Vector2f raycast2Start = new Vector2f(this.gameObject.transform.position).add(playerCoreWidth, 0.0f);
        raycast2Start.sub(playerCoreWidth / 1.11f, 0.24f);
        Vector2f raycast2End = new Vector2f(raycast2Start).add(0.0f, yValue);
        RaycastInfo info2 = Window.getPhysics().raycast(gameObject, raycast2Start, raycast2End);

        onFloor = (info.hit && info.hitObject != null && info.hitObject.getComponent(Floor.class) != null) ||
                (info2.hit && info2.hitObject != null && info2.hitObject.getComponent(Floor.class) != null);

        DebugDraw.addLine2D(raycastStart, raycastEnd, new Vector3f(0, 1, 0));
        DebugDraw.addLine2D(raycast2Start, raycast2End, new Vector3f(1, 0, 0));
    }

    @Override
    public void beginCollision(GameObject collidingObject, Contact contact, Vector2f contactNormal) {
        if (isDead) return;

        if (collidingObject.getComponent(Floor.class) != null) {
            if (Math.abs(contactNormal.x) > 0.8f) {
                this.velocity.x = 0;
            } else if (contactNormal.y > 0.8f) {
                this.velocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;
            }
        }
    }

    public boolean won(){
        return false;
    }
}
