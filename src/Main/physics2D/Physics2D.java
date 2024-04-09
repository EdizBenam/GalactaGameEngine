package Main.physics2D;

import Main.galacta.GameObject;
import Main.galacta.Transform;
import Main.physics2D.components.Box2DCollider;
import Main.physics2D.components.Circle2DCollider;
import Main.physics2D.components.PillBoxCollider;
import Main.physics2D.components.Rigidbody2D;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.joml.Vector2f;

public class Physics2D {
    private Vec2 gravity = new Vec2(0, -10.0f);
    private World world = new World(gravity);

    private float physicsTime = 0.0f;
    private float physicsTimeStep = 1.0f / 60.0f;
    private int velocityIterations = 8;
    private int positionIterations = 3;

    public Physics2D(){
        world.setContactListener(new GalactaContactListener());
    }

    public void add(GameObject go) {
        Rigidbody2D rigidbody2D = go.getComponent(Rigidbody2D.class);
        if (rigidbody2D != null && rigidbody2D.getRawBody() == null) {
            Transform transform = go.transform;

            BodyDef bodyDef = new BodyDef();
            bodyDef.angle = (float)Math.toRadians(transform.rotation);
            bodyDef.position.set(transform.position.x, transform.position.y);
            bodyDef.angularDamping = rigidbody2D.getAngularDamping();
            bodyDef.linearDamping = rigidbody2D.getLinearDamping();
            bodyDef.fixedRotation = rigidbody2D.isFixedRotation();
            bodyDef.userData = rigidbody2D.gameObject;
            bodyDef.bullet = rigidbody2D.isContinuousCollision();
            bodyDef.gravityScale = rigidbody2D.gravityScale;
            bodyDef.angularVelocity = rigidbody2D.angularVelocity;

            switch (rigidbody2D.getBodyType()) {
                case Kinematic: bodyDef.type = BodyType.KINEMATIC; break;
                case Static: bodyDef.type = BodyType.STATIC; break;
                case Dynamic: bodyDef.type = BodyType.DYNAMIC; break;
            }

            Body body = this.world.createBody(bodyDef);
            body.m_mass = rigidbody2D.getMass();
            rigidbody2D.setRawBody(body);
            Circle2DCollider circleCollider;
            Box2DCollider boxCollider;
            PillBoxCollider pillBoxCollider;


            if ((circleCollider = go.getComponent(Circle2DCollider.class)) != null) {
                addCircle2DCollider(rigidbody2D, circleCollider);
            }

            if ((boxCollider = go.getComponent(Box2DCollider.class)) != null) {
                addBox2Dcollider(rigidbody2D, boxCollider);
            }

            if((pillBoxCollider = go.getComponent(PillBoxCollider.class)) != null){
                addPillBoxCollider(rigidbody2D, pillBoxCollider);
            }
        }
    }

    public void disposeGameObject(GameObject go) {
        Rigidbody2D rb = go.getComponent(Rigidbody2D.class);
        if (rb != null) {
            if (rb.getRawBody() != null) {
                world.destroyBody(rb.getRawBody());
                rb.setRawBody(null);
            }
        }
    }

    public void update(double dt) {
        physicsTime += dt;
        if (physicsTime >= 0.0f) {
            physicsTime -= physicsTimeStep;
            world.step(physicsTimeStep, velocityIterations, positionIterations);
        }
    }

    public void setIsSensor(Rigidbody2D rigidbody2D){
        Body body = rigidbody2D.getRawBody();
        if(body == null) return;
        Fixture fixture = body.getFixtureList();
        while (fixture != null){
            fixture.m_isSensor = true;
            fixture = fixture.m_next;
        }
    }

    public void setNotSensor(Rigidbody2D rigidbody2D){
        Body body = rigidbody2D.getRawBody();
        if(body == null) return;
        Fixture fixture = body.getFixtureList();
        while (fixture != null){
            fixture.m_isSensor = false;
            fixture = fixture.m_next;
        }
    }

