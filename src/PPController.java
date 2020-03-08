/*******************************************************************************************************
 * This class contains the methods that update game data and the view.
 * @author aarontr1, DSawtelle, pinghsu520, tylerleduc98
 ******************************************************************************************************/
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import javafx.scene.input.KeyCode;
import model.MCharacter;
import model.MLevel;
import model.MPlatform;
import model.MToken;
import model.MWorld;

public class PPController implements Serializable {
	private static final long serialVersionUID = 6564815199745593313L;
	//Attribute(s)--------------------------------------------------------------------------------------
	private PPModel model;
	private HashMap<Character, String> assets;
	private String folderPath = "";
	private int key = 1;
	private int playerKey = 0;
	
	private HashMap<Integer, Integer[]> playerSpawn = new HashMap<Integer, Integer[]>();
	//Constructor(s)------------------------------------------------------------------------------------
	/***************************************************************************************************
	 * Constructs the PPController object according to the given parameters.
	 * @param view Represents the view that the game is displayed
	 * @param folderPath Specifies the folder that assets are saved in
	 * @param assetPath Represents the name of the assets file
	 * @param worldPath Represents the name of file that the world is saved in
	 * @throws FileNotFoundException thrown if the underlying structures couldn't be accessed
	 ***************************************************************************************************/
	public PPController(PPView view, String folderPath, String assetPath, String worldPath) throws FileNotFoundException{
		this.folderPath = folderPath;
		setAssets(assetPath);
		this.model = new PPModel();
		setObserver(view);
		this.model.setWorld(makeWorld(worldPath));
	}
	
	//Mutator(s)----------------------------------------------------------------------------------------
	/**************************************************************************************************
	 * Assigns an observer to the model object for the view
	 * @param view Object that is being added to the set of observers
	 **************************************************************************************************/
	public void setObserver(PPView view) {
		this.model.addObserver(view);
	}
	/***************************************************************************************************
	 * Assigns assets to this object according to assets file specified.
	 * @param assetsPath Name of the assets file.
	 * @throws FileNotFoundException thrown if the assets couldn't be accessed
	 **************************************************************************************************/
	public void setAssets(String assetsPath) throws FileNotFoundException {
		Scanner list = new Scanner(new File(this.folderPath + assetsPath));
		HashMap<Character, String> assets = new HashMap<Character, String>();
		//For valid asset mapping(s), build an asset map (valid mapping: "A=pathToImage.png")
		while (list.hasNextLine()) {
			String line = list.nextLine();
			if (line.charAt(1) == '=') {
				assets.put(line.charAt(0), this.folderPath + line.substring(2));
			}
		} list.close();
		this.assets = assets;
	}
	/***************************************************************************************************
	 * Removes the ID from the model
	 * @param ID Number that represents the ID being removed
	 **************************************************************************************************/
	public void remove(int ID) {
		this.model.removeObject(ID);
	}
	/***************************************************************************************************
	 * Remove the given view as an observer of the model
	 * @param view specifies the view to remove as an observer from the model
	 **************************************************************************************************/
	public void removeObserver(PPView view) {
		this.model.deleteObserver(view);
	}
	/***************************************************************************************************
	 * Calls the model to respawn the character
	 * @param ID Specifies the character that is being respawned
	 **************************************************************************************************/
	public void respawn(int ID) {
		this.model.respawn(ID);
	}
	/***************************************************************************************************
	 * Calls the model to refresh the display. Used whenever anything in the view
	 * is being updated.
	 **************************************************************************************************/
	public void refreshDisplay() {
		this.model.refreshDisplay();
	}
	
	//Accessor(s)---------------------------------------------------------------------------------------
	
