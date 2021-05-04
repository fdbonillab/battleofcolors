package ga.kylemclean.minesweeper.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import ga.kylemclean.minesweeper.Minesweeper;
import ga.kylemclean.minesweeper.game.Board;
import ga.kylemclean.minesweeper.game.Cell;
import ga.kylemclean.minesweeper.to.User;

public class GameScreen implements Screen, InputProcessor {
    final String CELL_RED = "cell_red";
    final String CELL_GREEN= "cell_green";
    final String CELL_BLUE = "cell_blue";
    final String CELL_YELLOW = "cell_yellow";
    final String CELL_EMPTY = "cell_empty";
    final String NAME_RED = "Red";
    final String NAME_GREEN= "Green";
    final String NAME_BLUE = "Blue";
    final String NAME_YELLOW = "Yellow";
    final String NAME_EMPTY = "empty";
    final int CODE_EMPTY = 0;
    final int CODE_GREEN = 1;
    final int CODE_BLUE = 2;
    final int CODE_YELLOW = 3;
    final int CODE_RED = 4;
    private Stage stage;
    private Skin skin;
    private Skin skinBtnMenu;
    private TextButton closeButton;
    Window window;
    private Table tablePosiciones;
    private Table tableMenu;
    private enum GameState {
        NOT_STARTED, PLAYING, PAUSED, WON, LOST
    }
    int siguienteColor = 1;
    private Minesweeper game;
    private SpriteBatch batch;
    private OrthographicCamera gameCamera, fixedCamera;
    private ShapeRenderer shapeRenderer;

    private TextureAtlas cellTextures, uiTextures;
    private BitmapFont font;

    private Vector3 gameCameraTargetPosition;
    private float gameCameraTargetZoom;

    private Vector3 touchPos, screenTouchDownPos;
    private boolean panningCamera;
    private float defaultZoom;

    private int cellSize = 40;
    private int buttonSize = 60;
    private int boardHeight;
    private int boardWidth;
    private Rectangle boardWorldRectangle, zoomRectangle;
    private int mines;
    private Cell[][] board;
    private Vector2 pressingCell;
    private Vector2 chordingCell;
    private int cellsFlagged;
    private int cellsOpened;

    private GameState gameState;
    private GameState gameStateBeforePause;
    private float gameTime;
    private float gameTimeCountDown;
    private Label outputLabel;
    Texture textureBtnMenu;
    InputMultiplexer inputMultiplexer;


    private Label firstLabel, secondLabel, thirdLabel, fourthLabel;
    private Label firstSquaresLabel, secondSquaresLabel, thirdSquaresLabel, fourthSquaresLabel;
    private Label firstPercLabel, secondPercLabel, thirdPercLabel, fourthPercLabel;
    private GlyphLayout minesLayout, timeLayout;
    private Vector2 minesDisplayPosition, timeDisplayPosition,btnMenuDisplayPosition;
    float btnMenux  = Gdx.graphics.getWidth()*0.95f;
    float btnMenuy  = Gdx.graphics.getHeight()*0.9f;

    /**
     * Initialize the GameScreen.
     *
     * @param game        A reference to the Game object.
     * @param boardWidth  The width of the board in cells.
     * @param boardHeight The height of the board in cells.
     * @param mines       The number of mines to be generated on the board.
     */

