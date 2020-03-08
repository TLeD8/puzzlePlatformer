/*******************************************************************************************************
 * PPView represents the scene on which the game is displayed on. Includes methods to
 * build the game scene, a menu and the ability to save and load games. Changes to the view are
 * done by making calls to the appropriate methods in the controller depending on key inputs.
 * 
 * @author aarontr1, DSawtelle, pinghsu520, tylerleduc98
 ******************************************************************************************************/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.MCharacter;
import model.MPlatform;
import model.MToken;
import model.MWorld;

public class PPView extends Application implements Observer{
	//Attribute(s)--------------------------------------------------------------------------------------
	private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();
	
	
	private MediaPlayer player;
	
	
	private PPController controller;
	private Pane appRoot = new Pane();
		private Label gameInfo;
		private long timeShown = System.currentTimeMillis();
	private Pane gameRoot = new Pane();
	private Pane uiRoot = new Pane();
		private ImageView health;
		private SimpleIntegerProperty tokenCount = new SimpleIntegerProperty(9);
	private Pane menuRoot = new Pane();
		private SimpleDoubleProperty volumeLevel = new SimpleDoubleProperty(1);
	private boolean running = false;
	private int playerID = 0;
	//Mutator(s)----------------------------------------------------------------------------------------
	/***************************************************************************************************
	 * This method initializes the roots that are used to display the game
	 ***************************************************************************************************/
	private void setRoots() {
		this.menuRoot = buildMenu();
		this.uiRoot = buildUI();
		this.gameRoot = buildGame();
		
		this.gameInfo = new Label();
		this.gameInfo.setTranslateX(600);
		this.gameInfo.setTranslateY(800);
		this.gameInfo.setMinWidth(720);
		this.gameInfo.setTextFill(Color.web("#ffff00"));
		this.gameInfo.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		this.gameInfo.setStyle("-fx-font-weight: bold");
		this.gameInfo.setFont(new Font("Arial", 48));
		this.gameInfo.setVisible(false);
		this.gameInfo.setOnMouseClicked(event -> {
			this.menuRoot.setVisible(false);
			this.gameInfo.setVisible(false);
		});
		
		this.appRoot.getChildren().clear();
		this.appRoot.getChildren().addAll(this.gameRoot, this.uiRoot, this.menuRoot, this.gameInfo);
	}
	/***************************************************************************************************
	 * This methods assigns a controller object and is used to start a game.
	 * @param controller Object that contains methods to modify the view and model.
	 ***************************************************************************************************/
	private void setController(PPController controller) {
		this.controller = controller;
		this.controller.setObserver(this);
		this.controller.refreshDisplay();
	}
	/***************************************************************************************************
	 * Sets the variable running value to a boolean in order to pause the main game.
	 * @param value True for game running, false for game pause
	 ***************************************************************************************************/
	public void setRunning(boolean value) {
		running = value;
	}
	
	//Accessor(s)---------------------------------------------------------------------------------------
	
