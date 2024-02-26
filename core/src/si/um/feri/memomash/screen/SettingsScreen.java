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
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.memomash.Difficulty;
import si.um.feri.memomash.MemoMash;
import si.um.feri.memomash.assets.AssetDescriptors;
import si.um.feri.memomash.assets.RegionNames;
import si.um.feri.memomash.common.GameManager;
import si.um.feri.memomash.config.GameConfig;

public class SettingsScreen extends ScreenAdapter {

    private final MemoMash game;
    private AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private Sound buttonClick;
    private Music screenMusic;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    private CheckBox soundCheckBox;
    private CheckBox musicCheckBox;
    private SelectBox<String> gridSizeSelectBox;
    private SelectBox<String> levelSelectBox;
    private final String[] gridSizes = {"4x5", "5x4"};

    public SettingsScreen(MemoMash game) {
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
            if (GameManager.INSTANCE.isBackgroundMusicEnabled()) {
                screenMusic.setLooping(true);
                screenMusic.play();
            }
        }

        stage.addActor(createUi());
        Gdx.input.setInputProcessor(stage);
        loadPreferences();
    }

    private void loadPreferences() {
        soundCheckBox.setChecked(!GameManager.INSTANCE.isSoundEnabled());
        musicCheckBox.setChecked(!GameManager.INSTANCE.isBackgroundMusicEnabled());
        gridSizeSelectBox.setSelected(GameManager.INSTANCE.getSelectedGridSize());
        levelSelectBox.setSelected(GameManager.INSTANCE.getSelectedDifficulty().toString());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);

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

        soundCheckBox = new CheckBox("Sound off", skin);
        soundCheckBox.getLabel().setFontScale(0.8f);
        musicCheckBox = new CheckBox("Music off", skin);
        musicCheckBox.getLabel().setFontScale(0.8f);
        gridSizeSelectBox = new SelectBox<>(skin);
        gridSizeSelectBox.setItems(gridSizes);
        gridSizeSelectBox.setSelectedIndex(0); // default
        levelSelectBox = new SelectBox<String>(skin);
        levelSelectBox.setItems(Difficulty.getNames());
        levelSelectBox.setSelected(GameManager.INSTANCE.getSelectedDifficulty().toString());

        loadPreferences(); // initializes the checkboxes based on the saved preferences

        soundCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameManager.INSTANCE.setSoundDisabled(soundCheckBox.isChecked());
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
            }
        });

        musicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameManager.INSTANCE.setMusicDisabled(musicCheckBox.isChecked());
                if (GameManager.INSTANCE.isBackgroundMusicEnabled()) {
                    buttonClick.play();
                }
            }
        });

        gridSizeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                adaptGridSize(gridSizeSelectBox.getSelected());
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
            }
        });

        levelSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setLevel(Difficulty.fromName(levelSelectBox.getSelected()));
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
            }
        });

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
                if (GameManager.INSTANCE.isSoundEnabled()) {
                    buttonClick.play();
                }
            }
        });

        Table contentTable = new Table(skin);

        TextureRegion settingsBackground = gameplayAtlas.findRegion(RegionNames.SETTINGS_BACKGROUND);
        contentTable.setBackground(new TextureRegionDrawable(settingsBackground));

        Label.LabelStyle titleStyle = skin.get("title", Label.LabelStyle.class);
        Label titleLabel = new Label("Settings", titleStyle);
        titleLabel.setFontScale(1);

        Label.LabelStyle narrationStyle = skin.get("big", Label.LabelStyle.class);
        Label setSettingsLabel = new Label("Set your settings:", narrationStyle);

        soundCheckBox.getLabel().setFontScale(0.6f);
        musicCheckBox.getLabel().setFontScale(0.6f);

        Label.LabelStyle bigStyle = skin.get("big", Label.LabelStyle.class);
        Label setGridLabel = new Label("Select grid size:", bigStyle);

        Label setLevelLabel = new Label("Select level:", bigStyle);

        contentTable.add(titleLabel).padBottom(50).colspan(2).row();
        contentTable.add(setSettingsLabel).colspan(2).row();
        contentTable.row();
        contentTable.add(soundCheckBox).colspan(2).row();
        contentTable.row();
        contentTable.add(musicCheckBox).colspan(2).row();
        contentTable.add(setGridLabel).padTop(5).row();
        contentTable.row();
        contentTable.add(gridSizeSelectBox).padTop(5).row();
        contentTable.add(setLevelLabel).padTop(8).row();
        contentTable.row();
        contentTable.add(levelSelectBox).padTop(5).row();
        contentTable.row();
        contentTable.add(backButton).center().row();

        table.add(contentTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }

    private void adaptGridSize(String selectedSize) {
        String[] sizeParts = selectedSize.split("x");
        int columns = Integer.parseInt(sizeParts[0]);
        int rows = Integer.parseInt(sizeParts[1]);
        GameManager.INSTANCE.setSelectedGridSize(rows, columns);
    }

    private void setLevel(Difficulty difficulty) {
        GameManager.INSTANCE.setDifficulty(difficulty);
    }
}
