
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.function.UnaryOperator;

import javafx.geometry.Insets;

import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.control.TextFormatter.Change;


public class ClientGUI extends Application{

	static int portNum;

//	static boolean connected;
	TextField portNumber, inputText, c1;
	Button serverChoice,clientChoice,b1, pButton, category1, category2, category3;
	HashMap<String, Scene> sceneMap;
	GridPane grid;
	HBox buttonBox, inputBox;
	VBox clientBox, portBox;
	Scene startScene;
	BorderPane startPane;
	Stage primaryStage;
	Server serverConnection;
	Client clientConnection;
	ListView<String> listItems, listItems2;
	Text category, guess, wrongPort;
	int guesses = 6;
	int counter = 0;
	int catcounter = 0;
	int cat1counter = 0;
	int cat2counter = 0;
	int cat3counter = 0;

	String currentCategory;

	String size;

	Text sizetext, loser, winorlose;

	ArrayList<TextField> wordd;

	HBox wordspaces;
	BorderPane playingpane;

	Image background = new Image("background.jpg");







	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Client");

		sceneMap = new HashMap<>();

		sceneMap.put("LandingPage", createPortGui());
		sceneMap.put("ClientPage", createClientGui());
		sceneMap.put("PlayingPage", playingGUI());
		sceneMap.put("WinLosePage", WinLoseGUI());

		String css;

		css = Objects.requireNonNull(this.getClass().getResource("/assets/style.css")).toExternalForm();

		sceneMap.get("LandingPage").getStylesheets().add(css);
		sceneMap.get("ClientPage").getStylesheets().add(css);
		sceneMap.get("PlayingPage").getStylesheets().add(css);
		sceneMap.get("WinLosePage").getStylesheets().add(css);


