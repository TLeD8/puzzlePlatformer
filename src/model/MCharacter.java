/***************************************************************************************************
 * This class represents a playable character.
 */
package model;

import java.io.Serializable;
import java.util.ArrayList;
import javafx.geometry.Point2D;

public class MCharacter extends MPlatform implements Serializable {
	private static final long serialVersionUID = 769079227202952836L;
		//Attribute(s)--------------------------------------------------------------------------------------
		private boolean canJump = true;
		public transient Point2D velocity = new Point2D(0, 0);
		protected ArrayList<MPlatform> obstacles;
		private int spawnX, spawnY;
		private int tokenCount = 9;
		
		/***************************************************************************************************
		 * 
		 **************************************************************************************************/
		//Constructor(s)------------------------------------------------------------------------------------
		/***************************************************************************************************
		 * Instantiate a character at the specified location with the provided image information that
		 * interacts with the given obstacle list
		 * @param posX as int - The horizontal location initially of the character
		 * @param posY as int - The vertical location initially of the character
		 * @param filePath as String - The file location of the image to be used as the character
		 * @param obstacles as ArrayList of PPObjects - The obstacles that the character should be aware of
		 **************************************************************************************************/
		public MCharacter(int posX, int posY, String filePath, ArrayList<MPlatform> obstacles) {
			super(posX, posY, filePath);
			this.obstacles = obstacles;
		}
		
		//Mutator(s)----------------------------------------------------------------------------------------
		/***************************************************************************************************
		 * Set the obstacles list for this character to interact with
		 * @param obstacles as ArrayList of PPObjects - The obstacles that the character should interact with
		 **************************************************************************************************/
		public void setObstacles(ArrayList<MPlatform> obstacles) {
			this.obstacles = obstacles;
			this.obstacles.remove(this);	//This character shouldn't consider itself an obstacle
		}
		/***************************************************************************************************
		 * Sets the location where a character will spawn
		 * @param spawnX The position on the x axis
		 * @param spawnY The position on the y axis
		 ***************************************************************************************************/
		public void setSpawn(int spawnX, int spawnY) {
			this.spawnX = spawnX;
			this.spawnY = spawnY;
		}
		public void setTokenCount(int count) {
			this.tokenCount = count;
		}
		/***************************************************************************************************
		 * Spawns the character at the respawn location with full health.
		 ***************************************************************************************************/
		public void respawn() {
			this.setHealth(MWorld.DEFAULT_HEALTH);
			this.setPos(this.spawnX, this.spawnY);
		}
		
		//Accessor(s)---------------------------------------------------------------------------------------
		/***************************************************************************************************
		 * Returns the number of token this character has collected
		 * @return Number of tokens
		 ***************************************************************************************************/
		public int getTokenCount() {return this.tokenCount;}
		
		//Functional Method(s)------------------------------------------------------------------------------
		/***************************************************************************************************
		 * See if the character should accelerate due to gravity (up to a cap of 10)
		 **************************************************************************************************/
		public void checkVelocity() {
			//Ensure that the velocity is set properly
			if (this.velocity == null) {this.velocity = new Point2D(0, 0);}
			
			if (this.velocity.getY() < 10) {
				this.velocity = this.velocity.add(0, 1);
			}
		}
		
		/***************************************************************************************************
		 * Initiate a jump of this character
		 * @param value as int - The value of the y-velocity component to apply to the character
		 **************************************************************************************************/
		public void jump(int value) {
			//Ensure that the velocity is set properly
			if (this.velocity == null) {this.velocity = new Point2D(0, 0);}
			
			if (this.canJump) {
				this.velocity = this.velocity.add(0, Math.abs(value)*-1);
				this.canJump = false;
			}
		}
		/***************************************************************************************************
		 * Initiate horizontal movement of this character
		 * @param value as int - The magnitude and direction of the horizontal movement of the character
		 * @return The MToken object that is the token interacted with during the movement
		 **************************************************************************************************/
		public MToken moveX(int value) {
			boolean movingRight = value > 0;
			if (movingRight) {this.setAnimation(MWorld.BLOCK_SIZE);}
			else {this.setAnimation(0);}
			MToken retToken = null;
			
			for (int i=0; i<Math.abs(value); i++) {
				for (MPlatform obstacle : this.obstacles) {
					if (this.intersects(obstacle)) {
						//Apply the damage taken
						this.setHealth(this.getHealth() - obstacle.getDamage());
						if ((retToken = processToken (obstacle)) != null) {break;}
						if (movingRight) {
							if (this.x + this.width == obstacle.x) {return retToken;}
						} else {
							if (this.x == obstacle.x + obstacle.width) {return retToken;}
						}
					}
				}
				this.setPos(((movingRight ? this.x + 1 : this.x - 1)), this.y);
			}
			
			return retToken;
		}
		/***************************************************************************************************
		 * Initiate vertical movement of this character
		 * @param value as int - The magnitude and direction of the vertical movement of the character
		 * @return The MToken object that is the token interacted with during the movement
		 **************************************************************************************************/
		public MToken moveY(int value) {
			boolean movingDown = value > 0;
			MToken retToken = null;
			
			for (int i=0; i<Math.abs(value); i++) {
				for (MPlatform obstacle : this.obstacles) {
					if (this.intersects(obstacle)) {
						//Apply the damage taken
						this.setHealth(this.getHealth() - obstacle.getDamage());
						if ((retToken = processToken (obstacle)) != null) {break;}
						if (movingDown) {
							if (this.y + this.height == obstacle.y) {
								this.setPos(this.x, (this.y - 1));
								this.canJump = true;
								return retToken;
							}
						} else {
							if (this.y == obstacle.y + obstacle.height) {return retToken;}
						}
					}
				}
				this.setPos(this.x, ((movingDown ? this.y + 1: this.y - 1)));
			}
			return retToken;
		}
		/***************************************************************************************************
		 * Finds a specified token according to the parameter and processes / removes it.
		 * @param token Represents the token that is being searched for.
		 * @return The MToken that was processed.
		 **************************************************************************************************/
		private MToken processToken(MPlatform token) {
			MToken retToken = null;
			if (token.getClass() == MToken.class) {					//Token Interaction
				retToken = (MToken) token;
				if (retToken.isConsumable()) {
					this.obstacles.remove(token);
				}
			}
			return retToken;
		}
}
