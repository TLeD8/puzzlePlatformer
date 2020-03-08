/***************************************************************************************************
 * This class represents a Token in the game.
 ***************************************************************************************************/
package model;

import java.io.Serializable;

public class MToken extends MPlatform implements Serializable {
	private static final long serialVersionUID = -6177621520448378554L;
	//Token attribute(s)
	private String stateChangePath = "";
	private boolean isConsumable = true;
	private int groupID = 0;
	/***************************************************************************************************
	 * Constructs a token object that represents a token within the game.
	 * @param posX X position of the token.
	 * @param posY Y position of the token.
	 * @param filePath Name of the file that contains the token data.
	 * @param stateChangePath Name of the change path.
	 * @param groupID ID of the group of tokens.
	 * @param isConsumable Determines whether the token can be consumed or not.
	 ***************************************************************************************************/
	public MToken(int posX, int posY, String filePath, String stateChangePath, int groupID, boolean isConsumable) {
		super(posX, posY, filePath);
		this.groupID = groupID;
		this.isConsumable = isConsumable;
		setStateChange(stateChangePath);
	}
	/***************************************************************************************************
	 * Sets the state change path for this instance.
	 * @param stateChangePath Specifies the location of the file.
	 ***************************************************************************************************/
	public void setStateChange(String stateChangePath) {
		if ((stateChangePath == null) || (stateChangePath.length() == 0)) {stateChangePath = this.filePath;}
		this.stateChangePath = stateChangePath;
	}
	/***************************************************************************************************
	 * Resets the image to the new image.
	 ***************************************************************************************************/
	public void stateChange() {
		//Reset the image to the new image
		this.filePath = this.stateChangePath;
		this.img = null;
		this.getImage();
	}
	
	public boolean isConsumable() {return this.isConsumable;}
	public int getGroupID() {return this.groupID;}
}
