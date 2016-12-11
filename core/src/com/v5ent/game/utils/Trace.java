package com.v5ent.game.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.v5ent.game.core.WorldController;
import com.v5ent.game.entities.Role;
import com.v5ent.game.skill.Earth;
import com.v5ent.game.skill.Fire;
import com.v5ent.game.skill.Thunder;
import com.v5ent.game.skill.Water;

public class Trace {

	private static final String TAG = Trace.class.getName();
	private static int DISTANCE = 200;
	public static boolean symbol(WorldController worldController, List<Vector2> points, Role player){
		//need to recognise the shap what user drawn
        /*
        * e.g. 'W' WATER, 'triangle' FIRE, 'N' THUNDER,'8' WIND,'M' EARTH
        */
        /***************************************************************
        *                  Cartesian coordinate system                 *
        ****************************************************************/
        //firstly,recognize the start direction
        float x0,y0,x1,y1,x2,y2;
        double theta0;
		double theta1;
		double theta2;
        theta0 = -1;theta1 = -1;theta2 = -1;
        float dir = 0;float threshold;
        //this is POINTS for store the feature-point
        List<Vector2>  feature_points = new ArrayList<Vector2>();
        int size;
        threshold = 60;//degree
        //delete the same points
        int i;i =1;
        while(i < points.size()){
        	x0 = points.get(i).x - points.get(i-1).x;
        	y0 = points.get(i).y - points.get(i-1).y;
            if(x0 == 0 && y0 ==0 ){
            	points.remove(i);
                i -= 1;
            }
            i += 1;
        }
        if(points.size()>=4){
        	//1,get direction by first three points
        	x0 = points.get(1).x - points.get(0).x;
        	y0 = points.get(1).y - points.get(0).y;
        	x1 = points.get(2).x - points.get(1).x;
        	y1 = points.get(2).y - points.get(1).y;
        	x2 = points.get(3).x - points.get(2).x;
        	y2 = points.get(3).y - points.get(2).y;
        	int dir0,dir1,dir2;
        	dir0 = direction_xy(x0,y0);dir1 = direction_xy(x1,y1);dir2 = direction_xy(x2,y2);
        	float offset;offset = 0.5f;
        	float x_,y_;
        	if(Math.abs(x0*y0) > 0 ){
        		theta0 = Math.atan(Math.abs(y0)/Math.abs(x0));
        	}
        	if(Math.abs(x1*y1) > 0 ){
        		theta1 = Math.atan(Math.abs(y1)/Math.abs(x1));
        	}
        	if(Math.abs(x2*y2) > 0 ){
        		theta2 = Math.atan(Math.abs(y2)/Math.abs(x2));
        	}
        	if(theta0 != -1 && Math.abs(radtodeg(theta0)) < offset && theta1 != -1 && Math.abs(radtodeg(theta1)) < offset && theta2 != -1 && Math.abs(radtodeg(theta2)) < offset){
        		dir = 4;
        	}else{
        		if(dir0 == dir1 || dir1 == dir2){
        			dir = dir1;
        		}
        		if(dir0 == dir2){
        			dir = dir0;
        		}
        	}
        	Gdx.app.debug(TAG, "x0:"+x0+" y0:"+y0+" x1:"+x1+" y1:"+y1+" dir:"+dir);
        	double p_dir = dirtorad(player.getCurrentDir());
        	//****************************************************************/
        	if(dir == 3){ //Thunder
        		threshold = 60;//degree
        		feature_points = collectFeatures(points,threshold);
        		size = feature_points.size();
        		//important feature
        		if(size >=2 
        				&& direction_xy(feature_points.get(1).x-feature_points.get(0).x,feature_points.get(1).y-feature_points.get(0).y) == 1){
        			Gdx.app.debug(TAG,"There is/are "+(Math.floor(size/2))+" Thunder(s)!!!size:"+(size));
        			Gdx.app.debug(TAG, "At direction:"+(p_dir)+" x:"+(player.getX())+" y:"+(player.getY())+" cos(direction)*DISTANCE:"+(Math.cos(p_dir)*DISTANCE)+" sin(direction)*DISTANCE:"+Math.sin(p_dir)*DISTANCE);
        			worldController.skill = new Thunder();
        			/*Vector2 p = worldController.map.getMapCellPostion(player.getPosition());
        			worldController.map.setObjectPosition(worldController.skill,new Vector2(p.x+2,p.y+2))*/;
        			worldController.skill.setPosition(
        					player.getX() +(float) (Math.cos(p_dir)*DISTANCE), 
        					player.getY()+ (float)(Math.sin(p_dir)*DISTANCE));
        			Gdx.app.debug(TAG, " x:"+((float) (Math.cos(p_dir)*DISTANCE))+" y:"+((float)(Math.sin(p_dir)*DISTANCE))+" dir:"+radtodeg(p_dir));
        			Gdx.app.debug(TAG, "Skill x:"+(player.getX() +(float) (Math.cos(p_dir)*DISTANCE))+" y:"+(player.getY()+ (float)(Math.sin(p_dir)*DISTANCE))+" dir:"+radtodeg(p_dir));
//                om = instance_create(player.x + cos(p_dir)*DISTANCE, player.y - sin(p_dir)*DISTANCE,player_magic);//water
//                om.sprite_index = skill_thunder;
//                om.damage_value = 120;
					return true;
        		}
        	}
        	if(dir == 4){ //Water
        		threshold = 60;//degree
        		feature_points = collectFeatures(points,threshold);
        		size = feature_points.size();
        		if(
        				(size>1 
        						&& direction_xy(feature_points.get(1).x-feature_points.get(0).x,feature_points.get(1).y-feature_points.get(0).y)==1)
        						||
        						(size==1 
        						&& direction_xy(points.get(size).x-feature_points.get(0).x,points.get(size).y-feature_points.get(0).y)==0))
        		{
        			Gdx.app.debug(TAG,"There is/are "+(Math.floor((size+1)/2))+" Water(s)!!!size:"+(size));
        			worldController.skill = new Water();
        			worldController.skill.setPosition(
        					player.getX() +(float) (Math.cos(p_dir)*DISTANCE), 
        					player.getY()+ (float)(Math.sin(p_dir)*DISTANCE));
//                    om = instance_create(player.x + cos(p_dir)*DISTANCE, player.y - sin(p_dir)*DISTANCE,player_magic);//water
//                    om.sprite_index = skill_water;
					return true;
        		}
        	}
        	if(dir == 1){ //Earth
        		threshold = 60;//degree
        		feature_points = collectFeatures(points,threshold);
        		size = feature_points.size();
        		if(size >1 
        				&& direction_xy(feature_points.get(1).x-feature_points.get(0).x,feature_points.get(1).y-feature_points.get(0).y) == 4){
        			Gdx.app.debug(TAG,"There is/are "+Math.floor((size+1)/2)+" GroundSpike(s)!!!size:"+size);
        			worldController.skill = new Earth();
        			worldController.skill.setPosition(
        					player.getX() +(float) (Math.cos(p_dir)*DISTANCE), 
        					player.getY()+ (float)(Math.sin(p_dir)*DISTANCE));
//                om = instance_create(player.getX() + Math.cos(p_dir)*DISTANCE, player.getY() - Math.sin(p_dir)*DISTANCE,player_magic);//water
//                om.sprite_index = skill_wind;
//                om.damage_value = 105;
					return true;
        		}
        	}
        	if(dir == 2){ //Fire
        		threshold = 60;//degree
        		feature_points = collectFeatures(points,threshold);
        		size = feature_points.size();
        		if(size >=1 
        				&& direction_xy(feature_points.get(0).x-points.get(0).x,(points.get(0).y-feature_points.get(0).y)) == 3){
        			Gdx.app.debug(TAG,"There is/are "+(Math.floor((size+1)/2))+" Raging fire(s)!!!size:"+(size));
        			worldController.skill = new Fire();
        			worldController.skill.setPosition(
        					player.getX() +(float) (Math.cos(p_dir)*DISTANCE), 
        					player.getY()+ (float)(Math.sin(p_dir)*DISTANCE));
//               om = instance_create(player.getX() + Math.cos(p_dir)*DISTANCE, player.getY() - Math.sin(p_dir)*DISTANCE,player_magic);//water
//               om.sprite_index = skill_fire;
//               om.damage_value = 95;
					return true;
        		}
        	}
        }
		return false;
	}
	
