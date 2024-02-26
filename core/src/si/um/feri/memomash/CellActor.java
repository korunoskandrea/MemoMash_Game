package si.um.feri.memomash;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.Collections;

public class CellActor extends Image {

    private CellState state;
    private TextureRegion flippedRegion;
    private TextureRegion pictureRegion;

    private boolean matched;

    public CellActor(TextureRegion flippedRegion) {
        super(new TextureRegionDrawable(flippedRegion)); // Set the initial region using the constructor of the superclass
        state = CellState.FLIPPED;
        matched = false;
        this.flippedRegion = flippedRegion;
    }

    public void flip() {
        state = (state == CellState.FLIPPED) ? CellState.PICTURE : CellState.FLIPPED;
        System.out.println(state);
        System.out.println(flippedRegion);
        System.out.println(pictureRegion);
        setDrawable(new TextureRegionDrawable((state == CellState.FLIPPED) ? flippedRegion : pictureRegion));
        addAnimation();
    }
    public void setState(CellState state) {
        this.state = state;
    }

    public void setDrawable(TextureRegion region) {
        super.setDrawable(new TextureRegionDrawable(region));
        addAnimation(); // play animation when region changed
    }

    public boolean isFlipped() {
        return state == CellState.FLIPPED;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isMatched() {
        return matched;
    }

    public TextureRegion getFlippedRegion() {
        return flippedRegion;
    }

    public void setPictureRegion(TextureRegion pictureRegion) {
        this.pictureRegion = pictureRegion;
    }

    public TextureRegion getPictureRegion() {
        return pictureRegion;
    }

    private void addAnimation() {
        setOrigin(Align.center);
        addAction(
                Actions.sequence(
                        Actions.parallel(
                                Actions.rotateBy(720, 0.25f),
                                Actions.scaleTo(0, 0, 0.25f)
                        ),
                        Actions.scaleTo(1, 1, 0.25f)
                )
        );
    }
}