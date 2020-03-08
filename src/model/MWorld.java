package model;

import java.io.Serializable;

public class MWorld implements Serializable {
	private static final long serialVersionUID = -7407562494852306193L;
	public final static int BLOCK_SIZE = 32;
	public final static int DEFAULT_HEALTH = 10;
	//========================[ constructor ]========================
	private MLevel currlevel;
	private MWorld nextWorld;

	//==========================[ setters ]==========================
	public void setCurrLevel(MLevel currLevel){
		this.currlevel = currLevel;
	}
	public void setNextWorld(MWorld nextWorld) {
		this.nextWorld = nextWorld;
	}
	
	//==========================[ getters ]==========================
	public MLevel getCurrLevel(){
		return this.currlevel;
	}
	public MWorld getNextWorld() {
		return this.nextWorld;
	}
}
