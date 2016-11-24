package com.v5ent.game.core;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.entities.Npc;
import com.v5ent.game.entities.Role;
import com.v5ent.game.entities.Target;
import com.v5ent.game.pfa.GraphGenerator;
import com.v5ent.game.pfa.ManhattanDistance;
import com.v5ent.game.pfa.MyGraph;
import com.v5ent.game.pfa.MyNode;
import com.v5ent.game.pfa.MyPathSmoother;
import com.v5ent.game.pfa.MyRaycastCollisionDetector;
import com.v5ent.game.utils.Constants;

public class WorldController extends InputAdapter {

    private static final String TAG = WorldController.class.getName();

    public OrthographicCamera camera = null;

    public MapsManager mapMgr;
    public Role player;
    //行走路径
    private Array<MyNode> path = new Array<MyNode>(true, 10);
    private MyGraph graph;
    public Target target;

    public WorldController() {
        init();
    }

    private void init() {
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();

        mapMgr = new MapsManager();

        player = new Role("lante");
        player.setPosInMap(MapsManager.START_POINT);

        Gdx.input.setInputProcessor(this);
    }

    public void update(float deltaTime) {
        if (player.isArrived()) {
            target = null;
        }
        if (target != null) {
            target.update(deltaTime);
        }
        player.update(deltaTime);
        for (Npc npc : mapMgr.npcs) {
            npc.update(deltaTime);
//          npc.randomMove(this);
        }
        handleDebugInput(deltaTime);
        //camera follow the Player
        float x = player.getX();
        float y = player.getY();
        //make sure camera in map
        x = MathUtils.clamp(x, Constants.VIEWPORT_WIDTH / 2, mapMgr.cols * 32f - Constants.VIEWPORT_WIDTH / 2);
        y = MathUtils.clamp(y, Constants.VIEWPORT_HEIGHT / 2, mapMgr.rows * 32f - Constants.VIEWPORT_HEIGHT / 2);
        camera.position.set(x, y, 0f);
        camera.update();
    }

