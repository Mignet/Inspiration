package com.v5ent.game.core;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.v5ent.game.entities.EventObject;
import com.v5ent.game.entities.Npc;
import com.v5ent.game.entities.Role;
import com.v5ent.game.entities.TouchPoint;
import com.v5ent.game.screens.HUDScreen;
import com.v5ent.game.pfa.GraphGenerator;
import com.v5ent.game.pfa.ManhattanDistance;
import com.v5ent.game.pfa.MyGraph;
import com.v5ent.game.pfa.MyNode;
import com.v5ent.game.pfa.MyPathSmoother;
import com.v5ent.game.pfa.MyRaycastCollisionDetector;
import com.v5ent.game.utils.Constants;

public class WorldController extends InputAdapter {

    private static final String TAG = WorldController.class.getName();
    /**
     * main camera
     **/
    public OrthographicCamera camera = null;

    public OrthographicCamera hudCamera = null;
    private InputMultiplexer multiplexer;
    /**
     * head-up-display
     */
    public HUDScreen hudScreen;

    public MapsManager mapMgr;
    public Role player;
    //A path
    private Array<MyNode> path = new Array<MyNode>(true, 10);
    public TouchPoint touchPoint;

    public WorldController() {
        init();
    }

    private void init() {
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
//        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0);
        camera.update();

        player = new Role("lante");
        mapMgr = new MapsManager(player);


        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);//physical world
//        hudCamera.setToOrtho(false,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());//physical world
        hudCamera.update();
        hudScreen = new HUDScreen(this, player);
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hudScreen.getStage());
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
//        Gdx.input.setInputProcessor(this);
    }

    public void update(float deltaTime) {
        if (player.isArrived()) {
            touchPoint = null;
        }
        if (touchPoint != null) {
            touchPoint.update(deltaTime);
        }
        player.update(deltaTime);
        for (Npc npc : mapMgr.npcs) {
            npc.update(deltaTime);
            npc.randomMove(this);
        }
        handleDebugInput(deltaTime);
        //follow the Player
        float x = player.getX();
        float y = player.getY();
        camera.position.set(x, y, 0f);
        //make sure camera in map
//        x = MathUtils.clamp(x, Constants.VIEWPORT_WIDTH / 2, mapMgr.width - Constants.VIEWPORT_WIDTH / 2);
//        y = MathUtils.clamp(y, Constants.VIEWPORT_HEIGHT / 2, mapMgr.height - Constants.VIEWPORT_HEIGHT / 2);
        offsetCamera(mapMgr.width, mapMgr.height, camera);
        camera.update();
    }

    private void offsetCamera(int mapWidth, int mapHeight, Camera cam) {
        // These values likely need to be scaled according to your world coordinates.
        // The left boundary of the map (x)
        int mapLeft = 0;
        // The right boundary of the map (x + width)
        int mapRight = 0 + mapWidth;
        // The bottom boundary of the map (y)
        int mapBottom = 0;
        // The top boundary of the map (y + height)
        int mapTop = 0 + mapHeight;
        // The camera dimensions, halved
        float cameraHalfWidth = cam.viewportWidth * .5f;
        float cameraHalfHeight = cam.viewportHeight * .5f;

        // Move camera after player as normal

        float cameraLeft = cam.position.x - cameraHalfWidth;
        float cameraRight = cam.position.x + cameraHalfWidth;
        float cameraBottom = cam.position.y - cameraHalfHeight;
        float cameraTop = cam.position.y + cameraHalfHeight;

        // Horizontal axis
        if (mapWidth < cam.viewportWidth) {
            cam.position.x = mapRight / 2;
        } else if (cameraLeft <= mapLeft) {
            cam.position.x = mapLeft + cameraHalfWidth;
        } else if (cameraRight >= mapRight) {
            cam.position.x = mapRight - cameraHalfWidth;
        }

        // Vertical axis
        if (mapHeight < cam.viewportHeight) {
            cam.position.y = mapTop / 2;
        } else if (cameraBottom <= mapBottom) {
            cam.position.y = mapBottom + cameraHalfHeight;
        } else if (cameraTop >= mapTop) {
            cam.position.y = mapTop - cameraHalfHeight;
        }
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
        //we click npc or block,set touchPoint
        if (!isCollisionWithNpc(x, y) && !isCollisionWithBlock(x, y) && !isCollisionWithEvent(x,y)) {
            //A* path finding
            path.clear();
            Vector2 start = new Vector2(MathUtils.round(player.getX() / 32), MathUtils.round(player.getY() / 32));
            //we need set exactly start position
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
                player.followPath(path);
//                Gdx.app.debug(TAG, "======================Follow Path==================");
                touchPoint = new TouchPoint(x, y);
            }
        } else {
            touchPoint = null;
        }
        return true;
    }

    public void closeSpeechWithNpc() {
        Npc npc = getCurrentSelectedNpc();
        if(npc!=null){
            npc.setSelected(false);
            npc.setCurrentDir(npc.getDefaultDir());
            npc.setState(npc.getDefaultState());
        }
    }

    public boolean isCollisionWithTriggle(int x, int y) {
        return false;
    }

    private boolean isCollisionWithNpc(int x, int y) {
        for (Npc npc : this.mapMgr.npcs) {
            int npcX = MathUtils.floor(npc.getX() / 32);
            int npcY = MathUtils.floor(npc.getY() / 32);
            //this block is none
            if (npc.isSelected()) {
                npc.setSelected(false);
                npc.setCurrentDir(npc.getDefaultDir());
                npc.setState(npc.getDefaultState());
            }
            if (npcX == x && npcY == y) {
                int x0 = MathUtils.floor(player.getX() / 32);
                int y0 = MathUtils.floor(player.getY() / 32);
                float distance = (x - x0) * (x - x0) + (y - y0) * (y - y0);
                //I click you
                if (distance < 10f) {
                    Gdx.app.debug(TAG, "distance:" + distance + " you clicked " + npc.getEntityId());
                    //talk with npc,npc must fixed when talk
                    npc.setSelected(true);
                    npc.setState(Role.State.FIXED);
                    hudScreen.loadSpeech(npc);
                    //1.face to face
                    int gapX = x0 - npcX;
                    int gapY = y0 - npcY;
                    if (Math.abs(gapX) < Math.abs(gapY)) {
                        if (gapY < 0) {
                            player.setCurrentDir(Role.Direction.UP);
                            npc.setCurrentDir(Role.Direction.DOWN);
                        } else {
                            player.setCurrentDir(Role.Direction.DOWN);
                            npc.setCurrentDir(Role.Direction.UP);
                        }
                    } else {
                        if (gapX < 0) {
                            player.setCurrentDir(Role.Direction.RIGHT);
                            npc.setCurrentDir(Role.Direction.LEFT);
                        } else {
                            player.setCurrentDir(Role.Direction.LEFT);
                            npc.setCurrentDir(Role.Direction.RIGHT);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isCollisionWithEvent(int x, int y) {
        for (EventObject eo : this.mapMgr.events) {
            int eoX = MathUtils.floor(eo.getX() / 32);
            int eoY = MathUtils.floor(eo.getY() / 32);
            if (eoX == x && eoY == y) {
                int x0 = MathUtils.floor(player.getX() / 32);
                int y0 = MathUtils.floor(player.getY() / 32);
                float distance = (x - x0) * (x - x0) + (y - y0) * (y - y0);
                //I click you
                if (distance <= 1f) {
                    if(!eo.isToggled()){
                        executeCommand(eo.getCommand());
                        setToggleOnGroup(eo.getName());
                    }
                    return true;
                }
            }
        }
        return false;
    }
    private void setToggleOnGroup(String name){
        for (EventObject eo : this.mapMgr.events) {
            if(name.equals(eo.getName())){
                eo.setToggled(true);
            }
        }
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

    public Npc getCurrentSelectedNpc() {
        for (Npc npc : this.mapMgr.npcs) {
            if (npc.isSelected()) {
                return npc;
            }
        }
        return null;
    }

 /********************************************* COMMAND ********************************************************/
    public void executeCommand(String cmd){
        Gdx.app.debug(TAG,"preform cmd:"+cmd);
        String[] list = cmd.split(",");
        if("MapTo".equals(list[0])){
            Transfer(list[1],new Vector2(Integer.valueOf(list[2]),Integer.valueOf(list[3])));
        }
        if("openBox".equals(list[0])){
            OpenBox();
        }
    }

    /******************************************** Triggle Event*************************************/
    public void Transfer(String mapName,Vector2 pos){
        mapMgr.loadMap(mapName);
        player.setPosInMap(pos);
    }

    public void OpenBox(){
        //add money
    }
    /*******************************************************************************************/
}
