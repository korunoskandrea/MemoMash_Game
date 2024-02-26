package si.um.feri.memomash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

import si.um.feri.memomash.CellActor;
import si.um.feri.memomash.Difficulty;
import si.um.feri.memomash.MemoMash;
import si.um.feri.memomash.assets.AssetDescriptors;
import si.um.feri.memomash.assets.RegionNames;
import si.um.feri.memomash.common.GameManager;
import si.um.feri.memomash.common.GameResult;
import si.um.feri.memomash.config.GameConfig;

public class GameScreen extends ScreenAdapter {
    private static final Logger log = new Logger(GameScreen.class.getSimpleName(), Logger.DEBUG);

    private final MemoMash game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Viewport hudViewport;

    private Stage gameplayStage;
    private Stage hudStage;

    private Sound buttonClick;
    private Sound rightClicked;
    private Sound wrongClicked;
    private Music gameMusic;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    private String cardsGrid = GameManager.INSTANCE.getSelectedGridSize();
    private Boolean sound = GameManager.INSTANCE.isSoundEnabled();
    private Boolean music = GameManager.INSTANCE.isBackgroundMusicEnabled();
    private Image infoImage;
    private Label resultLabel;
    private Label scoreLabel;

    private String playerName ;
    private float startTime;  // Variable to store the start time of the game
    private float elapsedTime;  // Variable to store the elapsed time during the game
    private Label timerLabel;  // Label to display the timer on the HUD
    private int score;

    private CellActor firstFlippedCard;
    private CellActor secondFlippedCard;
    private ArrayList<CellActor> cells = new ArrayList<>();


    public GameScreen(MemoMash game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }


    public void show() {
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        hudViewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);

        gameplayStage = new Stage(viewport, game.getBatch());
        hudStage = new Stage(hudViewport, game.getBatch());

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        buttonClick = assetManager.get(AssetDescriptors.SOUND_CLICKED_ASSET_DESCRIPTOR);
        gameMusic = assetManager.get(AssetDescriptors.SOUND_GAME_ASSET_DESCRIPTOR);
        rightClicked = assetManager.get(AssetDescriptors.SOUND_CORRECT_ASSET_DESCRIPTOR);
        wrongClicked = assetManager.get(AssetDescriptors.SOUND_LOSING_ASSET_DESCRIPTOR);

        if (GameManager.INSTANCE.isSoundEnabled()) {
           if(GameManager.INSTANCE.isBackgroundMusicEnabled()){
               gameMusic.setLooping(true);
               gameMusic.play();
           }
        }

        Actor grid = createGrid(GameManager.INSTANCE.getBoardRows(), GameManager.INSTANCE.getBoardColumns(), 10);
        gameplayStage.addActor(grid);
        hudStage.addActor(createBackButton());
        hudStage.addActor(createInfo());

        startTime = TimeUtils.nanoTime();
        elapsedTime = 0;
        score = 0;

        timerLabel = new Label("Time:", skin);
        timerLabel.setFontScale(2.5f);
        hudStage.addActor(timerLabel);

        scoreLabel = new Label("Score: " + score, skin);
        scoreLabel.setFontScale(2.5f);
        hudStage.addActor(scoreLabel);

        // Bravo label
        resultLabel = new Label("Bravo!", skin); // Updated label text
        resultLabel.setFontScale(5f);
        resultLabel.setAlignment(Align.center); // Align the label to the center
        resultLabel.setPosition(GameConfig.HUD_WIDTH / 2f - resultLabel.getWidth() / 2f, GameConfig.HUD_HEIGHT / 2f);
        hudStage.addActor(resultLabel);
        resultLabel.setVisible(false);

