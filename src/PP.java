import java.awt.Toolkit;

import javafx.application.Application;

/*******************************************************************************************************
 * @author aarontr1, DSawtelle, pinghsu520, tylerleduc98
 * 
 * Plays a Puzzle Platformer game where you control a character with the objective of reaching its partner.
 ******************************************************************************************************/
public class PP {
	//Attribute(s)--------------------------------------------------------------------------------------
	public final static int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
	public final static int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
	
	/***************************************************************************************************
	 * Opens the puzzle platformer application
	 * @param args as String[] - Arguments passed in the command line launch to augment program function
	 **************************************************************************************************/
	public static void main(String args[]) {
		try {
			Application.launch(PPView.class, args);
		} catch (Exception e) {e.printStackTrace(System.err);}
	}
	
}