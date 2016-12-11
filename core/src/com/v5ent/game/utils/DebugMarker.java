package com.v5ent.game.utils;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class DebugMarker {
	private static ShapeRenderer debugRenderer = new ShapeRenderer();
	
	private static final String TAG = DebugMarker.class.getName();
	
	public static void dispose(){
		debugRenderer.dispose();
	}
    public static void drawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(color);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
    
    public static void drawPolygon(Polygon polygon, Matrix4 projectionMatrix)
    {
    	
    	Gdx.gl.glLineWidth(2);
    	debugRenderer.setProjectionMatrix(projectionMatrix);
    	debugRenderer.begin(ShapeRenderer.ShapeType.Line);
    	
    	debugRenderer.setColor(Color.RED);
    	float[] vertices = polygon.getVertices();
        for(int i=0; i<vertices.length; i+=2){
        	debugRenderer.line(vertices[i], vertices[i+1], vertices[(i+2)%vertices.length], vertices[(i+3)%vertices.length]);
        }
    	debugRenderer.end();
    	Gdx.gl.glLineWidth(1);
    }

    public static void drawDebugLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix)
    {
        Gdx.gl.glLineWidth(2);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.WHITE);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

	public static void drawTrace(List<Vector2> points, Matrix4 projectionMatrix) {
		Gdx.gl.glLineWidth(2);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.WHITE);
        for(int i=0;i<points.size()-1;i++){
        	debugRenderer.line(points.get(i), points.get(i+1));
        }
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
	}
	
	public static ShapeRenderer getDebugRenderer() {
		return debugRenderer;
	}
}

