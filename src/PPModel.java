/*******************************************************************************************************
 * @author Aaron J. Trevino
 * 
 * Model structure for the puzzle platformer JavaFX application
 ******************************************************************************************************/
import java.io.Serializable;
import java.util.HashMap;
import java.util.Observable;
import model.*;

public class PPModel extends Observable implements Serializable {
	private static final long serialVersionUID = -7831455086538942814L;
	//Attribute(s)--------------------------------------------------------------------------------------
	private MWorld world;
	private HashMap<Integer, Integer> tokenCount = new HashMap<Integer, Integer>();
	
	//Mutator(s)----------------------------------------------------------------------------------------
	/***************************************************************************************************
	 * Assigns a MWorld object to this instance.
	 * @param world Represents the world object that is being saved.
	 ***************************************************************************************************/
	public void setWorld(MWorld world) {
		this.world = world;
		this.tokenCount.put(0, 9);
	}
	/***************************************************************************************************
	 * Calls the jump method in the character class for the character specified
	 * @param ID Specifies the character
	 * @param value Height of the jump
	 ***************************************************************************************************/
	public void jumpCharacter(int ID, int value) {
		MCharacter character = getObject(ID);
		character.jump(value);
	}
	/***************************************************************************************************
	 * Calls methods in the character class to move the character on the x axis
	 * @param ID Specifies the character
	 * @param value How far the character is moving
	 ***************************************************************************************************/
	public void moveCharacter(int ID, int value) {
		MCharacter character = getObject(ID);
		double x = character.getX();
		MToken temp = character.moveX(value);
		
		//If the character moved, notify the view
		if (x != character.getX()) {init(character);}
		//If the player hit a token, notify the view
		tokenCheck(temp, character);
	}
	/***************************************************************************************************
	 * Checks the velocity of the character whether it moved vertically or fell off the map. Notifies
	 * the view if the character moved, fell off, or hit a token
	 * @param ID Specifies the character
	 ***************************************************************************************************/
	public void checkVelocity(int ID) {
		MCharacter character = getObject(ID);
		getObject(0).setTokenCount(this.tokenCount.get(0));
		double y = character.getY();
		character.checkVelocity();
		MToken temp = character.moveY((int) character.velocity.getY());
		
		//Death condition - fell out of the map
		if (character.getY() > this.world.getCurrLevel().height || character.getY() < MWorld.BLOCK_SIZE) {
			character.setHealth(0);
		}
		if (character.getHealth() == 0) {
			this.tokenCount.put(character.ID, this.tokenCount.get(character.ID)-3);
			character.setTokenCount(this.tokenCount.get(character.ID));
			respawn(character.ID);
		}
		
		//If the character moved, notify the view
		if (y != character.getY()) {init(character);}
		//If the player hit a token, notify the view
		tokenCheck(temp, character);
	}
	/***************************************************************************************************
	 * Checks parts of the level for how many tokens that have been collected and assigns the next world
	 * if enough tokens have been collected to achieve the win condition
	 * @param temp The token that is being checked whether it has been consumed or not
	 ***************************************************************************************************/
	private void tokenCheck(MToken temp, MCharacter character) {
		if (temp != null) {
			if (temp.getGroupID() == 1) {
				MLevel level = null;
				if (temp.getY() <= (PP.HEIGHT/4)) {			//First upper quarter of the level - North
					level = this.world.getCurrLevel().getNorth();
				}
				else if (temp.getY() >= 3*(PP.HEIGHT/4)) {	//Last bottom quarter of the level - South
					level = this.world.getCurrLevel().getSouth();
				}
				else if (temp.getX() > (PP.WIDTH/2)) {		//First half of the level - West
					level = this.world.getCurrLevel().getWest();
				}
				else {										//Last half of the level - East
					level = this.world.getCurrLevel().getEast();
				}
				//If there isn't a level in that direction, break the gate
				if (level == null) {temp.stateChange();}
				else {
					this.world.setCurrLevel(level);
					init(false);
					return;
				}
			}
			else if (temp.getGroupID() == 0) {
				this.tokenCount.put(character.ID, this.tokenCount.get(character.ID)+1);
				character.setTokenCount(this.tokenCount.get(character.ID));
			}
			//Win Condition
			else if (temp.getGroupID() == -1) {
				if (this.world.getNextWorld().getCurrLevel() != null) {
					this.world = this.world.getNextWorld();
					}
				init(true);
				return;
			}
			
			if (temp.isConsumable()) {this.removeObject(temp.ID);;}
			init(temp);
		}
	}
	/***************************************************************************************************
	 * Calls the respawn method in the character class to respawn the character at the levels original spawn point
	 * @param ID Specifies the character that is being respawned
	 ***************************************************************************************************/
	public void respawn(int ID) {
		MCharacter character = getObject(ID);
		
		character.respawn();
	}
	/***************************************************************************************************
	 * Removes an object from the level according to the ID specified
	 * @param ID Specifies the object that is being removed
	 ***************************************************************************************************/
	public void removeObject(int ID) {
		this.world.getCurrLevel().removeObject(ID); 
	}
	
	//Accessor(s)---------------------------------------------------------------------------------------
	/***************************************************************************************************
	 * Returns a MWorld object
	 * @return Mworld object that is returned
	 ***************************************************************************************************/
	public MWorld getWorld() {
		return this.world;
	}
	/***************************************************************************************************
	 * Returns a Mcharacter object that represents a playable character
	 * @param ID as int - The ID of the character being retrieved
	 * @return Mcharacter object that is returned
	 ***************************************************************************************************/
	public MCharacter getObject(int ID) {
		MCharacter character = this.world.getCurrLevel().getPlayers().get(ID);
		if (character.equals(null)) {character = this.world.getCurrLevel().getEnemies().get(ID);}
		return character;
	}
	
	//Functional Method(s)------------------------------------------------------------------------------
	/***************************************************************************************************
	 * Refreshes the display for all objects. Notifies the view of the data to be refreshed.
	 ***************************************************************************************************/
	public void refreshDisplay() {
		//Notify the view of the background of the level
		init(this.world.getCurrLevel().getBackground());
		
		//Notify the view of the level built
		for (MPlatform p: this.world.getCurrLevel().getLevel().values()) {init(p);}
		
		//Notify the view of the tokens in the level
		for (MToken t: this.world.getCurrLevel().getTokens().values()) {init(t);}
		
		//Notify the view of the enemies
		for (MCharacter e: this.world.getCurrLevel().getEnemies().values()) {init(e);}
		
		//Notify the view of the players
		for (MCharacter p: this.world.getCurrLevel().getPlayers().values()) {init(p);}
	}
	/***************************************************************************************************
	 * Notify the observers of a change to the player/enemy/obstacle object(s) on the current stage
	 * @param obj as Object - The object that should be passed to the update method of any observer(s)
	 **************************************************************************************************/
	public void init(Object obj) {
		this.setChanged();
		this.notifyObservers(obj);
	}
	
}