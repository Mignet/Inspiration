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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.v5ent.game.entities.Aim;
import com.v5ent.game.entities.Npc;
import com.v5ent.game.entities.Role;
import com.v5ent.game.entities.Trap;
import com.v5ent.game.pfa.GraphGenerator;
import com.v5ent.game.pfa.ManhattanDistance;
import com.v5ent.game.pfa.MyGraph;
import com.v5ent.game.pfa.MyNode;
import com.v5ent.game.pfa.MyPathSmoother;
import com.v5ent.game.pfa.MyRaycastCollisionDetector;
import com.v5ent.game.screens.HUDScreen;
import com.v5ent.game.skill.Skill;
import com.v5ent.game.utils.Constants;
import com.v5ent.game.utils.Trace;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.Gdx.input;

public class WorldController extends InputAdapter {

    private static final String TAG = WorldController.class.getName();
    /**
     * main camera
     **/
    public OrthographicCamera camera = null;

    public OrthographicCamera hudCamera = null;
    private InputMultiplexer multiplexer;
    public Skill skill;
    /**
     * head-up-display
     */
    public HUDScreen hudScreen;

    public MapsManager mapMgr;
    public Role player;
    //A path
    private Array<MyNode> path = new Array<MyNode>(true, 10);
    public Aim aim;

    public WorldController() {
        init();
    }