	/**
	 * 根据方向转换角度
	 * @param dir
	 * @return
	 */
	private static double dirtorad(Role.Direction dir) {
		if(Role.Direction.UP.equals(dir)){
			return Math.PI/2;
		}
		if(Role.Direction.DOWN.equals(dir)){
			return Math.PI*3/2;
		}
		if(Role.Direction.LEFT.equals(dir)){
			return Math.PI;
		}
		if(Role.Direction.RIGHT.equals(dir)){
			return 0;
		}
		/*if(Role.Direction.UP_LEFT.equals(dir)){
			return Math.PI*3/4;
		}
		if(Role.Direction.UP_RIGHT.equals(dir)){
			return Math.PI*1/4;
		}
		if(Role.Direction.DOWN_LEFT.equals(dir)){
			return Math.PI*5/4;
		}
		if(Role.Direction.DOWN_RIGHT.equals(dir)){
			return Math.PI*7/4;
		}*/
		return 0;
	}

	/**
	 * 收集特征值
	 * @author dxtx
	 * @param points - 原始points
	 * @param threshold - degree
	 * @return feature points
	 * @since JDK 1.6
	 */
	private static List<Vector2> collectFeatures(List<Vector2> points, float threshold) {
	    List<Vector2> feature_points = new ArrayList<Vector2>();
	    int n;
	    //theta = arccos((x0*x1+y0*y1)/(sqrt((sqr(x0)+sqr(y0))*(sqr(x1)+sqr(y1)))));
	        for(n=2;n<points.size();n+=1){
	        	float x0 = points.get(n-1).x - points.get(n-2).x;
	        	float y0 = points.get(n-1).y - points.get(n-2).y;
	        	float x1 = points.get(n).x - points.get(n-1).x;
	        	float y1 = points.get(n).y - points.get(n-1).y;
	            //if(x0 == 0 && y0 == 0)||(x1 == 0 && y1 == 0) continue;
	            //threshold value 90(degree)
//	        	Gdx.app.debug(TAG,"x0:"+(x0)+" y0:"+(y0)+" x1:"+(x1)+" y1:"+(y1));
	            double theta = Math.acos((x0*x1+y0*y1)/(Math.sqrt((x0*x0+y0*y0)*(x1*x1+y1*y1))));
//	            Gdx.app.debug(TAG,"theta:"+theta+" radtodeg(theta):"+radtodeg(theta)+" threshold:"+(threshold)+" theta>threshold?"+(radtodeg(theta) > threshold));
	            if(radtodeg(theta) > threshold){
	                //this is a inflexion point
	            	Gdx.app.debug(TAG,"feature_points:("+points.get(n-1).x+" ,"+points.get(n-1).y+")");
	            	feature_points.add(new Vector2(points.get(n-1)));
	            }
	        }
	    return feature_points;
	}

	//rad to degree
	private static double radtodeg(double theta) {
		return (theta/Math.PI)*180;
	}
	
	//dir 划分4象限
	private static int direction_xy(float x, float y) {
		if( x > 0 && y > 0 ){//第一象限
	        return 1;
	    }else if(x > 0 && y < 0){//第四象限
	        return 4;
	    }else if(x < 0 && y < 0){//第三象限
	        return 3;
	    }else if(x < 0 && y > 0){//第二象限
	        return 2;
	    }else{
	    	Gdx.app.debug(TAG,"Exception:("+x+","+y+")");
	        return 0;
	    }
	}
}

