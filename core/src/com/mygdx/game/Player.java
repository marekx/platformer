package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player {

    public static final String PLAYER_IMG_PATH = "adventurer-idle-00.png";
    public static final float JUMP_FORCE = 250f;
    public static final float RUN_FORCE = 10f;
    private static final int BOX_SIZE = 32;
    private static final float PLAYER_DENSITY = 1.0f;
    private static final float PLAYER_START_X = 8f;
    private static final float PLAYER_START_Y = 18f;
    public static final float DAMPING = 0.87f;

    State state = State.Standing;
    private Body body;
    private boolean isJumping = false;
    private boolean isCrouching = false;
    private boolean isDead = false;
    boolean facesRight = true;
    final Vector2 velocity = new Vector2();
    float stateTime;

    enum State {
        Standing, Walking, Jumping, Crouching
    }


    public Player(World world) {
        createBoxBody(world, PLAYER_START_X, PLAYER_START_Y);
    }

    private void createBoxBody(World world, float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.fixedRotation = true;
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(BOX_SIZE / MainGameActivity.PIXEL_PER_METER / 2, BOX_SIZE / MainGameActivity.PIXEL_PER_METER / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = PLAYER_DENSITY;
        body = world.createBody(bdef);
        body.createFixture(fixtureDef).setUserData(this);
    }

    public Body getBody() {
        return body;
    }

    public void hit() {
        isDead = true;
    }
    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }
    public boolean isJumping() {
        return isJumping;
    }
    public boolean isDead() {
        return isDead;
    }

    public boolean isCrouching() {
        return isCrouching;
    }

    public void setCrouching(boolean crouching) {
        isCrouching = crouching;
    }
}