	//Functional Method(s)------------------------------------------------------------------------------
	/***************************************************************************************************
	 * Calls methods in the model to update its data according to what movement key was pressed
	 * @param keys Contains what key was pressed
	 * @param ID Specifies the character that is being updated
	 **************************************************************************************************/
	public void update(HashMap<KeyCode, Boolean> keys, int ID) {
		if (isPressed(KeyCode.W, keys) || isPressed(KeyCode.SPACE, keys) || isPressed(KeyCode.UP, keys) || isPressed(KeyCode.KP_UP, keys)) {
			model.jumpCharacter(ID, -30);
		}
		if (isPressed(KeyCode.A, keys) || isPressed(KeyCode.LEFT, keys) || isPressed(KeyCode.KP_LEFT, keys)) {
			model.moveCharacter(ID, -5);
		}
		if (isPressed(KeyCode.D, keys) || isPressed(KeyCode.RIGHT, keys) || isPressed(KeyCode.KP_RIGHT, keys)) {
			model.moveCharacter(ID, 5);
		}
		this.model.checkVelocity(ID);
	}
	/***************************************************************************************************
	 * Returns whether a key input matches the KeyCode parameter being passed
	 * @param code The key that is being compared to.
	 * @param keys Contains the key that the user pressed
	 * @return True if the key pressed matches the KeyCode, false otherwise
	 **************************************************************************************************/
	private boolean isPressed(KeyCode code, HashMap<KeyCode, Boolean> keys) {
		return keys.getOrDefault(code, false);
	}
	/***************************************************************************************************
	 * Randomly selects a background image from a file of images that is used by the view.
	 * @param modifier Specifies the background being selected.
	 * @return A string that represents the path for the background image.
	 **************************************************************************************************/
	private String getRandomBackground(String modifier) {
		String backgroundPath = "";
		ArrayList<File> backgrounds = new ArrayList<File>();
		for (File f: new File("bin/images/" + backgroundPath).listFiles()) {
			if ((f.getName().indexOf("background") != -1) &&
			    (f.getName().indexOf(modifier) != -1)) {backgrounds.add(f);}
		}
		Collections.shuffle(backgrounds);
		
		if (backgrounds.size() > 0) {backgroundPath = "images/" + backgrounds.get(0).getName();}
		return backgroundPath;
	}
	/***************************************************************************************************
	 * Generates the world that contains the levels that are played on
	 * @param worldPath Path of the folder that contains the world file
	 * @return A MWorld object that represents a world
	 * @throws FileNotFoundException
	 **************************************************************************************************/
	private MWorld makeWorld(String worldPath) throws FileNotFoundException {
		Scanner world = new Scanner(new File(this.folderPath + worldPath));
		ArrayList<MLevel> levels = new ArrayList<MLevel>();
		ArrayList<ArrayList<Integer>> markers = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<MLevel>> tempLevels = new ArrayList<ArrayList<MLevel>>();
		MWorld next = new MWorld();
		
		//Get the grid for level layout and a list of the levels the world contains
		int y=0;
		while (world.hasNextLine()) {
			String line = world.nextLine();
			markers.add(new ArrayList<Integer>());
			//Build out the grid for level(s)
			if (line.length() != 0 && Character.isDigit(line.charAt(0))) {
				for (int x=0; x<line.length(); x++) {
					markers.get(y).add(Integer.parseInt(line.charAt(x) + ""));
				}
			}
			//Or build the levels to store in the grid
			else if (line.length() != 0 && line.charAt(0) == '=') {
				int iPrev = 1;
				for (int i=0; i<line.length(); i++) {
					if (line.charAt(i) == '|') {
						levels.add(makeLevel(line.substring(iPrev, i)));	//Add the path between the delimiter
						iPrev = i+1;
					}
				}
			}
			//Or build another world to set as the next world
			else if (line.length() != 0 && line.charAt(0) == ':') {
				try {
					next = makeWorld(line.substring(1));
				}
				catch (Exception e) {}	//There isn't a valid other world to link to
			}
			y++;
		} world.close();
		
		//Randomly allocate all the levels as paired based on the layout grid
		Collections.shuffle(levels);
		int countLevels = 0;
		for (int i=0; i<markers.size() && countLevels<levels.size(); i++) {
			tempLevels.add(new ArrayList<MLevel>());
			for (int j=0; j<markers.get(0).size() && countLevels<levels.size(); j++) {
				//Set the neighboring levels if they exist
				MLevel north = null, south = null, east = null, west = null;
				//Only iterate over spots with level(s)
				if (markers.get(i).get(j) != 1) {
					tempLevels.get(i).add(j, null);
					continue;
				}
				
				tempLevels.get(i).add(j, levels.get(countLevels++));
				try {	//---------- [ NORTH ]--------	//Special case: Set this level as the other level's south
					north = tempLevels.get(i-1).get(j);
					tempLevels.get(i-1).get(j).setSouth(tempLevels.get(i).get(j));
				} catch (Exception e) {}
				try {	//---------- [ SOUTH ]--------
					south = tempLevels.get(i+1).get(j);
				} catch (Exception e) {}
				try {	//---------- [ EAST ]---------
					east = tempLevels.get(i).get(j+1);
				} catch (Exception e) {}
				try {	//---------- [ WEST ]---------	//Special case: Set this level as the other level's east
					west = tempLevels.get(i).get(j-1);
					tempLevels.get(i).get(j-1).setEast(tempLevels.get(i).get(j));
				} catch (Exception e) {}
				//Set the levels found
				tempLevels.get(i).get(j).setNorth(north);
				tempLevels.get(i).get(j).setSouth(south);
				tempLevels.get(i).get(j).setEast(east);
				tempLevels.get(i).get(j).setWest(west);
			}
		}
		//Pick a random level to serve as the starting level
		for (ArrayList<MLevel> a: tempLevels) {levels.addAll(a);}
		Collections.shuffle(levels);
		MLevel lvl = null;
		for (int i=0; i<levels.size() && lvl == null; i++) {lvl = levels.get(i);}
		
		//Set a random starting level for the world
		MWorld retWorld = new MWorld();
		retWorld.setNextWorld(next);
		if (levels.size() > 0) {retWorld.setCurrLevel(lvl);}
		else {System.err.println("PPController.makeWorld() - Error: No levels found to build a world with.");}
		Random rand = new Random();
		if (next.getCurrLevel() != null) {
			if (rand.nextBoolean()) {
				next.setNextWorld(retWorld);
				retWorld = next;
			}
		}
		return retWorld;
	}
	/***************************************************************************************************
	 * Retrieves a level file that contains the data to generate a level to be played on
	 * @param lvlPath Represents the name of the level file
	 * @return A MLevel file
	 * @throws FileNotFoundException
	 **************************************************************************************************/
	private MLevel makeLevel(String lvlPath) throws FileNotFoundException {
		Scanner lvl = new Scanner(new File(this.folderPath + lvlPath));
		HashMap<Integer, MCharacter> players = new HashMap<Integer, MCharacter>();
		HashMap<Integer, MCharacter> enemies = new HashMap<Integer, MCharacter>();
		HashMap<Integer, MToken> tokens = new HashMap<Integer, MToken>();
		HashMap<Integer, MPlatform> level = new HashMap<Integer, MPlatform>();
		
		//Build the level based on the asset mappings, adding appropriate categories of character or platform as needed
		int y=0;
		int x=0;
		while (lvl.hasNextLine()) {
			String line = lvl.nextLine();
			for (x=0; x<line.length(); x++) {
				Character c = line.charAt(x);
				if (Character.isDigit(c) || c == 's') {		//Platform [0-9]
					MPlatform temp = new MPlatform(x*MWorld.BLOCK_SIZE, y*MWorld.BLOCK_SIZE, this.assets.get(c));
					temp.ID = this.key++;
					level.put(temp.ID, temp);
				}
				else if (c == '^') {			//Damage Platform [^]
					MPlatform temp = new MPlatform(x*MWorld.BLOCK_SIZE, y*MWorld.BLOCK_SIZE, this.assets.get(c));
					temp.setDamage(2);
					temp.ID = this.key++;
					level.put(temp.ID, temp);
				}
				else if (c == 'T') {			//Token [T] - GroupID 0
					MToken temp = new MToken(x*MWorld.BLOCK_SIZE, y*MWorld.BLOCK_SIZE, this.assets.get(c), null, 0, true);
					temp.ID = this.key++;
					tokens.put(temp.ID, temp);
				}
				else if (c == 'E') {			///Token [E] - GroupID 1
					MToken temp = new MToken(x*MWorld.BLOCK_SIZE, y*MWorld.BLOCK_SIZE, this.assets.get(c), this.assets.get(Character.toLowerCase(c)), 1, false);
					temp.ID = this.key++;
					tokens.put(temp.ID, temp);
				}
				else if (c == 'G') {			//Token [G] - Group -1 (Win condition)
					MToken temp = new MToken(x*MWorld.BLOCK_SIZE, y*MWorld.BLOCK_SIZE, this.assets.get(c), null, -1, false);
					temp.ID = this.key++;
					tokens.put(temp.ID, temp);
				}
				else if (c == 'S') {			//Player [S]
					MCharacter temp = new MCharacter(x*MWorld.BLOCK_SIZE, y*MWorld.BLOCK_SIZE, this.assets.get(c), null);
					temp.ID = this.playerKey;
					players.put(temp.ID, temp);
					Integer arr[] = new Integer[] {x*MWorld.BLOCK_SIZE, y*MWorld.BLOCK_SIZE};
					this.playerSpawn.put(temp.ID, arr);
					temp.setSpawn(x*MWorld.BLOCK_SIZE, y*MWorld.BLOCK_SIZE);
				}
			}
			y++;
		} lvl.close();
		
		//Set any player's obstacle(s)
		for (MCharacter player: players.values()) {
			ArrayList<MPlatform> temp = new ArrayList<MPlatform>();
			temp.addAll(enemies.values());
			temp.addAll(level.values());
			temp.addAll(tokens.values());
			player.setObstacles(temp);
		}
		//Set any enemy's obstacle(s)
		for (MCharacter enemy: enemies.values()) {
			ArrayList<MPlatform> temp = new ArrayList<MPlatform>();
			temp.addAll(level.values());
			enemy.setObstacles(temp);
		}
		
		MLevel retLvl = new MLevel(level, y*MWorld.BLOCK_SIZE, x*MWorld.BLOCK_SIZE);
		retLvl.setBackground(getRandomBackground(""), PP.HEIGHT, PP.WIDTH);
		retLvl.setTokens(tokens);
		retLvl.setPlayers(players);
		retLvl.setEnemies(enemies);
		return retLvl;
	}
}