package com.mygdx.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;


public class MapParser {
    private static final String MAP_LAYER_NAME_GROUND = "ground";
    private static final String MAP_LAYER_NAME_BOUNDS = "bounds";
    private static final String MAP_LAYER_NAME_DANGERS = "dangers";

    public static void parseMapLayers(World world, TiledMap tiledMap) {
        for (MapLayer layer : tiledMap.getLayers()) {

            for (MapObject object : layer.getObjects()) {
                Shape shape;
                if (object instanceof RectangleMapObject) {
                    shape = getRectangle((RectangleMapObject)object);
                }
                else if (object instanceof PolygonMapObject) {
                    shape = getPolygon((PolygonMapObject)object);
                }
                else if (object instanceof PolylineMapObject) {
                    shape = getPolyline((PolylineMapObject)object);
                }
                else if (object instanceof CircleMapObject) {
                    shape = getCircle((CircleMapObject)object);
                }
                else {
                    continue;
                }                if (layer.getName().equals(MAP_LAYER_NAME_GROUND))
                    new Ground(world, shape);
                if (layer.getName().equals(MAP_LAYER_NAME_BOUNDS))
                    new Bounds(world, shape);
                if (layer.getName().equals(MAP_LAYER_NAME_DANGERS))
                    new DangerZone(world, shape);
            }
        }
    }
    private static ChainShape createPolyline(PolylineMapObject polyline) {
        float[] vertices = polyline.getPolyline().getTransformedVertices();
        Vector2[] worldVerticies = new Vector2[vertices.length / 2];
        for (int i = 0; i < worldVerticies.length; i++) {
            worldVerticies[i] = new Vector2(vertices[i * 2] / MainGameActivity.PIXEL_PER_METER,
                    vertices[i * 2 + 1] / MainGameActivity.PIXEL_PER_METER);
        }
        ChainShape cs = new ChainShape();
        cs.createChain(worldVerticies);
        return cs;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / MainGameActivity.PIXEL_PER_METER,
                (rectangle.y + rectangle.height * 0.5f ) / MainGameActivity.PIXEL_PER_METER);
        polygon.setAsBox(rectangle.width * 0.5f /MainGameActivity.PIXEL_PER_METER,
                rectangle.height * 0.5f / MainGameActivity.PIXEL_PER_METER,
                size,
                0.0f);
        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / MainGameActivity.PIXEL_PER_METER);
        circleShape.setPosition(new Vector2(circle.x / MainGameActivity.PIXEL_PER_METER, circle.y / MainGameActivity.PIXEL_PER_METER));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            System.out.println(vertices[i]);
            worldVertices[i] = vertices[i] / MainGameActivity.PIXEL_PER_METER;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / MainGameActivity.PIXEL_PER_METER;
            worldVertices[i].y = vertices[i * 2 + 1] / MainGameActivity.PIXEL_PER_METER;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}