        // Reset Game button
        TextButton resetButton = new TextButton("Reset", skin);
        resetButton.setWidth(150);
        resetButton.setPosition( 20f, GameConfig.HUD_HEIGHT - resetButton.getHeight() -20f);
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
                resetGame();
            }
        });
        hudStage.addActor(resetButton);


        Gdx.input.setInputProcessor(new InputMultiplexer(gameplayStage, hudStage));

        if (GameManager.INSTANCE.getSelectedDifficulty() == Difficulty.EASY) {
            for (CellActor cell : cells) {
                cell.flip();
            }
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
                for (CellActor cell : cells) {
                    cell.flip();
                }
            }).start();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hudViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);

        // update
        gameplayStage.act(delta);
        hudStage.act(delta);

        if (score < 10) {
            // update
            gameplayStage.act(delta);
            hudStage.act(delta);

            // Update timer and display on the HUD
            elapsedTime = (TimeUtils.nanoTime() - startTime) / 1_000_000_000.0f; // Convert to seconds
            timerLabel.setText("Time: " + (int) elapsedTime + "s");
            timerLabel.pack();
            timerLabel.setPosition(
                    GameConfig.HUD_WIDTH / 2f - timerLabel.getWidth() / 2f,
                    GameConfig.HUD_HEIGHT - timerLabel.getHeight() - 20f
            );
        }

        scoreLabel.setText("Score: " + score);
        scoreLabel.pack();
        scoreLabel.setPosition(
                GameConfig.HUD_WIDTH / 2f - scoreLabel.getWidth() / 2f,
                GameConfig.HUD_HEIGHT - scoreLabel.getHeight() - 50f
        );
        // draw
        gameplayStage.draw();
        hudStage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        gameplayStage.dispose();
        hudStage.dispose();
        gameMusic.dispose();
    }

    private void resetGame() {
        resultLabel.setVisible(false);
        score = 0;
        elapsedTime = 0;
        startTime = TimeUtils.nanoTime();

        for (CellActor cell : cells) {
            if(!cell.isFlipped())
                cell.flip();
        }

        firstFlippedCard = null;
        secondFlippedCard = null;
    }
    // added for comparison
    public void onCellClicked(CellActor clickedCell) {
        if (firstFlippedCard == null) {
            firstFlippedCard = clickedCell;
        } else if (secondFlippedCard == null && firstFlippedCard != clickedCell) {
            secondFlippedCard = clickedCell;

            // Check if the names match
            if (firstFlippedCard.getPictureRegion().equals(secondFlippedCard.getPictureRegion())) {
                score++;
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    rightClicked.play();
                }
                if (score == 10) {
                    int playerOrder = 0;
                    resultLabel.setText(" Bravo!");
                    resultLabel.setVisible(true);
                    List<GameResult> results = GameManager.INSTANCE.getResults();
                    if(!results.isEmpty()){
                        playerOrder = results.get(0).getPlayerNumber();
                        for (int i = 0; i < results.size(); i++){
                            if(playerOrder < results.get(i).getPlayerNumber()){
                                playerOrder = results.get(i).getPlayerNumber();
                            }
                        }
                    }
                    playerOrder++;
                    GameManager.INSTANCE.addResult(playerOrder,playerName, elapsedTime);
                }
                firstFlippedCard.setMatched(true);
                secondFlippedCard.setMatched(true);

                // Remove matched cards from the stage after 0.5 seconds
                addActionToRemoveAfterDelay(firstFlippedCard, 1f);
                addActionToRemoveAfterDelay(secondFlippedCard, 1f);

                firstFlippedCard = null;
                secondFlippedCard = null;
            } else {
                // Names don't match, flip the cards back after a delay
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    wrongClicked.play();
                }
                float delay = 1f; // Adjust the delay for flipping back as needed
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        if (!firstFlippedCard.isMatched()) {
                            firstFlippedCard.flip();
                        }
                        if (!secondFlippedCard.isMatched()) {
                            secondFlippedCard.flip();
                        }
                        firstFlippedCard = null;
                        secondFlippedCard = null;
                    }
                }, delay);
            }
        }
    }

    private void addActionToRemoveAfterDelay(CellActor cellActor, float delay) {
        cellActor.addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.run(() -> cellActor.remove())
        ));
    }

    private Actor createGrid(int rows, int columns, final float cellSize) {
        final Table table = new Table();
        table.setDebug(false);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.SETTINGS_BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        final Table grid = new Table();
        grid.defaults().size(cellSize);
        grid.setDebug(false);

        final TextureRegion flippedRegion = gameplayAtlas.findRegion(RegionNames.CARD_OVERLAY);
        final TextureRegion picture1 = gameplayAtlas.findRegion(RegionNames.LANDSCAPE_AT_VETHEUIL);
        final TextureRegion picture2 = gameplayAtlas.findRegion(RegionNames.STARRY_NIGHT_OVER_THE_RHONE);
        final TextureRegion picture3 = gameplayAtlas.findRegion(RegionNames.STARRY_NIGHT);
        final TextureRegion picture4 = gameplayAtlas.findRegion(RegionNames.SWANS_REFLECTING_ELEPHANTS);
        final TextureRegion picture5 = gameplayAtlas.findRegion(RegionNames.GIRL_WITH_A_PEARL_EARRING);
        final TextureRegion picture6 = gameplayAtlas.findRegion(RegionNames.WHEAT_FIELD_WITH_CYPRESSES);
        final TextureRegion picture7 = gameplayAtlas.findRegion(RegionNames.ADELE_BLOCH_BAUER);
        final TextureRegion picture8 = gameplayAtlas.findRegion(RegionNames.THE_KISS);
        final TextureRegion picture9 = gameplayAtlas.findRegion(RegionNames.PORTRAIT_OF_A_YOUNG_GIRL);
        final TextureRegion picture10 = gameplayAtlas.findRegion(RegionNames.THE_TEMPTATION_OF_SAINT_ANTHONY);


        final Array<TextureRegion> pictures = new Array<>(
                new TextureRegion[]{picture1, picture2, picture3, picture4,
                        picture5, picture6, picture7, picture8, picture9, picture10}
        );

        pictures.shuffle();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                final CellActor cell = new CellActor(flippedRegion);
                int pictureIndex = (column + (row * columns) + 1) % pictures.size;
                cell.setPictureRegion(pictures.get(pictureIndex));
                cell.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        final CellActor clickedCell = (CellActor) event.getTarget();
                        clickedCell.flip();
                        onCellClicked(clickedCell);
                    }
                });
                grid.add(cell);
                cells.add(cell);
            }
            grid.row();
        }
        table.add(grid).row();
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }

    private Actor createBackButton() {
        final TextButton backButton = new TextButton("Back", skin);
        backButton.setWidth(100);
        backButton.setPosition(GameConfig.HUD_WIDTH / 2f - backButton.getWidth() / 2f, 20f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
                game.setScreen(new MenuScreen(game));
            }
        });
        return backButton;
    }

    private Actor createInfo() {
        final Table table = new Table();
        table.add(infoImage).size(30).row();
        table.center();
        table.pack();
        table.setPosition(
                GameConfig.HUD_WIDTH / 2f - table.getWidth() / 2f,
                GameConfig.HUD_HEIGHT - table.getHeight() - 20f
        );
        return table;
    }
}