	//Functional Method(s)------------------------------------------------------------------------------
	/***************************************************************************************************
	 * This methods is used to setup the Game properties
	 * @return Pane object that the game is displayed on
	 ***************************************************************************************************/
	private Pane buildGame() {
		Pane game = new Pane();
		
		//Setup the Game properties
		game.setId("game");
		game.setPrefSize(PP.WIDTH, PP.HEIGHT);
		
		return game;
	}
	/***************************************************************************************************
	 * This method is used set up the UI for the game which displays the health
	 * and the amount of coins collected.
	 * @return Pane object that holds the UI elements
	 ***************************************************************************************************/
	private Pane buildUI() {
		Pane ui = new Pane();
		
		//Setup the UI properties
		ui.setId("ui");
		ui.setPrefSize(PP.WIDTH, PP.HEIGHT);

		//Setup health
		this.health = new ImageView("images/100%_health.png");
		this.health.setTranslateX(25);
		this.health.setTranslateY(25);
		this.health.setPreserveRatio(true);
		this.health.setFitHeight(100);
		
		Label counter = new Label("Gathered: "); 
		counter.setTranslateX(350);
		counter.setTranslateY(25);
		counter.setTextFill(Color.web("#ffff00"));
		counter.setStyle("-fx-font-weight: bold");
		counter.setFont(new Font("Arial", 48));
		counter.textProperty().bind(Bindings.convert(this.tokenCount));
		
		ui.getChildren().add(counter);
		
		//Add the elements to the UI
		ui.getChildren().add(this.health);
		
		return ui;
	}
	/***************************************************************************************************
	 * This methods builds the menu that contains options to create a new game, save and load games,
	 * control the music volume, display the objective, and quit the game.
	 * @return Pane object that represents the menu
	 ***************************************************************************************************/
	private Pane buildMenu() {
		Pane menu = new Pane();
		
		//Setup the Menu properties
		menu.setId("menu");
		menu.setPrefSize(PP.WIDTH, PP.HEIGHT);
		
		//Logo creation
		Rectangle title = new Rectangle(600, 90);
		title.setStroke(Color.WHITE);
		title.setStrokeWidth(3);
		title.setFill(null);
		Text text = new Text("Where is Mrs. Gumball?");
		text.setFill(Color.WHITE);
		text.setFont(Font.font("Times New Roman", FontWeight.BOLD, 50));
		//Logo placement
		StackPane stack = new StackPane();
		stack.setAlignment(Pos.CENTER);
		stack.getChildren().add(title);
		stack.getChildren().add(text);
		stack.setTranslateX(PP.WIDTH / 2 - 300);
		stack.setTranslateY(100);
		//Effect around logo
		FadeTransition titleFT = new FadeTransition(Duration.millis(1500), title);
		titleFT.setFromValue(1.0);
		titleFT.setToValue(0.2);
		titleFT.setCycleCount(Timeline.INDEFINITE);
		titleFT.setAutoReverse(true);
		titleFT.play();
		
		//Selection Menu
		MenuItem newGame = new MenuItem("New Game", 300);
		newGame.setOnMousePressed(event -> {
			try {
				//Set the roots, a new controller, flip menu visibility, and set the game as running
				setRoots();
				setController(new PPController(this, "src/", "data/assets.dat", "data/world1.wrld"));
				this.menuRoot.setVisible(!this.menuRoot.isVisible());
				this.gameInfo.setVisible(false);
				this.running = true;
			} catch (FileNotFoundException e) {e.printStackTrace(System.err);}
		});
		MenuItem loadGame = new MenuItem("Load Game", 300);
		MenuItem saveGame = new MenuItem("Save Game", 300);
		MenuItem objectiveMenu = new MenuItem("Objective", 300);
		
		Slider volume = new Slider();
		volume.setScaleY(4);
		volume.setMin(0);
		volume.setMax(1);
		volume.setValue(1);
		volume.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
				volumeLevel.setValue(newVal.doubleValue());
			}
		});
		MenuItem quit = new MenuItem("Quit", 300);
		quit.setOnMousePressed(event -> {
			Stage stage = (Stage) menu.getScene().getWindow();
			stage.close();
		});
		VBox menuOptions = new VBox(
			newGame,
			createLine(400),
			loadGame,
			createLine(400),
			objectiveMenu,
			createLine(400),
			saveGame,
			createLine(400),
			quit,
			createLine(400),
			volume);
		HBox saveInput = new HBox();
		saveInput.setVisible(false);
		Label label = new Label("Save file name: ");
		label.setTextFill(Color.BLACK);
		label.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
		TextField textField = new TextField();
		textField.setPrefHeight(40);
		textField.setPrefWidth(120);
		Button save = new Button("Save");
		save.setOnMouseClicked(e -> {
			try {
				saveGame(textField.getText());
				menuOptions.setVisible(true);
				saveInput.setVisible(false);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		});
		Button cancel = new Button("Cancel");
		cancel.setOnMouseClicked(e -> {
			saveInput.setVisible(false);
			menuOptions.setVisible(true);
		});
		
		saveInput.getChildren().addAll(label, textField, save, cancel);
		saveInput.setSpacing(10);
		
		HBox objectiveBox = new HBox();
		Label objectiveLabel = new Label("Don't forget to find Mrs. Gumball!");
		objectiveLabel.setTextFill(Color.WHITE);
		objectiveLabel.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
		objectiveLabel.setMinWidth(400);
		Button ok = new Button("Ok");
		ok.setOnMouseClicked(event -> {
			menuOptions.setVisible(true);
			objectiveBox.setVisible(!objectiveBox.isVisible());
		});
		objectiveBox.getChildren().addAll(objectiveLabel, ok);
		objectiveBox.setTranslateX(200);
		objectiveBox.setTranslateY(20);
		objectiveBox.setSpacing(10);
		objectiveBox.setVisible(false);
		objectiveMenu.setOnMouseClicked(event -> {
			try {					
					menuOptions.setVisible(false);
					objectiveBox.setVisible(!objectiveBox.isVisible());
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		});
		textField.setOnKeyPressed(event -> {
			try {
				if (event.getCode() == KeyCode.ENTER) {
					saveGame(textField.getText());
					menuOptions.setVisible(true);
					saveInput.setVisible(false);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
		});
		saveGame.setOnMousePressed(event -> {
			try {
				menuOptions.setVisible(false);
				saveInput.setVisible(true);
			} catch (Exception e) {e.printStackTrace(System.err);}
		});
		VBox loadingOptions = new VBox();
		loadingOptions.setVisible(false);
		loadGame.setOnMousePressed(event -> {
			try {
				loadingOptions.setVisible(true);
				menuOptions.setVisible(false);
				loadingOptions.getChildren().clear();
				for (File f: new File("src\\..\\saves\\").listFiles()) {
					MenuItem loadOption = new MenuItem(f.getName(), 500);
					loadingOptions.getChildren().addAll(buildLoadingOption(loadOption), createLine(600));
				}
				MenuItem exit = new MenuItem("Cancel", 200);
				exit.setOnMouseClicked(e -> {
					loadingOptions.setVisible(false);
					menuOptions.setVisible(true);
				});
				loadingOptions.getChildren().add(exit);
			} catch (Exception e) {e.printStackTrace(System.err);}
		});
		saveInput.setTranslateX(PP.WIDTH / 2 - 200);
		saveInput.setTranslateY(300);
		objectiveBox.setTranslateX(PP.WIDTH / 2 - 200);
		objectiveBox.setTranslateY(300);
		loadingOptions.setTranslateX(PP.WIDTH / 2 - 290);
		loadingOptions.setTranslateY(200);
		menuOptions.setTranslateX(PP.WIDTH / 2 - 190);
		menuOptions.setTranslateY(200);
		
		menu.getChildren().addAll(stack, menuOptions, loadingOptions, saveInput, objectiveBox);
		
		return menu;
	}
	/***************************************************************************************************
	 * Sets up a mouse pressed event for a menu option that will load a game according to the
	 * name of the file that was clicked.
	 * @param option Represents the MenuItem that is being clicked on.
	 * @return MenuItem that loads a game when clicked on.
	 ***************************************************************************************************/
	private MenuItem buildLoadingOption(MenuItem option) {
		option.setOnMousePressed(event -> {
			try {
				//Set the roots, the loaded controller, flip menu visibility, and set the game as running
				setRoots();
				loadGame(option.getName());
				this.menuRoot.setVisible(!this.menuRoot.isVisible());
				this.gameInfo.setVisible(false);
				this.running = true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
		return option;
	}
	/***************************************************************************************************
	 * Creates a Line that is used to clearly separate menu options.
	 * @param size Represents the length of the line.
	 * @return a white line according to the size specified.
	 ***************************************************************************************************/
	public Line createLine(int size){
		Line line = new Line();
		line.setEndX(size);
		line.setStroke(Color.WHITE);
		return line;		
	}
	/***************************************************************************************************
	 * Saves the game in a saves folder by creating a save file that contains the controller.
	 * @param filePath Where the game is saved at.
	 * @throws Exception
	 ***************************************************************************************************/
	private void saveGame(String filePath) throws Exception {
		String saveLoc = "src\\..\\saves\\";
		//Automated save name - Use the current time as a default save name
		if (filePath.length() == 0) {filePath = "save_" + System.currentTimeMillis() + ".save";}
		
		//Save the game
		ObjectOutputStream oOut = new ObjectOutputStream(new FileOutputStream(saveLoc + filePath));
		oOut.writeObject(this.controller);
		oOut.close();
	}
	/***************************************************************************************************
	 * Loads a game by setting the views controller to the controller contained in the
	 * save file specified by filePath
	 * @param filePath Represents the name of the save file
	 * @throws Exception
	 ***************************************************************************************************/
	private void loadGame(String filePath) throws Exception {
		String saveLoc = "src\\..\\saves\\";
		//Automated load choice - Use the most recent save file
		if (filePath.length() == 0) {
			long lastMod = Long.MIN_VALUE;
			for (File f: new File(saveLoc).listFiles()) {
				if (f.lastModified() > lastMod) {filePath = f.getName();}
			}
		}
		
		ObjectInputStream oIn = new ObjectInputStream(new FileInputStream(saveLoc + filePath));
		setRoots();
		setController((PPController) oIn.readObject());
		oIn.close();
	}
	/***************************************************************************************************
	 * Initializes the scene the game is displayed on by calling setRoots and then
	 * showing it on the screen. Sets up music, the escape key to open the menu, and
	 * the AnimationTimer.
	 ***************************************************************************************************/
	@Override
	public void start(Stage primaryStage) throws Exception {
		//Setup the stage properties
		primaryStage.setMaximized(true);
		
		//Setup the application component layering
		setRoots();
		setController(new PPController(this, "src/", "data/assets.dat", "data/world1.wrld"));
		
		//Setup the music player
		String path = "./src/../music/music.mp3";
		Media musicFile = new Media(new File(path).toURI().toString());
		player = new MediaPlayer(musicFile);
		player.setStartTime(Duration.seconds(0));
		player.setStopTime(musicFile.getDuration());
		player.setCycleCount(MediaPlayer.INDEFINITE);
		player.play();
		player.volumeProperty().bind(this.volumeLevel);
		
		//Build the scene that gets inputs from the application root stage
		Scene scene = new Scene(appRoot);
	    scene.getStylesheets().addAll(this.getClass().getResource("data/style.css").toExternalForm());
		scene.setOnKeyPressed(event -> {
			this.keys.put(event.getCode(), true);
			//After a delay, take the message down when showing it
			if (!( ((System.currentTimeMillis() - timeShown) / (long) 1000F) < 2)) {this.gameInfo.setVisible(false);}
			if (event.getCode() == KeyCode.ESCAPE) {
				this.menuRoot.setVisible(!this.menuRoot.isVisible());
				this.running = !this.menuRoot.isVisible();
			}
		});
		scene.setOnKeyReleased(event -> this.keys.put(event.getCode(), false));
		
		primaryStage.setTitle("Where is Mrs. Gumball?");
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.show();
		
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (running) {
					controller.update(keys, playerID);
				}
			}
		};
	    timer.start();
	}
	/***************************************************************************************************
	 * Updates the view according to the object that is passed. This includes changing the background image,
	 * going to the next level, completing a world, getting a token, taking damage, changing the platform, or moving
	 * the character.
	 * @param model Represents an object that contains the data that the game is operating on
	 * @param obj Represents the object that the view is changing to.
	 ***************************************************************************************************/
	@Override
	public void update(Observable model, Object obj) {
		//Game State - Level Transition
		if (obj.getClass() == Boolean.class) {
			Boolean o = (Boolean) obj;
				this.gameRoot.getChildren().clear();
				this.controller.refreshDisplay();
				this.controller.respawn(this.playerID);
			if (o) {	//World Complete
				//Game state message
				this.gameInfo.setText("You managed one trial... but can you survive another?");
				this.gameInfo.setVisible(true);
				timeShown = System.currentTimeMillis();
				this.menuRoot.setVisible(true);
				try {
					this.setController(new PPController(this, "src/", "data/assets.dat", "data/world1.wrld"));
				} catch (FileNotFoundException e) {e.printStackTrace();}
			} else {	//New Level
				//Game state message
				this.gameInfo.setText("Another place to look...");
				this.gameInfo.setVisible(true);
				timeShown = System.currentTimeMillis();
			}
		}
		//Background(s) - Add level background
		else if (obj.getClass() == ImageView.class) {
			ImageView o = (ImageView) obj;
			if (!this.gameRoot.getChildren().contains(o)) {this.gameRoot.getChildren().add(o);}
		}
		//Character(s) - Player(s) and enemies
		else if (obj.getClass() == MCharacter.class) {
			MCharacter o = (MCharacter) obj;
			ImageView img = o.getImage();
			if (!this.gameRoot.getChildren().contains(img)) {this.gameRoot.getChildren().add(img);}
			else {
				if (o.ID == this.playerID) {//Player Functionality
					//Update token count and evaluate death
					this.tokenCount.setValue(o.getTokenCount());
					if (this.tokenCount.getValue() <= 0) {
						//Game state message
						this.gameInfo.setText("Death Won't Help You Save Her!");
						this.gameInfo.setVisible(true);
						timeShown = System.currentTimeMillis();
						this.menuRoot.setVisible(true);
						try {
							this.setController(new PPController(this, "src/", "data/assets.dat", "data/world1.wrld"));
						} catch (FileNotFoundException e) {e.printStackTrace();}
					}
					
					//Update health bar based on o.getHealth()
					if (o.getHealth() <= (MWorld.DEFAULT_HEALTH-(4*(MWorld.DEFAULT_HEALTH/4)))) {
					}
					else if (o.getHealth() <= (MWorld.DEFAULT_HEALTH-(3*(MWorld.DEFAULT_HEALTH/4)))) {
						this.health.setImage(new Image("images/25%_health.png"));
					}
					else if (o.getHealth() <= (MWorld.DEFAULT_HEALTH-(2*(MWorld.DEFAULT_HEALTH/4)))) {
						this.health.setImage(new Image("images/50%_health.png"));
					}
					else if (o.getHealth() <= (MWorld.DEFAULT_HEALTH-(1*(MWorld.DEFAULT_HEALTH/4)))) {
						this.health.setImage(new Image("images/75%_health.png"));
					}
					else if (o.getHealth() <= (MWorld.DEFAULT_HEALTH-(0*(MWorld.DEFAULT_HEALTH/4)))) {
						this.health.setImage(new Image("images/100%_health.png"));
					}
					
				}
				else if (o.getHealth() <= 0) {//Enemy Death Functionality
					this.gameRoot.getChildren().remove(img);
				}
			}
		}
		//Token(s) - Consumables and Interactables
		else if (obj.getClass() == MToken.class) {
			MToken o = (MToken) obj;
			ImageView img = o.getImage();
			if (!this.gameRoot.getChildren().contains(img)) {this.gameRoot.getChildren().add(img);}
			else {
				//Does the token need to be removed from the level or interacted with?
				if (o.isConsumable()) {
					this.gameRoot.getChildren().remove(img);
					this.controller.remove(o.ID);;
				}
			}
		}
		//Platform(s)
		else if (obj.getClass() == MPlatform.class) {
			MPlatform o = (MPlatform) obj;
			ImageView img = o.getImage();
			if (!this.gameRoot.getChildren().contains(img)) {this.gameRoot.getChildren().add(img);}
		}
	}
	/***************************************************************************************************
	 * Represents a menu item that is displayed on the game menu. Sets up the visual elements
	 * for a menu item.
	 ***************************************************************************************************/	
	private class MenuItem extends StackPane {
		private String name;
		/***************************************************************************************************
		 * Constructor for a MenuItem. Sets up the font, color, size and hover effects for
		 * the item.
		 * @param name Represents the text that is displayed on the menu option
		 * @param width Represents the width of the menu item rectangle
		 ***************************************************************************************************/
		public MenuItem(String name, int width){
			this.name = name;
			Rectangle option = new Rectangle(width, 80);
			option.setOpacity(0.2);
			Text text = new Text(name);
			text.setFill(Color.BLACK);
			text.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD,40));
			setAlignment(Pos.CENTER);
			setOnMouseEntered(event -> {
				option.setFill(Color.DARKGREY);
				option.setStroke(Color.WHITE);
				text.setFill(Color.WHITE);
			});
			setOnMouseExited(event -> {
				option.setFill(Color.BLACK);
				option.setStroke(null);
				text.setFill(Color.BLACK);
			});

			getChildren().addAll(option, text);
		}
				
		public String getName() {
			return name;
		}
		
		
	}
}