    /***
     * **
     * Nota caracteriztica del juego, para dar merito al mas rapido se actualiza el tablero de servidor tan pronto se  hagan tiras
     * de cuadros de 5 o 10
     */
    public GameScreen(Minesweeper game, int boardWidth, int boardHeight, int mines) {
        this.game = game;
        batch = this.game.batch;
        gameCamera = this.game.gameCamera;
        fixedCamera = this.game.fixedCamera;
        shapeRenderer = this.game.shapeRenderer;
        stage = new Stage();///pendiente pasar a un metodo setupUi como en el menuscreen
        skin = game.assets.get("ui/uiskin.json", Skin.class);
        //skinBtnMenu = game.assets.get("textures/ui/pack.atlas", Skin.class);


        // Get assets from game AssetManager.
        cellTextures = game.assets.get("textures/cells/pack.atlas", TextureAtlas.class);
        uiTextures = game.assets.get("textures/ui/pack.atlas", TextureAtlas.class);
        //btnTextures = game.assets.get("textures/ui/pack.atlas", TextureAtlas.class);
        font = game.assets.get("ui/arial-32.fnt", BitmapFont.class);

        gameCameraTargetPosition = new Vector3();
        ///Gdx.input.setInputProcessor(this);
        Gdx.input.setInputProcessor(new InputMultiplexer());
        inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        inputMultiplexer.addProcessor(this);
        //Gdx.input.setInputProcessor(stage);
        touchPos = new Vector3();
        screenTouchDownPos = new Vector3();

        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.mines = mines;

        board = new Cell[this.boardWidth][this.boardHeight];
        createCells(this.boardWidth, this.boardHeight);
        pressingCell = new Vector2(-1, -1);
        chordingCell = new Vector2(-1, -1);
        cellsFlagged = 0;
        cellsOpened = 0;

        boardWorldRectangle = new Rectangle(0, 0, boardWidth * cellSize, boardHeight * cellSize);
        zoomRectangle = new Rectangle(0, 0, boardWidth * cellSize, boardHeight * cellSize);
        // Set rectangle to 16:9 aspect ratio.
        if (zoomRectangle.getAspectRatio() >= (16F / 9F)) {
            zoomRectangle.height = (9F / 16F) * zoomRectangle.width;
        } else {
            zoomRectangle.width = (16F / 9F) * zoomRectangle.height;
        }
        // Pad rectangle.
        zoomRectangle.width += 16 * 9;
        zoomRectangle.height += 9 * 4;
        // Center rectangle on board.
        zoomRectangle.x = -((zoomRectangle.width - (boardWidth * cellSize / 2)) / 2);
        zoomRectangle.y = -((zoomRectangle.height - (boardHeight * cellSize / 2)) / 2);
        gameCameraTargetPosition.set(boardWidth * cellSize / 2, boardHeight * cellSize / 2, 0);
        // Have the camera snap to the target position at first
        gameCamera.position.set(gameCameraTargetPosition.cpy());
        //fixedCamera.position.set(gameCamera.position.cpy());
        // Zoom in at first
        gameCamera.zoom = 0.5f;
        gameCameraTargetZoom = zoomRectangle.width / 1280;
        defaultZoom = gameCameraTargetZoom;

        gameState = GameState.NOT_STARTED;
        gameTime = 0;
        gameTimeCountDown = 10;

        minesLayout = new GlyphLayout();
        timeLayout = new GlyphLayout();
        minesDisplayPosition = new Vector2(24, 720 - 24);
        timeDisplayPosition = new Vector2(1280 - 90, 720 - 24);
        btnMenuDisplayPosition = new Vector2(1280 - 80, 720-90);
        /******
         * zona de pruebas tests
         */
        //testSerializacionObjeto();
        //testTransformacionJson2Java();
        //testPintarTablero();
        createBotonMenu();
        if (  game.leaderboard != null ){
            game.leaderboard.consultarTopPlayers();
        }
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if( gameState == GameState.PLAYING){
                    respuestaComputer();
                    respuestaComputer();
                    respuestaComputer();
                }
            }
        }, 2000l, 1000l);
    }
    private void  createBotonMenu(){
        int row_height = Gdx.graphics.getWidth() / 12;
        int col_width = Gdx.graphics.getWidth() / 12;
        //InputMultiplexer inputMultiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
        if (!inputMultiplexer.getProcessors().contains(stage,false))
            inputMultiplexer.addProcessor(stage);
        /*ImageButton.ImageButtonStyle menuButtonStyle = new ImageButton.ImageButtonStyle();  //Instaciate
        menuButtonStyle.up = skinBtnMenu.getDrawable("btnMenu");  //Set image for not pressed button
        menuButtonStyle.down = skinBtnMenu.getDrawable("btnMenu");  //Set image for pressed
        menuButtonStyle.over = skinBtnMenu.getDrawable("btnMenu");  //set image for mouse over
        menuButtonStyle.pressedOffsetX = 1;
        menuButtonStyle.pressedOffsetY = -1;*/

        textureBtnMenu = new Texture(Gdx.files.internal("textures/celdas/btnMenu2.png"));
        //Texture myTexture = new Texture(cellTextures.findRegion("btnMenu").getTexture());
        TextureRegion myTextureRegion = new TextureRegion(textureBtnMenu);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        ImageButton buttonMenu = new ImageButton(myTexRegionDrawable);
        buttonMenu.setSize(col_width*0.6f,(float)(row_height*0.6));
        /*button3.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("switch_off.png"))));
        button3.getStyle().imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("switch_on.png"))));
        */
        buttonMenu.setPosition(btnMenux,btnMenuy);
        buttonMenu.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                //outputLabel.setText("Press a Button");
                Gdx.app.log("MyTag", "***** boton menu soltado ");
                mostrarMenu();
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //outputLabel.setText("Pressed Image Button");
                Gdx.app.log("MyTag", "***** boton menu down ");
                return true;
            }
        });
        buttonMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MyTag", "***** boton menu click ");
                mostrarMenu();
            }
        });
        stage.addActor(buttonMenu);
        //Gdx.input.setInputProcessor(stage);
    }

    /**
     * Create the cell objects.
     *
     * @param boardWidth  The width of the board.
     * @param boardHeight The height of the board.
     */
    private void createCells(int boardWidth, int boardHeight) {
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                board[x][y] = new Cell(
                        //cellTextures.findRegion("cell_normal_up"));
                        cellTextures.findRegion("cell_empty"));
            }
        }
    }

    /**
     * Fills the board with mines.
     * Mines will not be generated in a 3x3 space around the cell the user clicked.
     *
     * @param amount   The amount of mines to create on the board.
     * @param initialX The x position on the board of the first cell the user clicked.
     * @param initialY The y position on the board of the first cell the user clicked.
     */
    private void generateMines(int amount, int initialX, int initialY) {
        for (int m = 0; m < amount; ) {
            int randX = MathUtils.random(boardWidth - 1);
            int randY = MathUtils.random(boardHeight - 1);
            if (!board[randX][randY].isMine && !(
                    (randX >= initialX - 1 && randX <= initialX + 1) &&
                            (randY >= initialY - 1 && randY <= initialY + 1))) {
                // Set as a mine as long as it isn't already a mine and it is
                // not in a 3x3 space around the cell the user clicked.
                board[randX][randY].isMine = true;
                m++;
            }
        }
    }

    /**
     * Generates the labels for each cell based on their surrounding mines.
     */
    private void generateCellLabels() {
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                if (!board[x][y].isMine) {
                    int surroundingMines = 0;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (x + dx >= 0 && y + dy >= 0 &&
                                    x + dx < boardWidth && y + dy < boardHeight) {
                                if (board[x + dx][y + dy].isMine) {
                                    surroundingMines++;
                                }
                            }
                        }
                    }
                    board[x][y].surroundingMines = surroundingMines;
                }
            }
        }
    }

    /**
     * Open the cell at the specified location. If the cell has no surrounding mines,
     * open all surrounding cells as well.
     *
     * @param x The x-coordinate of the cell to open.
     * @param y The y-coordinate of the cell to open.
     */
    private void openCell(int x, int y) {
        if (!board[x][y].opened && !board[x][y].flagged) {
            board[x][y].opened = true;
            if (!board[x][y].isMine) {
                if (board[x][y].surroundingMines > 0) {
                    board[x][y].texture = cellTextures
                            .findRegion("cell" + board[x][y].surroundingMines);
                } else {
                    // There are no surrounding mines
                    board[x][y].texture = cellTextures
                            .findRegion("cell_empty");
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (x + dx >= 0 && y + dy >= 0 &&
                                    x + dx < boardWidth && y + dy < boardHeight) {
                                openCell(x + dx, y + dy);
                            }
                        }
                    }
                }
                cellsOpened++;
                if (cellsOpened == boardWidth * boardHeight - mines) {
                    winGame();
                }
            } else {
                board[x][y].texture = cellTextures.findRegion("cell_mine");
                loseGame();
            }
        }
    }

    /**
     * Flag or unflag the cell at the specified location.
     *
     * @param x The x-coordinate of the cell to (un)flag.
     * @param y The y-coordinate of the cell to (un)flag.
     */
    private void toggleFlagCell(int x, int y) {
        if (!board[x][y].opened) {
            if (!board[x][y].flagged) {
                board[x][y].flagged = true;
                board[x][y].texture = cellTextures.findRegion("cell_flag_up");
                cellsFlagged++;
            } else {
                board[x][y].flagged = false;
                board[x][y].texture = cellTextures.findRegion("cell_normal_up");
                cellsFlagged--;
            }
        }
    }

    /**
     * Chord (open all cells in 3x3 box around) a given cell.
     *
     * @param cellX The x-coordinate of the cell to chord.
     * @param cellY The y-coordinate of the cell to chord.
     */
    private void chordCell(int cellX, int cellY) {
        int surroundingFlags = 0;
        int surroundingMines = board[cellX][cellY].surroundingMines;
        for (int dy = -1; dy < 2; dy++) {
            for (int dx = -1; dx < 2; dx++) {
                if (!(dx == 0 && dy == 0)) { // Don't check the chording cell
                    // Make sure the cell we are checking is on the board
                    if (cellX + dx >= 0 && cellY + dy >= 0 &&
                        cellX + dx < boardWidth && cellY + dy < boardHeight) {
                        if (board[cellX + dx][cellY + dy].flagged) {
                            surroundingFlags++;
                        }
                    }
                }
            }
        }
        
        // If there are the right amount of flags, open the surrounding cells
        if (surroundingFlags == surroundingMines) {
            for (int dy = -1; dy < 2; dy++) {
                for (int dx = -1; dx < 2; dx++) {
                    if (!(dx == 0 && dy == 0)) { // Don't check the chording cell
                        // Make sure the cell we are checking is on the board
                        if (cellX + dx >= 0 && cellY + dy >= 0 &&
                                cellX + dx < boardWidth && cellY + dy < boardHeight) {
                            if (!board[cellX + dx][cellY + dy].flagged) {
                                openCell(cellX + dx, cellY + dy);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Win the game.
     */
    private void winGame() {
        gameState = GameState.WON;
    }

    /**
     * Lose the game.
     */
    private void loseGame() {
        gameState = GameState.LOST;
        // Show all mines on the board
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                if (board[x][y].isMine && !board[x][y].flagged) {
                    board[x][y].texture = cellTextures.findRegion("cell_mine");
                }
                if (!board[x][y].isMine && board[x][y].flagged) {
                    board[x][y].texture = cellTextures.findRegion("cell_flag_wrong");
                }
            }
        }
    }

    /**
     * Reset the game.
     */
    private void resetGame() {
        createCells(boardWidth, boardHeight);
        cellsFlagged = 0;
        cellsOpened = 0;
        gameState = GameState.NOT_STARTED;
        gameTime = 0;
        gameTimeCountDown = 10;
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameState == GameState.PLAYING) {
            gameTime += delta;
            gameTimeCountDown -=delta;
        }

        interpolateCamera(delta);

        gameCamera.update();
        fixedCamera.update();

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                batch.draw(board[x][y].texture, x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
        //batch.draw(cellTextures.findRegion("btnMenu2"),btnMenuDisplayPosition.x,btnMenuDisplayPosition.y,buttonSize,buttonSize);
        //batch.draw(cellTextures.findRegion("btnMenu2"),0,0, buttonSize, buttonSize);
        batch.end();

        // Draw rectangles behind the mines counter and timer
        Gdx.gl20.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(fixedCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.75f);
        shapeRenderer.rect(minesDisplayPosition.x - 8, minesDisplayPosition.y + 8,
                minesLayout.width + 8 * 2, -minesLayout.height - 8 * 2);
        shapeRenderer.rect(timeDisplayPosition.x + 8, timeDisplayPosition.y + 8,
                -timeLayout.width - 8 * 2, -timeLayout.height - 8 * 2);
        shapeRenderer.end();
        Gdx.gl20.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(fixedCamera.combined);
        batch.begin();
        // Draw mines remaining
        /*minesLayout.setText(font, ((mines - cellsFlagged) < 100 ? "0" : "") +
                ((mines - cellsFlagged) < 10 ? "0" : "") + (mines - cellsFlagged));
        font.draw(batch, minesLayout,
                minesDisplayPosition.x, minesDisplayPosition.y);*/
        // Draw time elapsed
        //timeLayout.setText(font, (int) gameTime / 60 + ":" + ((int) gameTime % 60 < 10 ? "0" : "") + (int) gameTime % 60);
        timeLayout.setText(font, (int) gameTimeCountDown / 60 + ":" + ((int) gameTimeCountDown % 60 < 10 ? "0" : "") + (int) gameTimeCountDown % 60);
        font.draw(batch, timeLayout,
        /* right aligned */timeDisplayPosition.x - timeLayout.width, timeDisplayPosition.y);
        int segAnterior = 0;
        /*if( (int)gameTime % 1 == 0 &&  (int)gameTime > segAnterior ){
            Gdx.app.log("MyTag", "***** gameTime "+gameTime);
            segAnterior = (int)gameTime;
            //respuestaComputer();
        }*/
        if( gameTimeCountDown <= 0 && gameState != GameState.WON){
            gameState = GameState.WON;
            verificarGanador();
        }
        // Draw title if game is over
        /*if (gameState == GameState.WON || gameState == GameState.LOST) {
            if (gameState == GameState.WON) {
                batch.draw(uiTextures.findRegion("win"),
                        1280 / 2 - uiTextures.findRegion("win").originalWidth / 2,
                        720 / 2 - uiTextures.findRegion("win").originalHeight / 2);
            } else if (gameState == GameState.LOST) {
                batch.draw(uiTextures.findRegion("lose"),
                        1280 / 2 - uiTextures.findRegion("lose").originalWidth / 2,
                        720 / 2 - uiTextures.findRegion("lose").originalHeight / 2);
            }
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    font.setColor(Color.BLACK);
                } else {
                    font.setColor(Color.WHITE);
                }
                font.draw(
                        batch, "Press SPACE to play again\nPress ESC to change settings",
                        1280 / 2 + i * -2, 120 + i * 2, 0, Align.center, false);
            }
        }*/

        batch.end();
        stage.act(delta);
        stage.draw();
    }
    /***ImageButton button3 = new ImageButton(mySkin);
     button3.setSize(col_width*4,(float)(row_height*2));
     button3.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("switch_off.png"))));
     button3.getStyle().imageDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("switch_on.png"))));
     button3.setPosition(col_width,Gdx.graphics.getHeight()-row_height*6);
     button3.addListener(new InputListener(){
    @Override
    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
    outputLabel.setText("Press a Button");
    }
    @Override
    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
    outputLabel.setText("Pressed Image Button");
    return true;
    }
    });
     stage.addActor(button3);
     * **/

    /**
     * Interpolate the camera's position and zoom to a target position and zoom.
     * (gameCameraTargetPosition and gameCameraTargetZoom)
     *
     * @param delta The change in time in seconds since the last frame.
     */
    private void interpolateCamera(float delta) {
        gameCamera.position.x += (gameCameraTargetPosition.x - gameCamera.position.x) * 10 * delta;
        gameCamera.position.y += (gameCameraTargetPosition.y - gameCamera.position.y) * 10 * delta;
        gameCamera.zoom += (gameCameraTargetZoom - gameCamera.zoom) * 10 * delta;
    }

    /**
     * Quit the game and return the player to the menu.
     */
    private void returnToMenu() {
        dispose();
        game.setScreen(new MenuScreen(game));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPos.set(screenX, screenY, 0);
        screenTouchDownPos = touchPos.cpy();
        gameCamera.unproject(touchPos);
        String colorParticipante = getColorParticipante();

        if (gameState == GameState.PLAYING || gameState == GameState.NOT_STARTED) {
            if (boardWorldRectangle.contains(touchPos.x, touchPos.y)) {
                int cellX = (int) touchPos.x / cellSize;
                int cellY = (int) touchPos.y / cellSize;

                // Make sure that the cell coordinates are on the board
                if ((cellX >= 0 && cellX <= boardWidth) && (cellY >= 0 && cellY <= boardHeight)) {
                    board[cellX][cellY].texture = cellTextures.findRegion(colorParticipante);
                    board[cellX][cellY].color = getCodigoColorParticipante();
                    gameState = GameState.PLAYING;
                }

                return true;
            }
        }
        if( gameState == GameState.WON ){
            return true;
        }
        return false;

    }

    public boolean touchDownOld(int screenX, int screenY, int pointer, int button) {
        touchPos.set(screenX, screenY, 0);
        screenTouchDownPos = touchPos.cpy();
        gameCamera.unproject(touchPos);

        if (gameState == GameState.PLAYING || gameState == GameState.NOT_STARTED) {
            if (boardWorldRectangle.contains(touchPos.x, touchPos.y)) {
                int cellX = (int) touchPos.x / cellSize;
                int cellY = (int) touchPos.y / cellSize;

                // Make sure that the cell coordinates are on the board
                if ((cellX >= 0 && cellX <= boardWidth) && (cellY >= 0 && cellY <= boardHeight)) {

                    if (!board[cellX][cellY].opened) {
                        // Cell is not yet open
                        pressingCell.set(cellX, cellY);
                        if (!board[cellX][cellY].flagged) {
                            board[cellX][cellY].texture = cellTextures.findRegion("cell_normal_down");
                        } else {
                            board[cellX][cellY].texture = cellTextures.findRegion("cell_flag_down");
                        }
                    } else {
                        // Cell is already open
                        chordingCell.set(cellX, cellY);
                    }
                }

                return true;
            }
        }
        return false;

    }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        boolean returnTrue = false;
        String colorParticipante = getColorParticipante();
        testSerializacionObjeto();
        // Set touchPos to world coordinates of touchUp position
        touchPos.set(screenX, screenY, 0);
        gameCamera.unproject(touchPos);

        if (gameState == GameState.PLAYING || gameState == GameState.NOT_STARTED) {
            // cellX and cellY represent the boards coordinates of the touched cell
            int cellX = (int) touchPos.x / cellSize;
            int cellY = (int) touchPos.y / cellSize;

            // Make sure the cell position is on the board
            if ((cellX >= 0 && cellX < boardWidth) && (cellY >= 0 && cellY < boardHeight)) {
                board[cellX][cellY].texture = cellTextures.findRegion(colorParticipante);
                board[cellX][cellY].color = getCodigoColorParticipante();
                // Pressing cell logic

                // Chording cell logic
            }

        }
        Rectangle textBtnMenuBounds=new Rectangle(btnMenux,btnMenuy,textureBtnMenu.getWidth(),textureBtnMenu.getHeight());
        // texture x is the x position of the texture
        if(textBtnMenuBounds.contains(touchPos.x,touchPos.y))
        {
            mostrarMenu();
        }
        pressingCell.set(-1, -1);
        chordingCell.set(-1, -1);
        panningCamera = false;
        //verificarGanador();
        //winGame();
        return returnTrue;
    }

    public boolean touchUpOld(int screenX, int screenY, int pointer, int button) {
        boolean returnTrue = false;

        // Set touchPos to world coordinates of touchUp position
        touchPos.set(screenX, screenY, 0);
        gameCamera.unproject(touchPos);

        if (gameState == GameState.PLAYING || gameState == GameState.NOT_STARTED) {
            // cellX and cellY represent the boards coordinates of the touched cell
            int cellX = (int) touchPos.x / cellSize;
            int cellY = (int) touchPos.y / cellSize;

            // Make sure the cell position is on the board
            if ((cellX >= 0 && cellX < boardWidth) && (cellY >= 0 && cellY < boardHeight)) {

                // Pressing cell logic
                if (pressingCell.x != -1 && pressingCell.y != -1) {
                    if (cellX == pressingCell.x && cellY == pressingCell.y && !panningCamera) {
                        if (button == 0) {
                            if (gameState == GameState.NOT_STARTED) {
                                generateMines(this.mines, cellX, cellY);
                                generateCellLabels();
                                gameState = GameState.PLAYING;
                            }
                            if (!board[cellX][cellY].flagged) {
                                openCell(cellX, cellY);
                            } else {
                                board[cellX][cellY].texture = cellTextures.findRegion("cell_flag_up");
                            }
                        } else if (button == 1) {
                            toggleFlagCell(cellX, cellY);
                        }
                        returnTrue = true;
                    } else {
                        // Dragged off the cell
                        if (pressingCell.x != -1 && pressingCell.y != -1) {
                            board[(int) pressingCell.x][(int) pressingCell.y].texture =
                                    !board[(int) pressingCell.x][(int) pressingCell.y].flagged ?
                                            cellTextures.findRegion("cell_normal_up") :
                                            cellTextures.findRegion("cell_flag_up");
                        }
                    }
                }

                // Chording cell logic
                if (chordingCell.x != -1 && chordingCell.y != -1) {
                    if (cellX == chordingCell.x && cellY == chordingCell.y && !panningCamera) {
                        chordCell(cellX, cellY);
                    } else {
                        // Dragged off cell
                    }
                }

            }

        }
        pressingCell.set(-1, -1);
        chordingCell.set(-1, -1);
        panningCamera = false;
        return returnTrue;
    }
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        boolean returnTrue = false;
        String colorParticipante = getColorParticipante();
        // Set touchPos to world coordinates of touchUp position
        touchPos.set(screenX, screenY, 0);
        gameCamera.unproject(touchPos);

        if (gameState == GameState.PLAYING || gameState == GameState.NOT_STARTED) {
            // cellX and cellY represent the boards coordinates of the touched cell
            int cellX = (int) touchPos.x / cellSize;
            int cellY = (int) touchPos.y / cellSize;

            // Make sure the cell position is on the board
            if ((cellX >= 0 && cellX < boardWidth) && (cellY >= 0 && cellY < boardHeight)) {
                board[cellX][cellY].texture = cellTextures.findRegion(colorParticipante);
                board[cellX][cellY].color = getCodigoColorParticipante();
                // Pressing cell logic

                // Chording cell logic
            }

        }
        pressingCell.set(-1, -1);
        chordingCell.set(-1, -1);
        panningCamera = false;
        return returnTrue;
    }
    public String getColorParticipanteAleatorio(){
        String cell_color = null;
        Random random = new Random();
        int color = random.nextInt(4);
        switch (color){
            case 0:cell_color = CELL_RED;break;
            case 1:cell_color = CELL_GREEN;break;
            case 2:cell_color = CELL_BLUE;break;
            case 3:cell_color = CELL_YELLOW;break;
        }
        return cell_color;
    }
    public String getColorParticipante(){
        //String cell_color = CELL_RED;
        return getColorByCode(getCodigoColorParticipante());
    }
    public int getCodigoColorParticipante(){
        return CODE_RED;
    }
    public Color getCodigoColorParaPintar(int codigoEnJuego){
        Color color= null;
        switch (codigoEnJuego){
            case CODE_EMPTY:color = Color.WHITE;break;
            case CODE_GREEN:color = Color.GREEN;break;
            case CODE_BLUE:color = Color.BLUE;break;
            case CODE_YELLOW:color = Color.YELLOW;break;
            case CODE_RED: color = Color.RED;break;
        }
        return color;
    }
    public String getMatrizServidorDummy(){
        String strBoardOld = " \"board\": [\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1,0,1,0,0],\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1,0,1,0,0]\n" +
                "]";
        String strBoard = "{\n" +
                "nombre: Elnombre\n" +
                "matrixList: null\n" +
                "matrixArray: [\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1],\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "[1,0,0,1,0,0,1,0,1,1,0,1,0,1,1,1]\n" +
                "]\n" +
                "arrayTest: [ 0, 2 ]\n" +
                "}";
                //strBoard = generateMatrix1();
        return strBoard;
    }
    public int[][] generateMatrix1(int ancho, int alto){
        Random random = new Random();
        //int color = random.nextInt(4);
        int [][] arrayInt = new int[ancho][alto];
        for(int i = 0; i < ancho;i++){
            for(int j = 0;j < alto;j++){
                arrayInt[i][j] = random.nextInt(4);;
            }
        }
        return  arrayInt;
    }
    public int[][] generateMatrix2(int ancho, int alto){
        ///en 10 segundos no me hago mas de 288 en la pantalla del computador
        /// entonces en 3 segundos serian alrededor de 100
        /// asi cada unidad de probabilidad serian 48 cuadros mas
        Random random = new Random();
        String strMatrix = "";
        int limite = 10;
        int probabilidad = 2; ///30x16 = 480
        int limiteCuadrosSegunPorcentaje = (probabilidad*((ancho-1)*(alto-1)))/limite;
        int color = getCodigoColorParticipante();
        while( color == getCodigoColorParticipante() || color == CODE_EMPTY){ /// para que no salga el mismo del particpante
           //color = random.nextInt(4)+1;////para que no salga 0 que seria blanco
            siguienteColor++;
            color = (siguienteColor)%(CODE_RED+1);
        }
        int [][] arrayInt = new int[ancho][alto];
        int contador = 0;
        for(int i = 0; i < ancho;i++){
            for(int j = 0;j < alto;j++){
                int porcentaje = random.nextInt(limite);
                if ( porcentaje < probabilidad && contador < limiteCuadrosSegunPorcentaje){
                    arrayInt[i][j] = color;
                    contador++;
                }
                strMatrix+=arrayInt[i][j] ;
            }
        }
        Gdx.app.log("MyTag", "***** limiteCuadros "+limiteCuadrosSegunPorcentaje+
                " contCuadros "+contador+" strMatrix "+strMatrix);
        return  arrayInt;
    }
    public void testSerializacionObjeto(){
        ArrayList<ArrayList<Integer>> listBoard= new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> listFila1 = new ArrayList<Integer>();
        listFila1.add(1); listFila1.add(2); listFila1.add(0);
        ArrayList<Integer> listFila2 = new ArrayList<Integer>();
        listFila2.add(1); listFila2.add(2); listFila2.add(0);
        listBoard.add(listFila1);listBoard.add(listFila2);

        int[][] a = new int[2][2];
        a[0][0] = 3;
        a[0][1] = 3;
        a[1][0] = 5;
        a[1][1] = 6;
        int [] arrTest = new int[2];
        arrTest[1] = 2;
        Board miBoard = new Board(listBoard);
        Board miBoard2 = new Board(a,arrTest,"Elnombre");
        //miBoard2.setMatrix(listBoard);
        //miBoard2.setMatrix2(a);
        //miBoard2.setArrayTest(arrTest);
        //miBoard2.setNombre("Elnombre");
        Json json = new Json();
        String text = json.toJson(miBoard2, Board.class);
        System.out.println(json.prettyPrint(text));
    }
    public Board crearBoardDummy(){
        int alto = 31;int ancho = 17;///30x16 = 480
        int [][] arrayInt = generateMatrix2(alto,ancho);/// color aleatorio y se trata de llenar el 40 por cientoe
        ////del tablero, despues se ajusta para que el porcentaje sea parametrizable y hacerlo parecer
        //// mas humano para cuando no hayan jugadores online
        Board miBoard = new Board(arrayInt);
        return miBoard;
    }
    public void testTransformacionJson2Java(){
        String strMatrixTest = getMatrizServidorDummy();
        transformarJson2JavaMatrix(strMatrixTest);
    }
    public Board transformarJson2JavaMatrix( String strBoard){
        Json json = new Json();
        Board miBoardTrans = json.fromJson(Board.class, strBoard);
        //Gdx.app.debug("MyTag", "*****"+miBoardTrans.getMatrix2()[0][0]);
        //Gdx.app.log("MyTag", "*****"+miBoardTrans.getMatrix2()[0][0]);
        return miBoardTrans;
    }
    public int getValorCoordenadaTablero(Board unBoard, int x , int y){
        //Gdx.app.log("MyTag", " ***** unBoard.getMatrix2().length "+unBoard.getMatrix2().length);
        //Gdx.app.log("MyTag", "***** unBoard.getMatrix2()[1].length "+unBoard.getMatrix2()[1].length);
        /*for( int i = 0;i <  unBoard.getMatrix2().length;i++ ){
            for( int j = 0;j < unBoard.getMatrix2()[i].length;j++){
                if ( i == x && j == y ){
                    return unBoard.getMatrix2()[i][j];
                }
            }
        }*/
        return unBoard.getMatrix2()[x][y];
    }
    public void testPintarTablero(){
        //String strMatrixTest = getMatrizServidorDummy();
        //Board unBoard = transformarJson2JavaMatrix(strMatrixTest);
        Board unBoard = crearBoardDummy();
        pintarTablero(unBoard);
    }
    public void respuestaComputer(){
        //String strMatrixTest = getMatrizServidorDummy();
        //Board unBoard = transformarJson2JavaMatrix(strMatrixTest);
        Board unBoard = crearBoardDummy();
        pintarTablero(unBoard);
    }
    public void pintarTablero(Board unBoard ){
        for (int y = 0; y < boardHeight; y++) {///16
            for (int x = 0; x < boardWidth; x++) {///30
                //Gdx.app.log("MyTag", "***** boardHeight "+boardHeight+" boardWidth "+boardWidth);
                if( getValorCoordenadaTablero(unBoard,x,y) != CODE_EMPTY ){
                    board[x][y].texture = cellTextures.findRegion
                            (getColorByCode(getValorCoordenadaTablero(unBoard,x,y)));
                    board[x][y].color = getValorCoordenadaTablero(unBoard,x,y);
                }             }
        }
    }
    public void verificarGanador(){
        int [] conteosXColor = new int [5];/// celda 0 para el color 0, 1 para el color 1 y asi
        int [] posiciones = new int[5];/// el rojo es 4 luego tamaño es 5
        Map mapPosiciones = new TreeMap<Integer,String>(Collections.<Integer>reverseOrder());

        int boardHeight = 16; int boardWidth = 30;
        for (int y = 0; y < boardHeight; y++) {
            for (int x = 0; x < boardWidth; x++) {
                for( int i =0;i<conteosXColor.length;i++ ){
                    if(  board[x][y].color == i ){
                        conteosXColor[i]++;
                    }
                }
                /*if ( board[x][y].color == CODE_EMPTY ){
                    conteosXColor[CODE_EMPTY]++;
                }*/
            }
        }
        for ( int i = 0; i < conteosXColor.length;i++){
            mapPosiciones.put(conteosXColor[i],getNameColorByCode(i));
        }
        Gdx.app.log("MyTag", "***** array conteoXColor [0]"+conteosXColor[0]
        +"-[1]"+conteosXColor[1]+"-[2]"+conteosXColor[2]+"-[3]"+conteosXColor[3]+"-[4]"+conteosXColor[4]);
        Gdx.app.log("MyTag", "***** posiciones ");
        Set keys = mapPosiciones.keySet();
        for (Iterator i = keys.iterator(); i.hasNext();) {
            Integer key = (Integer) i.next();
            String value = (String) mapPosiciones.get(key);
            System.out.println(key + " = " + value);
        }
        mostrarPopupResumenJuego(mapPosiciones);
    }
    public void mostrarPopupResumenJuego( Map mapPosiciones){
        window = new Window("", skin);
        tablePosiciones = new Table(skin);
        //window.setMoveable(false); //So the user can't move the window
        //window.add(new TextButton("Unpause", skin)); //Add a new text button that unpauses the game.
        Set keys = mapPosiciones.keySet();
        int contadorCuadros = 0;
        int numeroColumnas = 4;
        int limiteNombre = 10;
        List lstPlayers = null;
        Random rand = new Random();
        int primeroListaFicti = 0;
        int cont = 0;
        /*Label nameTitleLabel = new Label("Co", skin);
        Label cuadrosTitleLabel = new Label(Integer.toString(key), skin);
        Label percTitleLabel = new Label(Integer.toString(key), skin);
        nameLabel.setColor(getCodigoColorParaPintar(getCodigoColorByName(value)));
        cuadrosLabel.setColor(getCodigoColorParaPintar(getCodigoColorByName(value)));
        percLabel.setColor(getCodigoColorParaPintar(getCodigoColorByName(value)));
        tablePosiciones.add(nameLabel).width(100);
        tablePosiciones.add(cuadrosLabel).width(100);
        tablePosiciones.add(percLabel).width(100);*/
        for (Iterator i = keys.iterator(); i.hasNext();) {
            Integer key = (Integer) i.next();
            contadorCuadros = contadorCuadros + key;
        }
        Label winnerLabel = new Label( (String)(mapPosiciones.get(mapPosiciones.keySet().toArray()[0]))+" Won ", skin);
        tablePosiciones.add(winnerLabel).colspan(numeroColumnas);
        winnerLabel.setColor(getCodigoColorParaPintar(getCodigoColorByName((String) (mapPosiciones.get(mapPosiciones.keySet().toArray()[0])))));
        tablePosiciones.row();
        if (  game.leaderboard != null && game.leaderboard.checkConectionInternet() ){
            lstPlayers = game.leaderboard.getListPlayers();
            primeroListaFicti = rand.nextInt(lstPlayers.size()-5);
            Gdx.app.log("MyTag", "***** lstPlayers.size "+
                    lstPlayers.size()+" primero "+((User)lstPlayers.get(0)).getName() + " - "+((User)lstPlayers.get(0)).getIdUserGoogle());
        }
        for (Iterator i = keys.iterator(); i.hasNext();) {
            Integer key = (Integer) i.next();
            String value = (String) mapPosiciones.get(key);
            if( !value.equalsIgnoreCase(NAME_EMPTY) && key > 0){
                float pctge = ((float)key/contadorCuadros)*100;
                DecimalFormat df = new DecimalFormat("#.00");
                Label nameUserLabel;
                nameUserLabel = new Label("Guest", skin);
                Gdx.app.log("MyTag", "***** getColorParticipante "+getColorParticipante()+" game.nameAccount "+game.nameAccount);
                if( getNameColorByCode(getCodigoColorParticipante()) == value){
                    Gdx.app.log("MyTag", "***** getNameColorByCode(getCodigoColorParticipante()) "+getNameColorByCode(getCodigoColorParticipante()));
                    if( game.nameAccount != null){
                        nameUserLabel = new Label(game.nameAccount.substring(0,limiteNombre), skin);
                    } else {
                        nameUserLabel = new Label("Guest", skin);
                    }
                } else if ( lstPlayers != null && (lstPlayers.get(primeroListaFicti+cont)) != null ) {
                    int longitudNombre = ((User)(lstPlayers.get(primeroListaFicti+cont))).getIdUserGoogle().length();
                    limiteNombre = (limiteNombre > longitudNombre) ? longitudNombre:limiteNombre;
                    String namePlayerFicti = ((User)(lstPlayers.get(primeroListaFicti+cont))).getIdUserGoogle().substring(0,limiteNombre);
                    nameUserLabel = new Label(((User)(lstPlayers.get(primeroListaFicti+cont))).getIdUserGoogle().substring(0,limiteNombre), skin);
                } else {
                    nameUserLabel = new Label("Com"+cont, skin);
                }
                nameUserLabel.setColor(getCodigoColorParaPintar(getCodigoColorByName(value)));
                tablePosiciones.add(nameUserLabel).width(150);
                Label nameLabel = new Label(value, skin);
                Label cuadrosLabel = new Label(Integer.toString(key), skin);
                Label percLabel = new Label(df.format(pctge)+"%", skin);
                nameLabel.setColor(getCodigoColorParaPintar(getCodigoColorByName(value)));
                cuadrosLabel.setColor(getCodigoColorParaPintar(getCodigoColorByName(value)));
                percLabel.setColor(getCodigoColorParaPintar(getCodigoColorByName(value)));
                tablePosiciones.add(nameLabel).width(100);
                tablePosiciones.add(cuadrosLabel).width(100);
                tablePosiciones.add(percLabel).width(100);
                tablePosiciones.row();
                cont++;
            }
        }

        /*firstLabel = new Label("Red", skin);
        firstLabel.setColor(Color.RED);
        secondLabel = new Label("Red", skin);
        thirdLabel = new Label("Red", skin);
        fourthLabel = new Label("Red", skin);
        tablePosiciones.add(firstLabel);
        tablePosiciones.row();
        tablePosiciones.add(secondLabel);
        tablePosiciones.row();
        tablePosiciones.add(thirdLabel);
        tablePosiciones.row();
        tablePosiciones.add(fourthLabel);
        tablePosiciones.row();*/
        closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.setVisible(false);
            }
        });
        /*closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                window.setVisible(false);
            }
        });*/
        tablePosiciones.add(closeButton).colspan(numeroColumnas);
        //tablePosiciones.setFillParent(true);
        tablePosiciones.top();
        tablePosiciones.setDebug(true);
        window.add(tablePosiciones);
        window.top();
        window.setDebug(true);
        //window.add(closeButton);
        window.pack(); //Important! Correctly scales the window after adding new elements.
        float newWidth = 500, newHeight = 400;
        window.setBounds((Gdx.graphics.getWidth() - newWidth ) / 2,
                (Gdx.graphics.getHeight() - newHeight ) / 2, newWidth , newHeight ); //Center on screen.
        window.padTop(64);

        stage.addActor(window);
        stage.setDebugAll(false);
        //stage.set
        //Gdx.input.setInputProcessor(stage);

    }
    public void mostrarMenu(){
        window = new Window("", skin);
        tableMenu = new Table(skin);
        tableMenu.top();
        tableMenu.setDebug(true);
        closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.setVisible(false);
            }
        });
        /*closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                window.setVisible(false);
            }
        });*/
        tableMenu.add(closeButton).colspan(1);
        window.add(tableMenu);
        window.top();
        window.setDebug(true);
        //window.add(closeButton);

        window.pack(); //Important! Correctly scales the window after adding new elements.
        float newWidth = 500, newHeight = 400;
        window.setBounds((Gdx.graphics.getWidth() - newWidth ) / 2,
                (Gdx.graphics.getHeight() - newHeight ) / 2, newWidth , newHeight ); //Center on screen.
        window.padTop(64);

        stage.addActor(window);
        stage.setDebugAll(false);
        //stage.set
        Gdx.input.setInputProcessor(stage);
    }
    public String getColorByCode( int code){
        String cell_color = null;
        switch (code){
            case CODE_EMPTY:cell_color = CELL_EMPTY;break;
            case CODE_GREEN:cell_color = CELL_GREEN;break;
            case CODE_BLUE:cell_color = CELL_BLUE;break;
            case CODE_YELLOW:cell_color = CELL_YELLOW;break;
            case CODE_RED:cell_color = CELL_RED;break;
        }
        return cell_color;
    }
    public String getNameColorByCode( int code){
        String nameColor = null;
        switch (code){
            case CODE_EMPTY:nameColor = NAME_EMPTY;break;
            case CODE_GREEN:nameColor = NAME_GREEN;break;
            case CODE_BLUE:nameColor = NAME_BLUE;break;
            case CODE_YELLOW:nameColor = NAME_YELLOW;break;
            case CODE_RED:nameColor = NAME_RED;break;
        }
        return nameColor;
    }
    public int getCodigoColorByName( String name ){
        int codigoColor = 0;
        if (name.equalsIgnoreCase(NAME_EMPTY)) {
            codigoColor = CODE_EMPTY;
        } else if( name.equalsIgnoreCase(NAME_GREEN) ){
            codigoColor = CODE_GREEN;
        } else if( name.equalsIgnoreCase(NAME_BLUE) ){
            codigoColor = CODE_BLUE;
        } else if( name.equalsIgnoreCase(NAME_YELLOW) ){
            codigoColor = CODE_YELLOW;
        } else if( name.equalsIgnoreCase(NAME_RED) ){
            codigoColor = CODE_RED;
        }
        return codigoColor;
    }

    public boolean touchDraggedOld(int screenX, int screenY, int pointer) {
        if (gameCameraTargetZoom < defaultZoom - 0.1f && (
                Math.abs(screenTouchDownPos.x - screenX) >= 20 ||
                        Math.abs(screenTouchDownPos.y - screenY) >= 20 || panningCamera)) {
            panningCamera = true;
            gameCamera.translate((screenTouchDownPos.x - screenX) / 4f,
                    -(screenTouchDownPos.y - screenY) / 4f);
            gameCameraTargetPosition.set(gameCamera.position.cpy());
            gameCameraTargetPosition.x = MathUtils.clamp(gameCameraTargetPosition.x,
                    boardWorldRectangle.x, boardWorldRectangle.x + boardWorldRectangle.width);
            gameCameraTargetPosition.y = MathUtils.clamp(gameCameraTargetPosition.y,
                    boardWorldRectangle.y, boardWorldRectangle.y + boardWorldRectangle.height);
            screenTouchDownPos.set(screenX, screenY, 0);
            return true;
        }
        return false;
    }


    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.F2 ||
                (gameState == GameState.LOST || gameState == GameState.WON)
                        && keycode == Input.Keys.SPACE) {
            resetGame();
        }
        if (keycode == Input.Keys.ESCAPE) {
            returnToMenu();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        gameCameraTargetZoom += amount / 20F;
        gameCameraTargetZoom = MathUtils.clamp(gameCameraTargetZoom, 0.2f, defaultZoom);
        if (gameCameraTargetZoom >= defaultZoom - 0.1f) {
            gameCameraTargetPosition.set(boardWidth * cellSize / 2, boardHeight * cellSize / 2, 0);
        }
        return true;
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        gameStateBeforePause = gameState;
        gameState = GameState.PAUSED;
    }

    @Override
    public void resume() {
        gameState = gameStateBeforePause;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
