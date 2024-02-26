package si.um.feri.memomash.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.memomash.MemoMash;
import si.um.feri.memomash.assets.AssetDescriptors;
import si.um.feri.memomash.assets.RegionNames;
import si.um.feri.memomash.common.GameManager;
import si.um.feri.memomash.config.GameConfig;


public class MenuScreen extends ScreenAdapter {

    private final MemoMash game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private Sound buttonClick;
    private Music screenMusic;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    public MenuScreen(MemoMash game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        assetManager.load(AssetDescriptors.SOUND_CLICKED_ASSET_DESCRIPTOR);
        assetManager.finishLoading();

        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        buttonClick = assetManager.get(AssetDescriptors.SOUND_CLICKED_ASSET_DESCRIPTOR);
        screenMusic = assetManager.get(AssetDescriptors.SOUND_ALL_SCREENS_ASSET_DESCRIPTOR);

        if (GameManager.INSTANCE.isSoundEnabled()) {
            if(GameManager.INSTANCE.isBackgroundMusicEnabled()){
                screenMusic.setLooping(true);
                screenMusic.play();
            }
        }

        stage.addActor(createUi());
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 0f);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
        screenMusic.dispose();
    }

    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.INTRO_BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               game.setScreen(new GameScreen(game));
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
            }
        });

        TextButton leaderboardButton = new TextButton("Leaderboard", skin);
        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LeaderboardScreen(game));
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
            }
        });

        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
               game.setScreen(new SettingsScreen(game));
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
            }
        });

        TextButton quitButton = new TextButton("Quit", skin);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                }, 0.5f);
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
            }
        });

        Label.LabelStyle titleStyle = skin.get("title", Label.LabelStyle.class);
        Label titleLabel = new Label("MemoMash Game", titleStyle);


        Table buttonTable = new Table();
        Table title = new Table();
        buttonTable.defaults().padLeft(30).padRight(30);

        TextureRegion menuBackgroundRegion = gameplayAtlas.findRegion(RegionNames.MENU_BACKGROUND);
        buttonTable.setBackground(new TextureRegionDrawable(menuBackgroundRegion));
        buttonTable.add(titleLabel).padBottom(20).row();

        buttonTable.add(playButton).padBottom(15).expandX().fill().row();
        buttonTable.add(leaderboardButton).padBottom(15).fillX().row();
        buttonTable.add(settingsButton).padBottom(15).fillX().row();
        buttonTable.add(quitButton).fillX();

        table.add(title).top().colspan(2).row(); // Add the title table above the button table
        table.add(buttonTable).center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}