    public Vector2f getGravity(){
        return new Vector2f(world.getGravity().x, world.getGravity().y);
    }

    public void resetCircle2DCollider(Rigidbody2D rigidbody2D, Circle2DCollider circle2DCollider){
        Body body = rigidbody2D.getRawBody();
        if(body == null) return;
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++){
            body.destroyFixture(body.getFixtureList());
        }

        addCircle2DCollider(rigidbody2D, circle2DCollider);
        body.resetMassData();
    }

    public void resetBox2Dcollider(Rigidbody2D rigidbody2D, Box2DCollider box2DCollider){
        Body body = rigidbody2D.getRawBody();
        if(body == null) return;
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++){
            body.destroyFixture(body.getFixtureList());
        }

        addBox2Dcollider(rigidbody2D, box2DCollider);
        body.resetMassData();
    }

    public void addBox2Dcollider(Rigidbody2D rigidbody2D, Box2DCollider box2DCollider){
        Body body = rigidbody2D.getRawBody();
        assert body != null : "Raw body shouldn't be null!";

        PolygonShape shape = new PolygonShape();
        Vector2f halfSize = new Vector2f(box2DCollider.getHalfSize()).mul(0.5f);
        Vector2f offset = box2DCollider.getOffset();
        Vector2f origin = new Vector2f(box2DCollider.getOrigin());
        shape.setAsBox(halfSize.x, halfSize.y, new Vec2(offset.x, offset.y), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rigidbody2D.getFriction();
        fixtureDef.userData = box2DCollider.gameObject;
        fixtureDef.isSensor = rigidbody2D.isSensor();
        body.createFixture(fixtureDef);
    }

    public void addCircle2DCollider(Rigidbody2D rigidbody2D, Circle2DCollider circle2DCollider){
        Body body = rigidbody2D.getRawBody();
        assert body != null : "Raw body shouldn't be null!";

        CircleShape shape = new CircleShape();
        shape.setRadius(circle2DCollider.getRadius());
        shape.m_p.set(circle2DCollider.getOffset().x, circle2DCollider.getOffset().y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = rigidbody2D.getFriction();
        fixtureDef.userData = circle2DCollider.gameObject;
        fixtureDef.isSensor = rigidbody2D.isSensor();
        body.createFixture(fixtureDef);
    }

    public void resetPillBoxCollider(Rigidbody2D rigidbody2D, PillBoxCollider pillBoxCollider){
        Body body = rigidbody2D.getRawBody();
        if(body == null) return;
        int size = fixtureListSize(body);
        for (int i = 0; i < size; i++){
            body.destroyFixture(body.getFixtureList());
        }

        addPillBoxCollider(rigidbody2D, pillBoxCollider);
        body.resetMassData();
    }

    public void addPillBoxCollider(Rigidbody2D rigidbody2D, PillBoxCollider pillBoxCollider){
        Body body = rigidbody2D.getRawBody();
        assert body != null : "Raw body shouldn't be null!";

        addBox2Dcollider(rigidbody2D, pillBoxCollider.getBox2DCollider());
        addCircle2DCollider(rigidbody2D, pillBoxCollider.getTopCircle());
        addCircle2DCollider(rigidbody2D, pillBoxCollider.getBottomCircle());
    }

    public RaycastInfo raycast(GameObject reqObj, Vector2f point1, Vector2f point2){
        RaycastInfo callback = new RaycastInfo(reqObj);
        world.raycast(callback, new Vec2(point1.x, point1.y), new Vec2(point2.x, point2.y));
        return callback;
    }

    private int fixtureListSize(Body body){
        int size = 0;
        Fixture fixture = body.getFixtureList();
        while(fixture != null){
            size++;
            fixture = fixture.m_next;
        }
        return size;
    }

    public boolean locked(){
        return world.isLocked();
    }
}