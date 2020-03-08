/***************************************************************************************************
 * This class represents a platform inside a level.
 ***************************************************************************************************/
package model;

import java.io.FileInputStream;
import java.io.Serializable;
import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class MPlatform implements Serializable {
	private static final long serialVersionUID = 2981846869288805060L;
	
	//Attribute(s)--------------------------------------------------------------------------------------
	public int ID;
	protected transient ImageView img;
	protected transient Animation animation;
	private int damage = 0;
	private int health = MWorld.DEFAULT_HEALTH;
	private long timeDamage = System.currentTimeMillis();
	
	//Properties to help track collision
	protected String filePath;
	protected double x, y, height, width;
	/***************************************************************************************************
	 * 
	 **************************************************************************************************/
	//Constructor(s)------------------------------------------------------------------------------------
	/***************************************************************************************************
	 * Instantiate a object at the specified location with the provided image information
	 * @param posX as int - The horizontal location initially of the object
	 * @param posY as int - The vertical location initially of the object
	 * @param filePath as String - The file location of the image to be used as the object
	 **************************************************************************************************/
	public MPlatform(int posX, int posY, String filePath) {
		//Resize the image(s) to the default block size and preserve the ration of them
		this.filePath = filePath;
		this.x = posX;
		this.y = posY;
		this.width = this.height = MWorld.BLOCK_SIZE;
	}
	
	//Mutator(s)----------------------------------------------------------------------------------------
	/***************************************************************************************************
	 * Set the damage of the object
	 * @param damage as int - The damage this object should apply to and intersected damagable objects
	 **************************************************************************************************/
	public void setDamage(int damage) {this.damage = damage;}
	/***************************************************************************************************
	 * Set the health of the platform object
	 * @param health as int - The amount of damage that this object can take
	 **************************************************************************************************/
	public void setHealth(int health) {
		long timeDiff = (System.currentTimeMillis() - this.timeDamage) / (long) 100F;	//Seconds since last damage
		boolean isDamaged = ((timeDiff < 4) ? true : false);			//Only allow damage if it's been .4 seconds
		
		if ((health > this.health) || (!isDamaged)) {					//Damage is taken or health is being added
			if (health < this.health) {this.timeDamage = System.currentTimeMillis();}			//Damage is taken, record when it happened
			this.health = health;
		}
	}
	/***************************************************************************************************
	 * Set the position of this instances platform
	 * @param posX Represents the position on the x axis
	 * @param posY Represents the position on the y axis
	 ***************************************************************************************************/
	public void setPos(double posX, double posY) {
		this.x = posX;
		this.y = posY;
		try {
			getImage();
			this.img.setTranslateX(this.x);
			this.img.setTranslateY(this.y);
		} catch (NullPointerException e) {e.printStackTrace(System.err);}
	}
	/***************************************************************************************************
	 * Sets the height and width of the platform
	 * @param height Represents the height of the platform
	 * @param width Represents the width of the platform
	 ***************************************************************************************************/
	public void setDim(double height, double width) {
		this.height = height;
		this.width = width;
	}
	/***************************************************************************************************
	 * Sets the animation for the sprites
	 * @param offsetY Specifies where the sprite moves
	 ***************************************************************************************************/
	public void setAnimation(int offsetY) {
		this.animation = new SpriteAnimation(
				this.img,

                Duration.millis(1000),
                (int) img.getImage().getWidth() / 32, (int) img.getImage().getWidth() / 32,
                0, offsetY,
                32, 32
		);
		this.animation.setCycleCount(Animation.INDEFINITE);
		this.animation.play();
	}
	
	//Accessor(s)---------------------------------------------------------------------------------------
	/***************************************************************************************************
	 * Indicates whether this object is an enemy or not
	 * @return boolean - The status of this object as an enemy or not
	 **************************************************************************************************/
	public int getDamage() {return this.damage;}
	public int getHealth() {return this.health;}
	public double getX() {return this.x;}
	public double getY() {return this.y;}
	public ImageView getImage(){
		if (this.img == null) {
			try {
				this.img = new ImageView(new Image(new FileInputStream(this.filePath)));
				this.img.setViewport(new Rectangle2D(0, 0, width, height));
				this.img.setPreserveRatio(true);
				this.img.setFitWidth(this.width);
				this.img.setTranslateX(this.x);
				this.img.setTranslateY(this.y);
				setAnimation(0);
			} catch (Exception e) {}
		}
		//Set the properties of the view node
		return this.img;
	}
	/***************************************************************************************************
	 * Checks if the bounding boxes of this object and the specified object overlap.
	 * @param obj Represents the object that this object is being compared with.
	 * @return boolean - Represents whether the bounding boxes overlapped or not
	 ***************************************************************************************************/
	protected boolean intersects(MPlatform obj) {
		boolean intersects = false;
		checkVisable(obj);
		//Check if the bounding boxes of this object and the specified object overlap
		try {
			if (	//this = [   ], obj = (     )
			(																														//If the x line of this overlaps with the obj
				( (this.x + this.width) <= (obj.x + obj.width) && (this.x + this.width) >= (obj.x) )	//[ ( ] )   : this's right edge lies inside the range of obj.width
			||  ( (this.x) >= (obj.x) 			   			  && (this.x) <= (obj.x + obj.width)  )		//( [ ) ]	: this's left edge lies inside the range of obj.width
			)																														//and
		&&  (																														//the y line of this overlaps with the obj 
				( (this.y + this.height) <= (obj.y +obj.height) && (this.y + this.height) >= (obj.y))	//[ ( ] )	: this's top edge lies inside the range of obj.height
			||  ( (this.y) >= (obj.y) 						  && (this.y) <= (obj.y + obj.height)  )	//( [ ) ]	: this's bottom edge lies inside the range of obj.heigth
			)
				) 
			{intersects = true;}
		} catch (NullPointerException e) {}
		return intersects;
	}
	/***************************************************************************************************
	 * This method controls the "fog of war" for a platform and decides whether it is visible or not
	 * @param obj Represents the object that is being checked
	 ***************************************************************************************************/
	private void checkVisable(MPlatform obj) {
		if (true) {
			return;
		}
		obj.getImage().setVisible(false);
		int vD = 8; // view distance
		try {
			if (	//this = [   ], obj = (     )
			(																														//If the x line of this overlaps with the obj
				( (this.x + this.width) <= (obj.x + obj.width * vD) && (this.x + this.width * vD) >= (obj.x) )	//[ ( ] )   : this's right edge lies inside the range of obj.width
			||  ( (this.x) >= (obj.x) 			   			  && (this.x) <= (obj.x + obj.width)  )		//( [ ) ]	: this's left edge lies inside the range of obj.width
			)																														//and
		&&  (																														//the y line of this overlaps with the obj 
				( (this.y + this.height) <= (obj.y +obj.height * vD) && (this.y + this.height * vD) >= (obj.y))	//[ ( ] )	: this's top edge lies inside the range of obj.height
			||  ( (this.y) >= (obj.y) 						  && (this.y) <= (obj.y + obj.height)  )	//( [ ) ]	: this's bottom edge lies inside the range of obj.heigth
			)
				) 
			{obj.getImage().setVisible(true);}
		} catch (NullPointerException e) {}
		
	}
}
