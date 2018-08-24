package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MainGameActivity extends ApplicationAdapter {

    public static final float PIXEL_PER_METER = 32f;
    private static final float SCALE = 3.5f;
    private static final float TIME_STEP = 1 / 60f;
    private static final int VELOCITY_ITERATIONS = 6;
    private static final int POSITION_ITERATIONS = 2;
    private static final float VELOCITY_Y = -9.85f;
    private static final float VELOCITY_X = 0f;
    private static final String MAP_PATH = "data/GameMap.tmx";
    private OrthographicCamera orthographicCamera;
    private Box2DDebugRenderer box2DDebugRenderer;
    private World world;
    private Player player;
    private SpriteBatch batch;
    private Texture texture;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private TiledMap tiledMap;
    private Stage stage;
    private AnalogStick analogStick;
    private ShapeRenderer debugRenderer;
    private boolean debug = true;

    private Animation<TextureRegion> stand;
    private Animation<TextureRegion> walk;
    private Animation<TextureRegion> jump;

    @Override
    public void create() {

        texture = new Texture("hero/adventurer-Sheet.png");
        TextureRegion[][] regions = TextureRegion.split(texture, 50, 37);

        stand = new Animation<TextureRegion>(1f/4f, regions[0][0], regions[0][1], regions[0][2], regions[0][3]);
        walk = new Animation<TextureRegion>(1f/4f, regions[1][1], regions[1][2], regions[1][3], regions[1][4], regions[1][5], regions[1][6]);


        orthographicCamera = new OrthographicCamera();
        orthographicCamera.setToOrtho(false, Gdx.graphics.getWidth() / SCALE, Gdx.graphics.getHeight() / SCALE);
        world = new World(new Vector2(VELOCITY_X, VELOCITY_Y), false);
        batch = new SpriteBatch();
        texture = new Texture(Player.PLAYER_IMG_PATH);
        box2DDebugRenderer = new Box2DDebugRenderer();
        tiledMap = new TmxMapLoader().load(MAP_PATH);
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        MapParser.parseMapLayers(world, tiledMap);
        player = new Player(world);
        world.setContactListener(new WorldContactListener());


        analogStick = new AnalogStick(15, 15);
        stage = new Stage();
        stage.addActor(analogStick);

        Gdx.input.setInputProcessor(stage);

        debugRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();

        update(deltaTime);

        Gdx.gl.glClearColor(0.5f, 0.8f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiledMapRenderer.render();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        renderPlayer(deltaTime);
    }

    private void renderPlayer(float deltaTime) {
        // based on the player state, get the animation frame
        TextureRegion frame = null;
        switch (player.state) {
            case Standing:
                frame = stand.getKeyFrame(player.stateTime, true);
                break;
            case Walking:
                frame = walk.getKeyFrame(player.stateTime, true);
                break;
            case Jumping:
                frame = jump.getKeyFrame(player.stateTime, true);
                break;
        }

        // draw the player, depending on the current velocity
        // on the x-axis, draw the koala facing either right
        // or left
        Batch batch = tiledMapRenderer.getBatch();
        batch.begin();
        if (player.facesRight) {
            batch.draw(
                    frame,
                    player.getBody().getPosition().x * PIXEL_PER_METER - (texture.getWidth() / 2),
                    player.getBody().getPosition().y * PIXEL_PER_METER - (texture.getHeight() / 2),
                    texture.getWidth(),
                    texture.getHeight()
            );
        } else {
            batch.draw(
                    frame,
                    player.getBody().getPosition().x * PIXEL_PER_METER - (texture.getWidth() / 2),
                    player.getBody().getPosition().y * PIXEL_PER_METER - (texture.getHeight() / 2),
                    texture.getWidth(),
                    texture.getHeight()
            );
        }
        batch.end();

    }

    private void update(float deltaTime) {
        world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        playerUpdate(deltaTime);
        cameraUpdate();
        tiledMapRenderer.setView(orthographicCamera);
        batch.setProjectionMatrix(orthographicCamera.combined);
    }

    private void cameraUpdate() {
        Vector3 position = orthographicCamera.position;
        position.x = player.getBody().getPosition().x * PIXEL_PER_METER;
        position.y = player.getBody().getPosition().y * 1.7f * PIXEL_PER_METER;
        orthographicCamera.position.set(position);
        orthographicCamera.update();
    }

    @Override
    public void resize(int width, int height) {
        orthographicCamera.setToOrtho(false, width / SCALE, height / SCALE);
    }

    @Override
    public void dispose() {
        texture.dispose();
        batch.dispose();
        box2DDebugRenderer.dispose();
        world.dispose();
        tiledMapRenderer.dispose();
        tiledMap.dispose();
    }

    private void playerUpdate(float deltaTime) {

        if (deltaTime == 0) return;

        if (deltaTime > 0.1f)
            deltaTime = 0.1f;

        player.stateTime += deltaTime;

        boolean isJumping = false;
        boolean isCrouching = false;

        Float percentageX = analogStick.getKnobPercentX();
        Float percentageY = analogStick.getKnobPercentY();

        if (percentageY >= 0.75f) {
            //isJumping = true;
        } else if (percentageY < 0.75f && percentageY >= 0.25f) {
            isJumping = false;
            isCrouching = false;
        } else if (percentageY < 0.25f) {
            isCrouching = true;
        }

        if (percentageX < 0f) {
            player.facesRight = false;
            player.state = Player.State.Walking;
            player.velocity.x = Player.RUN_FORCE * percentageX;

        } else if (percentageX > 0f) {
            player.facesRight = true;
            player.state = Player.State.Walking;
            player.velocity.x = Player.RUN_FORCE * percentageX;

        }

        if (percentageX == 0f && percentageY == 0f) {
            //near the player.velocity.x to 0
            player.velocity.x *= Player.DAMPING;
        }

        if (player.velocity.x < 0.5f && player.velocity.x > -0.5f) {
            player.state = Player.State.Standing;
            player.velocity.x = 0f;
        }

        if (player.isDead()) {
            world.destroyBody(player.getBody());
            player = new Player(world);
        }

        if (isJumping) {
            player.getBody().applyForceToCenter(0, Player.JUMP_FORCE, false);
        }

        player.getBody().setLinearVelocity(player.velocity.x, player.getBody().getLinearVelocity().y);
    }


}
