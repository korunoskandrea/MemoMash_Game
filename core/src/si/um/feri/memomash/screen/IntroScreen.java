package si.um.feri.memomash.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import si.um.feri.memomash.MemoMash;
import si.um.feri.memomash.assets.AssetDescriptors;
import si.um.feri.memomash.assets.RegionNames;
import si.um.feri.memomash.common.GameManager;
import si.um.feri.memomash.config.GameConfig;

public class IntroScreen extends ScreenAdapter {

    public static final float INTRO_DURATION_IN_SEC = 5f;

    private final MemoMash game;
    private final AssetManager assetManager;

    private Viewport viewport;
    private TextureAtlas gameplayAtlas;

    private Sound introSound;

    private float duration = 0f;

    private Stage stage;

    public IntroScreen(MemoMash game) {
        this.game = game;
        assetManager = game.getAssetManager();
    }

    @Override
    public void show() {
        viewport = new FillViewport(GameConfig.HUD_WIDTH, GameConfig.HUD_HEIGHT);
        stage = new Stage(viewport, game.getBatch());

        // load assests
        assetManager.load(AssetDescriptors.GAMEPLAY);
        assetManager.load(AssetDescriptors.UI_SKIN);
        assetManager.load(AssetDescriptors.SOUND_PAINTING_WITH_BRUSH_ASSET_DESCRIPTOR);
        assetManager.load(AssetDescriptors.SOUND_WINNER_ASSET_DESCRIPTOR);
        assetManager.load(AssetDescriptors.SOUND_CORRECT_ASSET_DESCRIPTOR);
        assetManager.load(AssetDescriptors.SOUND_LOSING_ASSET_DESCRIPTOR);
        assetManager.load(AssetDescriptors.SOUND_FAIL_ASSET_DESCRIPTOR);
        assetManager.load(AssetDescriptors.SOUND_CLICKED_ASSET_DESCRIPTOR);
        assetManager.load(AssetDescriptors.SOUND_GAME_ASSET_DESCRIPTOR);
        assetManager.load(AssetDescriptors.SOUND_ALL_SCREENS_ASSET_DESCRIPTOR);
        assetManager.load(AssetDescriptors.PARTICLE_FAIL);
        assetManager.load(AssetDescriptors.PARTICLE_CHECK);
        assetManager.finishLoading(); // block until all assets are loaded

        gameplayAtlas = assetManager.get(AssetDescriptors.GAMEPLAY);
        introSound = assetManager.get(AssetDescriptors.SOUND_PAINTING_WITH_BRUSH_ASSET_DESCRIPTOR);

        Image background = new Image(gameplayAtlas.findRegion(RegionNames.INTRO_BACKGROUND));
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        stage.addActor(background);

        stage.addActor(createAnimationStarryNight());
        stage.addActor(createAnimationPearlGirl());
        stage.addActor(createPallete());
        stage.addActor(createAnimationBrush());
        if (GameManager.INSTANCE.isSoundEnabled()) {
            introSound.play();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(65 / 255f, 159 / 255f, 221 / 255f, 0f);

        duration += delta;

        // go to MenuScreen after the INTRO_DURATION_IN_SEC seconds
        if (duration > INTRO_DURATION_IN_SEC) {
            game.setScreen(new MenuScreen(game));
        }

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
    }

    private Actor createPallete() {
        final Image pallete = new Image(gameplayAtlas.findRegion(RegionNames.PALLETE));

        // Initial position to the right upper corner
        pallete.setPosition(viewport.getWorldWidth(), viewport.getWorldHeight());

        pallete.addAction(
                Actions.sequence(
                        Actions.moveTo(viewport.getWorldWidth() / 2f - pallete.getWidth() / 2f,
                                viewport.getWorldHeight() / 2f - pallete.getHeight() / 2f, 1), // Move to the center
                        Actions.fadeOut(1f)
                )
        );
        return pallete;
    }

    private Actor createAnimationBrush() {
        Image brush = new Image(gameplayAtlas.findRegion(RegionNames.BRUSH));

        // set positions x, y to center the image to the center of the window BRUSH
        float posXbrush = (viewport.getWorldWidth() / 2f) - brush.getWidth() / 2f;
        float posYbrush = (viewport.getWorldHeight() / 2f) - brush.getHeight() / 2f;

        brush.setOrigin(Align.center);
        brush.addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.fadeIn(1f),
                                Actions.moveBy(posXbrush, posYbrush, 1f)
                        ),
                        Actions.fadeOut(1f),
                        Actions.removeActor()
                )
        );

        return brush;
    }

    private Actor createAnimationStarryNight() {
        Image starrynight = new Image(gameplayAtlas.findRegion(RegionNames.STARRY_NIGHT));

        // Set initial position to the bottom-right corner
        starrynight.setPosition(
                viewport.getWorldWidth() - starrynight.getWidth(),
                0
        );

        starrynight.setOrigin(Align.center);
        starrynight.addAction(
                Actions.sequence(
                        Actions.rotateBy(360, 1.5f),
                        Actions.fadeOut(1f),
                        Actions.removeActor()
                )
        );
        return starrynight;
    }

    private Actor createAnimationPearlGirl() {
        Image pearlGirl = new Image(gameplayAtlas.findRegion(RegionNames.GIRL_WITH_A_PEARL_EARRING));

        // Set initial position to the left upper corner
        pearlGirl.setPosition(
                0,
                viewport.getWorldHeight() - pearlGirl.getHeight()
        );

        pearlGirl.setOrigin(Align.center);
        pearlGirl.addAction(
                Actions.sequence(
                        Actions.rotateBy(360, 1.5f),
                        Actions.fadeOut(1f),
                        Actions.removeActor()
                )
        );
        return pearlGirl;
    }



}