    private void handleDebugInput(float delta) {
        if (Gdx.app.getType() != ApplicationType.Desktop) return;

        // Selected Sprite Controls When Idle
        if (player.getState() == Role.State.IDLE) {
            int x = MathUtils.floor(player.getX() / 32);
            int y = MathUtils.floor(player.getY() / 32);
            //Keyboard input
            if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
                //Gdx.app.debug(TAG, "LEFT key");
                //Collision Test
                if (!isCollisionWithBlock(x - 1, y)) {
                    player.moveTo(x - 1, y);
                } else {
                    player.setCurrentDir(Role.Direction.LEFT);
                }
            } else if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
                //Gdx.app.debug(TAG, "RIGHT key");
                if (!isCollisionWithBlock(x + 1, y)) {
                    player.moveTo(x + 1, y);
                } else {
                    player.setCurrentDir(Role.Direction.RIGHT);
                }
            } else if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
                //Gdx.app.debug(TAG, "UP key");
                if (!isCollisionWithBlock(x, y + 1)) {
                    player.moveTo(x, y + 1);
                } else {
                    player.setCurrentDir(Role.Direction.UP);
                }
            } else if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
                //Gdx.app.debug(TAG, "DOWN key");
                if (!isCollisionWithBlock(x, y - 1)) {
                    player.moveTo(x, y - 1);
                } else {
                    player.setCurrentDir(Role.Direction.DOWN);
                }
            } else if (Gdx.input.isKeyPressed(Keys.Q)) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        // Reset game world
        if (keycode == Keys.R) {
            init();
            Gdx.app.debug(TAG, "Game world reset");
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 input = new Vector3(screenX, screenY, 0);
        camera.unproject(input);
        int x = MathUtils.floor(input.x / 32);
        int y = MathUtils.floor(input.y / 32);
        Gdx.app.debug(TAG, "clicked # (x:" + x + ",y:" + y + " )");
        //we click npc or block,set target
        if (!isCollisionWithNpc(x, y) && !isCollisionWithBlock(x, y)) {
            //A* path finding
            path.clear();
            Vector2 start = new Vector2(MathUtils.round(player.getX() / 32), MathUtils.round(player.getY() / 32));
            //we need set exactly start position
//            player.setPosInMap(start);
            Vector2 end = new Vector2(x, y);
            int numCols = mapMgr.cols;
            int numRows = mapMgr.rows;
            Gdx.app.debug(TAG, "From:" + start + " to " + end + "|numCols:" + numCols + "|numRows:" + numRows);
            int s = (int) start.x + ((int) start.y) * numCols;
            int t = (int) end.x + ((int) (end.y)) * numCols;
            final MyGraph graph = GraphGenerator.generateGraph(mapMgr.getBlockLayer(), mapMgr.npcs, numCols, numRows, 32, 32, start);
            final IndexedAStarPathFinder<MyNode> pathfinder = new IndexedAStarPathFinder<MyNode>(graph);
            final GraphPath<MyNode> outPath = new DefaultGraphPath<MyNode>();
            final boolean searchResult = pathfinder.searchNodePath(graph.getNodes().get(s), graph.getNodes().get(t), new ManhattanDistance(), outPath);
            MyPathSmoother pathSmoother = new MyPathSmoother(new MyRaycastCollisionDetector(graph));
            pathSmoother.smoothPath(outPath);
            StringBuilder sb = new StringBuilder();
            for (int i = outPath.getCount() - 1; i >= 0; i--) {
                sb.append(outPath.get(i).getX() + "," + outPath.get(i).getY() + "|");
                path.add(outPath.get(i));
            }
            Gdx.app.debug(TAG, "path:" + sb.toString());
            if (searchResult) {
//                    if(player.getState()== Role.State.IDLE) {
                player.followPath(path);
                Gdx.app.debug(TAG, "======================Follow Path==================");
//                    }else if(player.getState()== Role.State.WALKING) {
//                        player.appendPath(path);
//                        Gdx.app.debug(TAG,"======================Append Path==================");
//                    }
                target = new Target(x, y);
            }
        } else {
            target = null;
        }
        return true;
    }

    private boolean isCollisionWithNpc(int x, int y) {
        for (Npc npc : this.mapMgr.npcs) {
            int npcX = MathUtils.floor(npc.getX() / 32);
            int npcY = MathUtils.floor(npc.getY() / 32);
            if (npcX == x && npcY == y) {
                int x0 = MathUtils.floor(player.getX() / 32);
                int y0 = MathUtils.floor(player.getY() / 32);
                float distance = (x - x0) * (x - x0) + (y - y0) * (y - y0);
                if (distance < 10f) {
                    Gdx.app.debug(TAG, "distance:" + distance + " you clicked " + npc.getEntityId());
                    //TODO:talk with npc
                    //1.face to face
                    if (x0 < npcX) {
                        player.setCurrentDir(Role.Direction.RIGHT);
                        npc.setCurrentDir(Role.Direction.LEFT);
                    }
                    if (x0 > npcX) {
                        player.setCurrentDir(Role.Direction.LEFT);
                        npc.setCurrentDir(Role.Direction.RIGHT);
                    }
                    if (y0 < npcY) {
                        player.setCurrentDir(Role.Direction.UP);
                        npc.setCurrentDir(Role.Direction.DOWN);
                    }
                    if (y0 > npcY) {
                        player.setCurrentDir(Role.Direction.DOWN);
                        npc.setCurrentDir(Role.Direction.UP);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isCollisionWithBlock(int x, int y) {
        TiledMapTileLayer mapCollisionLayer = mapMgr.getBlockLayer();

        if (mapCollisionLayer == null) {
            return false;
        }

        if (mapCollisionLayer.getCell(x, y) != null) {
//            Gdx.app.debug(TAG, "Map Collision at = " + x + "," + y);
            return true;
        }
        //Npc's block
        for (Npc npc : mapMgr.npcs) {
            if (MathUtils.floor(npc.getX() / 32) == x && MathUtils.floor(npc.getY() / 32) == y) {
                return true;
            }
        }

        if (x < 0 || y < 0 || x >= mapMgr.cols || y >= mapMgr.rows) {
            return true;
        }

        return false;
    }

    public boolean isCollisionWithPlayer(int x, int y) {
        if (x == MathUtils.floor(player.getX() / 32) && y == MathUtils.floor(player.getY() / 32)) {
            Gdx.app.debug(TAG, "Player is here");
            return true;
        }
        return false;
    }

}
