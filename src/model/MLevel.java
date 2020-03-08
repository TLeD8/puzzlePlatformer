/*******************************************************************************************************
 * This class represents a level that the character plays on.
 *******************************************************************************************************/
package model;

import java.io.Serializable;
import java.util.HashMap;
import javafx.scene.image.ImageView;

public class MLevel implements Serializable {
	private static final long serialVersionUID = -1192971478496616916L;
	// references to other levels
	private MLevel north;
	private MLevel east;
	private MLevel south;
	private MLevel west;
	
	// stuff the level can hold
	private HashMap<Integer, MPlatform> level;
	private HashMap<Integer, MToken> tokens;
	private HashMap<Integer, MCharacter> players;
	private HashMap<Integer, MCharacter> enemies;
	
	//level size
	public int height;
	public int width;
	
	// background parameters
	private String backgroundPath;
	private int backgroundHeight;
	private int backgroundWidth;
	
	//========================[ constructor ]========================
	/*******************************************************************************************************
	 * Constructs a MLevel object that contains the data, height, and width for a level.
	 * @param level Represents a level that is being stored.
	 * @param height Represents the height of the level.
	 * @param width Represents the width of the level.
	 */
	public MLevel(HashMap<Integer, MPlatform> level, int height, int width) {
		setLevel(level);
		this.height = height;
		this.width = width;
	}
	
	//==========================[ setters ]==========================
	/*******************************************************************************************************
	 * Sets this instance's level to the level specified.
	 * @param level Represents the new level.
	 *******************************************************************************************************/
	public void setLevel(HashMap<Integer, MPlatform> level) {
		this.level = level;
	}
	/*******************************************************************************************************
	 * Sets the tokens for the level.
	 * @param tokens Represents the tokens that are added.
	 *******************************************************************************************************/
	public void setTokens(HashMap<Integer, MToken> tokens) {
		this.tokens = tokens;
	}
	/*******************************************************************************************************
	 * Sets the players for the level.
	 * @param players Represents the players being assigned.
	 *******************************************************************************************************/
	public void setPlayers(HashMap<Integer, MCharacter> players) {
		this.players = players;
	}
	/*******************************************************************************************************
	 * Sets the enemies for the level.
	 * @param enemies Represents the enemies on the level.
	 *******************************************************************************************************/
	public void setEnemies(HashMap<Integer, MCharacter> enemies) {
		this.enemies = enemies;
	}
	/*******************************************************************************************************
	 * Removes a specified object from this level.
	 * @param ID Specifies the object.
	 *******************************************************************************************************/
	public void removeObject(int ID) {
		if (this.level.containsKey(ID)) {this.level.remove(ID);}
		else if (this.tokens.containsKey(ID)) {this.tokens.remove(ID);}
		else if (this.players.containsKey(ID)) {this.players.remove(ID);}
		else if (this.enemies.containsKey(ID)) {this.enemies.remove(ID);}
	}
	
		//******[ reference setters ]******
	/*******************************************************************************************************
	 * Sets the background for the level.
	 * @param backgroundPath Represents the name of the background file.
	 * @param backgroundHeight Represents the height of the background.
	 * @param backgroundWidth Represents the width of the background.
	 *******************************************************************************************************/
	public void setBackground(String backgroundPath, int backgroundHeight, int backgroundWidth) {
		this.backgroundPath = backgroundPath;
		this.backgroundHeight = backgroundHeight;
		this.backgroundWidth = backgroundWidth;
	}
	public void setNorth(MLevel north) {
		this.north = north;
	}
	public void setEast(MLevel east) {
		this.east = east;
	}
	public void setSouth(MLevel south) {
		this.south = south;
	}
	public void setWest(MLevel west) {
		this.west = west;
	}
	
	//==========================[ getters ]==========================
	/*******************************************************************************************************
	 * Gets the ImageView object that contains the background image.
	 * @return ImageView object that holds the background image.
	 *******************************************************************************************************/
	public ImageView getBackground() {
		ImageView img = new ImageView();
		img.setPreserveRatio(true);
		img.setFitHeight(this.backgroundHeight);
		img.setFitWidth(this.backgroundWidth);
		try {
			img = new ImageView(this.backgroundPath);
		} catch (Exception e) {}
		
		return img;
	}
	public HashMap<Integer, MPlatform> getLevel() {
		return this.level;
	}
	public HashMap<Integer, MToken> getTokens() {
		return this.tokens;
	}
	public HashMap<Integer, MCharacter> getPlayers() {
		return this.players;
	}
	public HashMap<Integer, MCharacter> getEnemies() {
		return this.enemies;
	}
	
		//******[ reference getters ]******
	public MLevel getNorth() {
		return this.north;
	}
	public MLevel getEast() {
		return this.east;
	}
	public MLevel getSouth() {
		return this.south;
	}
	public MLevel getWest() {
		return this.west;
	}
}
