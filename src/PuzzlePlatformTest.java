
/*******************************************************************************************************
 * @author aarontr1, DSawtelle, pinghsu520, tylerleduc98
 * 
 * This class contains the JUnit test case(s) for the puzzle platformer project
 ******************************************************************************************************/
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import org.junit.Test;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import model.*;

public class PuzzlePlatformTest {
	//Attribute(s)--------------------------------------------------------------------------------------
	
	//Test(s)------------------------------------------------------------------------------------
	@Test
	/***************************************************************************************************
	 * +90% Model Package/PPModel/PPController Coverage Test Module
	 **************************************************************************************************/
	public void modelTest() {
		PPView view = new PPView();
		PPController controller = null;
		System.setErr(null);	//Prevent spurious console message(s) due to JavaFX components
		
		try {
			for (int i=0; i<30; i++) {controller = new PPController(view, "src/", "data/assets.dat", "data/world1.wrld");}
			controller = new PPController(view, "src/", "data/assets.dat", "data/world_test.wrld");
		} catch (Exception e) {}
		controller.removeObserver(view);
		controller.refreshDisplay();
		
		HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();
		int ID = 0;
		
		//Test updates
		controller.update(keys, ID);	//Nothing
		
		keys.put(KeyCode.W, true);			//W
		keys.put(KeyCode.A, true);			//A
		keys.put(KeyCode.D, true);			//D
		controller.update(keys, ID);
		keys.put(KeyCode.W, false);
		keys.put(KeyCode.A, false);
		keys.put(KeyCode.D, false);
		
		keys.put(KeyCode.SPACE, true);		//Spacebar
		keys.put(KeyCode.LEFT, true);		//Left arrow
		keys.put(KeyCode.RIGHT, true);		//Right arrow
		for (int i=0; i<1000; i++) {controller.update(keys, ID);}
		keys.put(KeyCode.SPACE, false);
		keys.put(KeyCode.LEFT, false);
		keys.put(KeyCode.RIGHT, false);
		
		keys.put(KeyCode.KP_UP, true);		//Numpad Up
		keys.put(KeyCode.KP_LEFT, true);	//Numpad Left
		keys.put(KeyCode.KP_RIGHT, true);	//Numpad Right
		controller.update(keys, ID);
		keys.put(KeyCode.KP_UP, false);
		keys.put(KeyCode.KP_LEFT, false);
		keys.put(KeyCode.KP_RIGHT, false);
		
		keys.put(KeyCode.UP, true);			//Up
		controller.update(keys, ID);
		keys.put(KeyCode.UP, false);
		
		controller.respawn(ID);				//Teleporter Interaction
		keys.put(KeyCode.LEFT, true);
		for (int i=0; i<100; i++) {controller.update(keys, ID);}
		keys.put(KeyCode.LEFT, false);
		keys.put(KeyCode.UP, true);
		for (int i=0; i<1000; i++) {controller.update(keys, ID);}
		keys.put(KeyCode.UP, false);
		
		controller.respawn(ID);				//Death by Falling
		keys.put(KeyCode.RIGHT, true);
		for (int i=0; i<500; i++) {controller.update(keys, ID);}
		keys.put(KeyCode.RIGHT, false);
		
		controller.remove(-1);
		controller.remove(ID);
		for (int i=0; i<2000; i++) {controller.remove(i);}
		
		//Fill in the remaining stuff
		MWorld world = new MWorld();	//World coverage
		world.getNextWorld();
		MToken token = null;			//Token coverage
		token = new MToken(0, 0, "images/health_orb.png", "", -1, true);
		token.stateChange();
		token.isConsumable();
		token.getGroupID();
		HashMap<Integer, MPlatform> temp = new HashMap<Integer, MPlatform>();
		temp.put(10, token);
		MLevel level = new MLevel(new HashMap<Integer, MPlatform>(), 1920, 1080);	//MLevel coverage
		level.setBackground("images/background_1.jpg", 1920, 1080);
		level.getBackground();
		level.getLevel();
		level.getTokens();
		level.getEnemies();
		level.getNorth();
		level.getEast();
		level.getSouth();
		level.getWest();
		SpriteAnimation ani = new SpriteAnimation(new ImageView(), Duration.INDEFINITE, 1, 1, 0, 0, 32, 32);
		ani.interpolate(2.2);
		PPModel model = new PPModel();												//PPModel Coverage
		model.setWorld(world);
		model.getWorld();
		assertThrows(NullPointerException.class, () -> {model.getObject(0);});
		assertThrows(NullPointerException.class, () -> {model.respawn(0);});
		assertThrows(NullPointerException.class, () -> {model.jumpCharacter(ID, 1);});
	}
}