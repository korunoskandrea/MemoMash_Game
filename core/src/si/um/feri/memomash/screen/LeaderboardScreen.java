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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import si.um.feri.memomash.Difficulty;
import si.um.feri.memomash.MemoMash;
import si.um.feri.memomash.assets.AssetDescriptors;
import si.um.feri.memomash.assets.RegionNames;
import si.um.feri.memomash.common.GameManager;
import si.um.feri.memomash.common.GameResult;
import si.um.feri.memomash.config.GameConfig;

public class LeaderboardScreen extends ScreenAdapter {
    private final MemoMash game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private Stage stage;

    private Sound buttonClick;
    private Music screenMusic;

    private Skin skin;
    private TextureAtlas gameplayAtlas;

    public LeaderboardScreen(MemoMash game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FitViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

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

    private Dialog createNameChangeDialog(int playerNumber) {
        Dialog dialog = new Dialog("", skin);

        Label nameLabel = new Label("Enter your new name:", skin);
        TextField nameField = new TextField("", skin);
        nameField.setName("nameField");

        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String newName = nameField.getText();
                if (!newName.isEmpty()) {
                    GameManager.INSTANCE.changePlayerName(newName, playerNumber);
                }
                dialog.hide();
            }
        });

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.getContentTable().add(nameLabel).padBottom(10).row();
        dialog.getContentTable().add(nameField).padBottom(20).row();
        dialog.getButtonTable().add(confirmButton).padRight(10);
        dialog.getButtonTable().add(cancelButton);

        return dialog;
    }


    private Actor createUi() {
        Table table = new Table();
        table.defaults().pad(20);

        TextureRegion backgroundRegion = gameplayAtlas.findRegion(RegionNames.INTRO_BACKGROUND);
        table.setBackground(new TextureRegionDrawable(backgroundRegion));

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

        TextureRegion leaderboardBackground = gameplayAtlas.findRegion(RegionNames.SETTINGS_BACKGROUND);
        contentTable.setBackground(new TextureRegionDrawable(leaderboardBackground));

        Label.LabelStyle titleStyle = skin.get("title", Label.LabelStyle.class);
        Label titleLabel = new Label("Leaderboard", titleStyle);

        // Retrieve and display leaderboard results
        List<GameResult> results = GameManager.INSTANCE.getResults();

        Difficulty difficultyOrder;

        Collections.sort(results, new Comparator<GameResult>() {
            @Override
            public int compare(GameResult result1, GameResult result2) {
                // Compare based on difficulty first
                int difficultyComparison = Integer.compare(
                        result1.getDifficulty().getValue(), result2.getDifficulty().getValue());

                if (difficultyComparison != 0) {
                    return difficultyComparison;
                }

                // If difficulty is the same, compare based on playing time
                BigDecimal roundedResult1 = BigDecimal.valueOf(result1.getTime()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal roundedResult2 = BigDecimal.valueOf(result2.getTime()).setScale(2, RoundingMode.HALF_UP);

                return roundedResult1.compareTo(roundedResult2);
            }
        });

        Label playersLabel = new Label("    Players     ", skin);
        playersLabel.setFontScale(1.7f);

        Label timeLabel = new Label("   Time    ", skin);
        timeLabel.setFontScale(1.7f);

        Label difficulttyLabel = new Label("    Difficultly     ", skin);
        difficulttyLabel.setFontScale(1.7f);

        contentTable.add(titleLabel).padBottom(50).colspan(3).center().row();
        contentTable.add(playersLabel).center();
        contentTable.add(timeLabel).center();
        contentTable.add(difficulttyLabel).center().row();
        contentTable.row();

        // Display sorted results
        for (GameResult result : results) {
            Label nameLabel;
            int playerNumber = result.getPlayerNumber();
            if (result.getPlayerName() == null) {
                nameLabel = new Label(String.valueOf(result.getPlayerNumber()), skin);
                nameLabel.setFontScale(1.5f);
                nameLabel.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Dialog nameChangeDialog = createNameChangeDialog(playerNumber);
                        nameChangeDialog.show(stage);
                        if (GameManager.INSTANCE.isSoundEnabled()) {
                            buttonClick.play();
                        }
                    }
                });
            } else {
                nameLabel = new Label(String.valueOf(result.getPlayerName()), skin);
                nameLabel.setFontScale(1.5f);
            }

            contentTable.add(nameLabel).center();
            contentTable.add(new Label(String.format("%.2f", result.getTime()), skin)).center();
            contentTable.add(new Label(result.getDifficulty().toString(), skin)).center().row();
        }

        contentTable.add(backButton).colspan(3).center().row();

        table.add(contentTable);
        table.center();
        table.setFillParent(true);
        table.pack();

        return table;
    }
}
