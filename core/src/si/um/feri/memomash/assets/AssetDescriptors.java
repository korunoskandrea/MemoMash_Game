package si.um.feri.memomash.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
public class AssetDescriptors {

    public static final AssetDescriptor<BitmapFont> UI_FONT =
            new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT, BitmapFont.class);

    public static final AssetDescriptor<Skin> UI_SKIN =
            new AssetDescriptor<Skin>(AssetPaths.UI_SKIN, Skin.class);

    public static final AssetDescriptor<TextureAtlas> GAMEPLAY =
            new AssetDescriptor<TextureAtlas>(AssetPaths.GAMEPLAY, TextureAtlas.class);

    public static final AssetDescriptor<Sound> SOUND_CORRECT_ASSET_DESCRIPTOR =
            new AssetDescriptor<Sound>(AssetPaths.SOUND_CORRECT_ASSET_DESCRIPTOR, Sound.class); // generated

//    public static final AssetDescriptor<Sound> SOUND_CORRECT_ASSET_DESCRIPTOR =
//            new AssetDescriptor<Sound>(AssetPaths.SOUND_CORRECT_ASSET_DESCRIPTOR, Sound.class);
    public static final AssetDescriptor<Sound> SOUND_FAIL_ASSET_DESCRIPTOR =
            new AssetDescriptor<Sound>(AssetPaths.SOUND_FAIL_ASSET_DESCRIPTOR, Sound.class);
    public static final AssetDescriptor<Sound> SOUND_LOSING_ASSET_DESCRIPTOR =
            new AssetDescriptor<Sound>(AssetPaths.SOUND_LOSING_ASSET_DESCRIPTOR, Sound.class);

    public static final AssetDescriptor<Sound> SOUND_PAINTING_WITH_BRUSH_ASSET_DESCRIPTOR =
            new AssetDescriptor<Sound>(AssetPaths.SOUND_PAINTING_WITH_BRUSH_ASSET_DESCRIPTOR, Sound.class);

    public static final AssetDescriptor<Sound> SOUND_WINNER_ASSET_DESCRIPTOR =
            new AssetDescriptor<Sound>(AssetPaths.SOUND_WINNER_ASSET_DESCRIPTOR, Sound.class);
    public static final AssetDescriptor<Sound> SOUND_CLICKED_ASSET_DESCRIPTOR =
            new AssetDescriptor<Sound>(AssetPaths.SOUND_CLICKED_ASSET_DESCRIPTOR, Sound.class);
    public static final AssetDescriptor<Music> SOUND_GAME_ASSET_DESCRIPTOR =
            new AssetDescriptor<Music>(AssetPaths.SOUND_GAME_ASSET_DESCRIPTOR, Music.class);
    public static final AssetDescriptor<Music> SOUND_ALL_SCREENS_ASSET_DESCRIPTOR =
            new AssetDescriptor<Music>(AssetPaths.SOUND_ALL_SCREENS_ASSET_DESCRIPTOR, Music.class);
    public static final AssetDescriptor<ParticleEffect> PARTICLE_CHECK =
            new AssetDescriptor<ParticleEffect>(AssetPaths.PARTICLE_CHECK, ParticleEffect.class);

    public static final AssetDescriptor<ParticleEffect> PARTICLE_FAIL =
            new AssetDescriptor<ParticleEffect>(AssetPaths.PARTICLE_FAIL, ParticleEffect.class);
    private AssetDescriptors() {
    }
}