		primaryStage.setScene(sceneMap.get("LandingPage"));
		primaryStage.show();

	}

	public Scene createPortGui(){
		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-image: url('"+background.getUrl()+"');");
		pane.setStyle("-fx-background-image: url('"+background.getUrl()+"'); -fx-background-repeat: no-repeat; -fx-background-size: cover;");
		Text title = new Text("Enter a port number");
		wrongPort = new Text();
		wrongPort.getStyleClass().add("port-text");
		title.getStyleClass().add("port-label");
		portNumber = new TextField();
		portNumber.setMaxWidth(150);
		portNumber.getStyleClass().add("custom-textfield");
		UnaryOperator<Change> filter = change -> {
			String val = change.getText();
			if (val.matches("[0-9]*")){
				return change;
			}

			return null;
		};

		TextFormatter<String> textFormatter = new TextFormatter<>(filter);
		portNumber.setTextFormatter(textFormatter);

		pButton = new Button("Connect");
		pButton.getStyleClass().add("my-button");
		portBox = new VBox(150);
		VBox topBox = new VBox(50);

		listItems = new ListView<String>();
		listItems2 = new ListView<String>();

		portBox.setAlignment(Pos.CENTER);

		topBox.setAlignment(Pos.CENTER);
		topBox.getChildren().addAll(title,portNumber, wrongPort);
		topBox.setSpacing(50);

		portBox.getChildren().addAll(topBox,pButton);
		portBox.setSpacing(40);

		pane.setCenter(portBox);

		pButton.setOnAction(event -> {
			String portText = portNumber.getText();
			portNum = Integer.parseInt(portText);
			try {
				clientConnection = new Client(data -> {
					Platform.runLater(() -> {
						System.out.println(data.toString());
						String dataText = data.toString();

						if (dataText.charAt(0) == 'I'){
							wordd.get(Integer.valueOf(dataText.substring(3,4))).setText(String.valueOf(data.toString().charAt(2)));
							counter++;
							if (counter == wordd.size()){
								catcounter++;
								primaryStage.setScene(sceneMap.get("ClientPage"));
								loser.setText("You Won!");
								if (currentCategory == "Animals"){
									clientConnection.send("Completed Category: Animals");
									category1.setDisable(true);
								} else if (currentCategory == "Movies") {
									category2.setDisable(true);
									clientConnection.send("Completed Category: Movies");
								}
								else{
									category3.setDisable(true);
									clientConnection.send("Completed Category: Countries");
								}
								counter = 0;
							}
							if (catcounter == 3){
								primaryStage.setScene(sceneMap.get("WinLosePage"));
								winorlose.setText("Your a winner!");
								clientConnection.send("Won Game");
							}
						}
						else if(dataText.charAt(0) == '-'){
							guesses--;
							System.out.println(guesses);
							guess.setText("Guesses: " + String.valueOf(guesses));
							if (guesses == 0){

								if (currentCategory == "Animals"){
									cat1counter++;
									clientConnection.send("Failed Category: Animals");

								} else if (currentCategory == "Movies") {
									cat2counter++;
									clientConnection.send("Failed Category: Movies");
								}
								else{
									cat3counter++;
									clientConnection.send("Completed Category: Countries");
								}

								if (cat1counter == 3 || cat2counter == 3 || cat3counter == 3){
									primaryStage.setScene(sceneMap.get("WinLosePage"));
									winorlose.setText("You're a loser");
									clientConnection.send("Lost Game");
								}
								else{
									primaryStage.setScene(sceneMap.get("ClientPage"));
									loser.setText("You Lost!");
									guesses = 6;
									guess.setText("Guesses: " + String.valueOf(guesses));
								}

							}
						}
						else{
							wordd = new ArrayList<>();
							wordspaces=new HBox();
							System.out.println(data.toString());
							for (int x = 0; x < Integer.valueOf(data.toString()); x++){
								TextField letters = new TextField();
								letters.getStyleClass().add("circle-text-field");


								wordd.add(letters);
								wordspaces.getChildren().add(wordd.get(x));

							}
							Insets insets = new Insets(20, 0, 20, 0); // You can adjust the margin size as needed
							wordspaces.setPadding(insets);
							wordspaces.setSpacing(50);
							playingpane.setBottom(wordspaces);
						}



					});
				}, portNum);
				// If successful, proceed to the client view

				clientConnection.start();

				Thread.sleep(25);
				System.out.println(clientConnection.isConnected());

				if (clientConnection.isConnected()){
					primaryStage.setScene(sceneMap.get("ClientPage"));
					primaryStage.setTitle("Client");
				}
				else{
					wrongPort.setText("Connection failed. Please re-enter the port number.");
				}

			} catch (Exception e){
					System.out.println("Connection failed. Please re-enter the port number.2");

				}
		});

		return new Scene(pane, 600, 500);

	}

	
	public Scene createClientGui() {
		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-image: url('"+background.getUrl()+"');");
		pane.setStyle("-fx-background-image: url('"+background.getUrl()+"'); -fx-background-repeat: no-repeat; -fx-background-size: cover;");
		loser = new Text();
		loser.getStyleClass().add("fancy-text");
		Text top = new Text("Choose a category");
		top.getStyleClass().add("fancy-text");
		category1 = new Button("Animals");
		category2 = new Button("Movies");
		category3 = new Button("Countries");
		category1.getStyleClass().add("round-button");
		category2.getStyleClass().add("round-button");
		category3.getStyleClass().add("round-button");
		HBox choices = new HBox(20);
		VBox content = new VBox(100);
		content.setAlignment(Pos.TOP_CENTER);
		content.getChildren().setAll(top);
		choices.setAlignment(Pos.CENTER);
		choices.getChildren().setAll(category1,category2,category3);
		content.getChildren().setAll(loser,top,choices);
		pane.setCenter(content);

		category1.setOnAction(event -> {
			clientConnection.send("Animals");
			currentCategory = "Animals";
			category.setText("Category: Animals");
			primaryStage.setScene(sceneMap.get("PlayingPage"));
		});

		category2.setOnAction(event -> {
			clientConnection.send("Movies");
			currentCategory = "Movies";
			category.setText("Category: Movies");
			primaryStage.setScene(sceneMap.get("PlayingPage"));
		});

		category3.setOnAction(event -> {
			clientConnection.send("Countries");
			currentCategory = "Countries";
			category.setText("Category: Countries");
			primaryStage.setScene(sceneMap.get("PlayingPage"));
		});


		return new Scene(pane, 500, 400);
		
	}

	public Scene playingGUI(){
		playingpane = new BorderPane();
		playingpane.setStyle("-fx-background-image: url('"+background.getUrl()+"');");
		playingpane.setStyle("-fx-background-image: url('"+background.getUrl()+"'); -fx-background-repeat: no-repeat; -fx-background-size: cover;");
		Text title = new Text("Hangman");
		category = new Text("Category: ");
		guess = new Text("Guesses: " + guesses);
		title.getStyleClass().add("game-text");
		category.getStyleClass().add("game-text");
		guess.getStyleClass().add("game-text");
		Text input = new Text("Enter a character:");
		input.getStyleClass().add("game-text");
		TextField inputText = new TextField();

		inputText.setMaxWidth(30);
		inputText.getStyleClass().add("circle-text-field");
		Button letterButton = new Button("Enter");
		letterButton.getStyleClass().add("my-button");


		inputBox = new HBox(15);
		inputBox.getChildren().setAll(input, inputText, letterButton);
		inputBox.setAlignment(Pos.CENTER);

		letterButton.setOnAction(event -> {
			String letter = inputText.getText();
			inputText.clear();
			clientConnection.send(letter);

		});

		VBox content = new VBox(10);
		content.setAlignment(Pos.TOP_CENTER);
		content.getChildren().setAll(title,category,guess, inputBox);
		playingpane.setCenter(content);
		return new Scene(playingpane, 500, 400);
	}
	public Scene WinLoseGUI(){
		BorderPane pane = new BorderPane();
		pane.setStyle("-fx-background-image: url('"+background.getUrl()+"');");
		pane.setStyle("-fx-background-image: url('"+background.getUrl()+"'); -fx-background-repeat: no-repeat; -fx-background-size: cover;");
		winorlose = new Text();
		winorlose.getStyleClass().add("fancy-text");
		Button restart = new Button("Restart");
		Button exit = new Button("Quit");
		exit.getStyleClass().add("my-button");
		restart.getStyleClass().add("my-button");
		HBox buttons = new HBox(20);
		VBox content = new VBox(20);
		buttons.setAlignment(Pos.CENTER);
		buttons.getChildren().setAll(restart,exit);
		content.setAlignment(Pos.CENTER);
		content.getChildren().setAll(winorlose,buttons);
		pane.setCenter(content);

		restart.setOnAction(event -> {
			primaryStage.setScene(sceneMap.get("ClientPage"));
			cat1counter = 0;
			cat2counter = 0;
			cat3counter = 0;
			catcounter = 0;
			currentCategory = null;
			counter = 0;
			guesses = 6;
			guess.setText("Guesses: " + String.valueOf(guesses));
			category1.setDisable(false);
			category2.setDisable(false);
			category3.setDisable(false);
		});

		exit.setOnAction(event -> {
			System.exit(1);
		});

		return new Scene(pane,500,400);
	}


}
