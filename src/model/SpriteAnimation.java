/***************************************************************************************************
 * This class represents the animation for sprites.
 ***************************************************************************************************/
package model;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class SpriteAnimation extends Transition {

    private final ImageView imageView;	// ImageView which viewport is being changed
    private final int count;   // number of columns in sprite sheet
    
    private final int columns; // the amount of sprites in the sprite sheet you
	 						   // want to animate (will go to next row if needed)
    
    private final int offsetX; // top left x position
    private final int offsetY; // top left y position
    private final int width;   // width of rectangle
    private final int height;  // height off rectangle

    // ============ Constructor ============
    /***************************************************************************************************
     * Constructs the object for this class that contains the data for the animation.
     * @param imageView Represents the sprite image.
     * @param duration Represents the duration of one animation cycle.
     * @param count Represents the number of animations.
     * @param columns Represents columns of the animation.
     * @param offsetX Represents the offset on the x axis.
     * @param offsetY Represents the offset on the y axis.
     * @param width Represents the width of the animation.
     * @param height Represents the height of the animation.
     ***************************************************************************************************/
    public SpriteAnimation(
            ImageView imageView, 
            Duration duration, 
            int count,   int columns,
            int offsetX, int offsetY,
            int width,   int height) {
        this.imageView = imageView;
        this.count     = count;
        this.columns   = columns;
        this.offsetX   = offsetX;
        this.offsetY   = offsetY;
        this.width     = width;
        this.height    = height;
        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }
    /***************************************************************************************************
     * Defines the behavior of the animation.
     * @param k Specifies current position
     ***************************************************************************************************/
    public void interpolate(double k) {
        final int index = Math.min((int) Math.floor(k * count), count - 1);
        final int x = (index % columns) * width + offsetX;
        final int y = (index / columns) * height + offsetY;
        imageView.setViewport(new Rectangle2D(x, y, width, height));
    }
}