    private void init() {
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();

        player = new Role("lante");
        mapMgr = new MapsManager(player);

        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);//physical world
        hudCamera.update();
        hudScreen = new HUDScreen(this, player);
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(hudScreen.getStage());
        multiplexer.addProcessor(this);
        input.setInputProcessor(multiplexer);
    }

    public void update(float deltaTime) {
        if (player.isArrived()) {
            aim = null;
        }
        if (aim != null) {
            aim.update(deltaTime);
        }
        int cx = MathUtils.floor(player.getX() / 32);
        int cy = MathUtils.floor(player.getY() / 32);
        if(isCollisionWithTrap(cx,cy)){
            //
            Gdx.app.debug(TAG,"Trap");
        }
        if(isCollisionWithEnemy(mapMgr)){
            Gdx.app.debug(TAG,"Let's Fight!");
        }
        player.update(deltaTime);
        for (Npc npc : mapMgr.npcs) {
            npc.update(deltaTime);
//            npc.randomMove(this);
        }
        if(skill!=null)skill.update(deltaTime);
        if(skill!=null&&skill.isEnded())skill = null;
        //forbidden keyboard
//        handleDebugInput(deltaTime);
        //follow the Player
        float x = player.getX();
        float y = player.getY();
        camera.position.set(x, y, 0f);
        //make sure camera in map
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
            //triggle
            if(isCollisionWithTrap(x,y)){
                return;
            }
            //Keyboard input
            if (input.isKeyPressed(Keys.A) || input.isKeyPressed(Keys.LEFT)) {
                //Gdx.app.debug(TAG, "LEFT key");
                //Collision Test
                if (!isCollisionWithBlock(x - 1, y)) {
                    player.moveTo(x - 1, y);
                } else {
                    player.setCurrentDir(Role.Direction.LEFT);
                }
            } else if (input.isKeyPressed(Keys.D) || input.isKeyPressed(Keys.RIGHT)) {
                //Gdx.app.debug(TAG, "RIGHT key");
                if (!isCollisionWithBlock(x + 1, y)) {
                    player.moveTo(x + 1, y);
                } else {
                    player.setCurrentDir(Role.Direction.RIGHT);
                }
            } else if (input.isKeyPressed(Keys.W) || input.isKeyPressed(Keys.UP)) {
                //Gdx.app.debug(TAG, "UP key");
                if (!isCollisionWithBlock(x, y + 1)) {
                    player.moveTo(x, y + 1);
                } else {
                    player.setCurrentDir(Role.Direction.UP);
                }
            } else if (input.isKeyPressed(Keys.S) || input.isKeyPressed(Keys.DOWN)) {
                //Gdx.app.debug(TAG, "DOWN key");
                if (!isCollisionWithBlock(x, y - 1)) {
                    player.moveTo(x, y - 1);
                } else {
                    player.setCurrentDir(Role.Direction.DOWN);
                }
            } else if (input.isKeyPressed(Keys.Q)) {
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

    /**轨迹点*/
    List<Vector2> points = new ArrayList<Vector2>();
    Vector2 startPoint = new Vector2(),endPoint = new Vector2();

    @Override
    public boolean touchUp(int screenX,int screenY, int pointer, int button) {
        Vector3 input = new Vector3(screenX, screenY, 0);
        camera.unproject(input);
        float x = input.x;
        float y = input.y;
        Gdx.app.debug(TAG, "pointer END:"+pointer+"=>("+x+","+y+")");
        endPoint.set(x,y);
        //如果起点和终点一样，我们认为是点击
        if(MathUtils.floor(startPoint.x/32)==MathUtils.floor(endPoint.x/32)&&
                MathUtils.floor(startPoint.y/32)==MathUtils.floor(endPoint.y/32)){
                //移动到该位置
            touchToMove(screenX,screenY);
            points.clear();
            return false;
        }
        //判断是否施法成功
        if(!Trace.symbol(this,points,player)){
            //TODO:自动物理攻击
        };
        points.clear();
        return false;
    }
    @Override
    public boolean touchDragged(int screenX,int screenY, int pointer) {
        Vector3 input = new Vector3(screenX, screenY, 0);
        camera.unproject(input);
        float x = input.x;
        float y = input.y;
//		Gdx.app.debug(TAG, "pointer:"+pointer+"=>("+x+","+y+")=>Cursor:("+cursor.getX()+","+cursor.getY()+")");
        points.add(new Vector2(x,y));
        return false;
    }
    @Override
    public boolean touchDown(int screenX,int screenY, int pointer, int button) {
        Vector3 input = new Vector3(screenX, screenY, 0);
        camera.unproject(input);
        float x = input.x;
        float y = input.y;
        startPoint.set(x, y);
        Gdx.app.debug(TAG, "pointer START:"+pointer+"=>("+x+","+y+")");
        return false;
    }
    public void touchToMove(int screenX,int screenY){
        Vector3 input = new Vector3(screenX, screenY, 0);
        camera.unproject(input);
        int x = MathUtils.floor(input.x / 32);
        int y = MathUtils.floor(input.y / 32);
        Gdx.app.debug(TAG, "clicked # (x:" + x + ",y:" + y + " )");
        //we click not npc or block,set aim to move
        if (!isCollisionWithNpc(x, y) && !isCollisionWithBlock(x, y)) {
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
            List<Sprite> temp =new ArrayList<Sprite>();
            temp.addAll(mapMgr.npcs);
//            temp.addAll(mapMgr.events);
            final MyGraph graph = GraphGenerator.generateGraph(mapMgr.getBlockLayer(),temp, numCols, numRows, 32, 32, start);
            final IndexedAStarPathFinder<MyNode> pathfinder = new IndexedAStarPathFinder<MyNode>(graph);
            final GraphPath<MyNode> outPath = new DefaultGraphPath<MyNode>();
            final boolean searchResult = pathfinder.searchNodePath(graph.getNodes().get(s), graph.getNodes().get(t), new ManhattanDistance(), outPath);
            MyPathSmoother pathSmoother = new MyPathSmoother(new MyRaycastCollisionDetector(graph));
            pathSmoother.smoothPath(outPath);
            StringBuilder sb = new StringBuilder();
            for (int i = outPath.getCount() - 1; i >= 0; i--) {
                sb.append("("+outPath.get(i).getX() + "," + outPath.get(i).getY() + ")|");
                path.add(outPath.get(i));
            }
            if (searchResult) {
                Gdx.app.debug(TAG, "Start Follow Path:" + sb.toString());
                player.followPath(path);
                aim = new Aim(x, y);
            }
        } else {
            aim = null;
        }
    }

    public void closeSpeechWithNpc() {
        Npc npc = getCurrentSelectedNpc();
        if(npc!=null){
            npc.setSelected(false);
            npc.setCurrentDir(npc.getDefaultDir());
            npc.setState(npc.getDefaultState());
        }
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
                    //2.talk with npc,npc must be fixed when talk
                    npc.setSelected(true);
                    npc.setState(Role.State.FIXED);
                    //3. show talk dialog
                    hudScreen.loadSpeech(npc);
                }
                return true;
            }
        }
        return false;
    }

    public boolean isCollisionWithTrap(int x, int y) {
        for (Trap eo : this.mapMgr.traps) {
            int eoX = MathUtils.floor(eo.getX() / 32);
            int eoY = MathUtils.floor(eo.getY() / 32);
            if (eoX == x && eoY == y) {
                int x0 = MathUtils.floor(player.getX() / 32);
                int y0 = MathUtils.floor(player.getY() / 32);
                float distance = (x - x0) * (x - x0) + (y - y0) * (y - y0);
                //I trap you
                if (distance <= 0f) {
                    Gdx.app.debug(TAG,"trigger event:"+eo.getName()+"|toggle:"+eo.isToggled());
                    if(!eo.isToggled()){
                        //1.face to item
                        int gapX = x0 - eoX;
                        int gapY = y0 - eoY;
                        if (Math.abs(gapX) < Math.abs(gapY)) {
                            if (gapY < 0) {
                                player.setCurrentDir(Role.Direction.UP);
                            } else {
                                player.setCurrentDir(Role.Direction.DOWN);
                            }
                        } else {
                            if (gapX < 0) {
                                player.setCurrentDir(Role.Direction.RIGHT);
                            } else {
                                player.setCurrentDir(Role.Direction.LEFT);
                            }
                        }
                        //stop walk
                        player.clearPathAndStop();
                        //2.start to execute
                        executeCommand(eo.getCommand());
                        setToggleOnGroup(eo.getName());
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * toggle all the same name tiles
     * @param name
     */
    private void setToggleOnGroup(String name){
        for (Trap eo : this.mapMgr.traps) {
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
    private String _previousEnemySpawn = "0";
    public String _enemySpawnID = null;
    private boolean isCollisionWithEnemy(MapsManager mapMgr){
        MapLayer mapEnemySpawnLayer =  mapMgr.getEnemySpawnLayer();

        if( mapEnemySpawnLayer == null ){
            return false;
        }

        Rectangle rectangle = null;

        for( MapObject object: mapEnemySpawnLayer.getObjects()){
            if(object instanceof RectangleMapObject) {
                rectangle = ((RectangleMapObject)object).getRectangle();
                Rectangle _boundingBox = player.getBoundingRectangle();
                if (_boundingBox.overlaps(rectangle) ){
                    String enemySpawnID = object.getName();

                    if( enemySpawnID == null ) {
                        return false;
                    }

                    if( _previousEnemySpawn.equalsIgnoreCase(enemySpawnID) ){
                        //Gdx.app.debug(TAG, "Enemy Spawn Area already activated " + enemySpawnID);
                        return true;
                    }else{
                        Gdx.app.debug(TAG, "Enemy Spawn Area " + enemySpawnID + " Activated with previous Spawn value: " + _previousEnemySpawn);
                        _previousEnemySpawn = enemySpawnID;
                    }
                    _enemySpawnID = enemySpawnID;
//                    notify(enemySpawnID, ComponentObserver.ComponentEvent.ENEMY_SPAWN_LOCATION_CHANGED);
                    return true;
                }
            }
        }

        //If no collision, reset the value
        if( !_previousEnemySpawn.equalsIgnoreCase(String.valueOf(0)) ){
            Gdx.app.debug(TAG, "Enemy Spawn Area RESET with previous value " + _previousEnemySpawn);
            _previousEnemySpawn = String.valueOf(0);
            _enemySpawnID = _previousEnemySpawn;
//            notify(_previousEnemySpawn, ComponentObserver.ComponentEvent.ENEMY_SPAWN_LOCATION_CHANGED);
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
            Transfer(list[1],new Vector2(Integer.valueOf(list[2]),Integer.valueOf(list[3])),Role.Direction.valueOf(list[4]));

        }
        if("openBox".equals(list[0])){
            OpenBox();
        }
    }

    /******************************************** Triggle Event*************************************/
    public void Transfer(final String mapName,final Vector2 pos,final Role.Direction dir){
        float delay = 0.1000f; // seconds
        player.setVisible(false);
        Timer.schedule(new Timer.Task(){
            @Override
            public void run() {
                mapMgr.loadMap(mapName);
                player.setCurrentDir(dir);
                player.setPosInMap(pos);
                player.setVisible(true);
            }
        }, delay);
    }

    public void OpenBox(){
        //add money
    }
    /*******************************************************************************************/
}
