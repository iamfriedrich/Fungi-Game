package driver;

import java.util.ArrayList;

import board.Board;
import board.Displayable;
import board.Player;
import cards.Card;
import cards.CardType;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class GraphicalGame extends Application {
	BorderPane root = new BorderPane();
	HBox player1HandBox = new HBox(10);
	HBox player1DisplayBox = new HBox(10);
	HBox player2HandBox = new HBox(10);
	HBox player2DisplayBox = new HBox(10);
	HBox forestBox = new HBox(10);
	Label gameStatusLabel = new Label();
	VBox buttonBox = new VBox(10);
	
	private Player p1, p2;
	boolean p1plays = true;
	Player currentPlayer;

	public GraphicalGame() {
		player1HandBox.setAlignment(Pos.CENTER);
		player1DisplayBox.setAlignment(Pos.CENTER);
		player2HandBox.setAlignment(Pos.CENTER);
		player2DisplayBox.setAlignment(Pos.CENTER);
		forestBox.setAlignment(Pos.CENTER);
		gameStatusLabel.setPrefSize(130, 300);
		
		VBox right = new VBox(20);
		right.getChildren().addAll(gameStatusLabel, buttonBox);
		root.setRight(right);
		
		VBox center = new VBox(5);
		center.getChildren().addAll(player1HandBox, player1DisplayBox, forestBox, player2DisplayBox, player2HandBox);
		root.setCenter(center);
		
		Button takeOneButton = new Button("Take One");
		takeOneButton.setPrefWidth(120);
		takeOneButton.setOnAction(e -> {
			for(int i=0; i<forestBox.getChildren().size(); i++) {
				Pane box = (Pane)forestBox.getChildren().get(i);
				if (box.getBorder() != null) {
					int pos = forestBox.getChildren().size() - i;
					if (currentPlayer.takeCardFromTheForest(pos)) {
						if (Board.getForestCardsPile().pileSize() > 0) {
							Board.getForest().add(Board.getForestCardsPile().drawCard());
						}
						this.updateGameAfterMove();
					}
					else {
						promptMessage("Take failed!");
					}
					break;
				}
			}
		});
		
		Button takeFromDecayButton = new Button("Take From Decay");
		takeFromDecayButton.setPrefWidth(120);
		takeFromDecayButton.setOnAction(e -> {
			if (currentPlayer.takeFromDecay()) {
				updateGameAfterMove();
			}
			else {
				promptMessage("Take failed!");
			}
		});
		
		Button cookButton = new Button("Cook Mushrooms");
		cookButton.setPrefWidth(120);
		cookButton.setOnAction(e -> {
			ArrayList<Card> selection = this.getSelectionInHandBox();
			if (currentPlayer.cookMushrooms(selection)) {
				updateGameAfterMove();
			}
			else {
				promptMessage("Cook failed!");
			}
		});
		
		Button sellButton = new Button("Sell Mushrooms");
		sellButton.setPrefWidth(120);
		sellButton.setOnAction(e -> {
			ArrayList<Card> selection = this.getSelectionInHandBox();
			if (selection.size() > 0) {
				String mushType = selection.get(0).getName();
				if (currentPlayer.sellMushrooms(mushType, selection.size())) {
					updateGameAfterMove();
				} 
				else {
					promptMessage("Sell failed!");
				}
			}
		});
		
		Button putDownButton = new Button("Put Pan Down");
		putDownButton.setPrefWidth(120);
		putDownButton.setOnAction(e -> {
			if (currentPlayer.putPanDown()) {
				updateGameAfterMove();
			}else {
				promptMessage("Put failed!");
			}
		});
		
		buttonBox.getChildren().addAll(takeOneButton, takeFromDecayButton, cookButton, sellButton, putDownButton);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		this.initializeGame();
		Scene scene = new Scene(root, 1200, 700);
		stage.setScene(scene);
		stage.setTitle("Fungi");
		stage.show();
	}
	
	public static void promptMessage(String message) {
		new Alert(AlertType.INFORMATION, message).showAndWait();
	}
	
	private void initializeGame() {
		Board.initialisePiles();
		Board.setUpCards();
		Board.getForestCardsPile().shufflePile();

		//Populate forest
		for (int i=0; i<8;i++) {
			Board.getForest().add(Board.getForestCardsPile().drawCard());
		}
		
		//Initialize players and populate player hands
		p1  = new Player(); p1plays = true; currentPlayer=p1; p2 = new Player();
		p1.addCardtoHand(Board.getForestCardsPile().drawCard());p1.addCardtoHand(Board.getForestCardsPile().drawCard());p1.addCardtoHand(Board.getForestCardsPile().drawCard());
		p2.addCardtoHand(Board.getForestCardsPile().drawCard());p2.addCardtoHand(Board.getForestCardsPile().drawCard());p2.addCardtoHand(Board.getForestCardsPile().drawCard());
		
		updatePlayer();
		updateForest();
		updateStatus();
	}
	
	private void updateStatus() {
		String text = "Forest pile: " + Board.getForestCardsPile().pileSize() + "\n\nDecay pile:\n";
		for (Card c : Board.getDecayPile()) {
			text += "- " + c.getName()+"\n";
		}
		text += "\n\n=== Player 1 ===\nSticks: "+ p1.getStickNumber() + "\nScore: " + p1.getScore();
		text += "\n\n=== Player 2 ===\nSticks: "+ p2.getStickNumber() + "\nScore: " + p2.getScore();
		gameStatusLabel.setText(text);
	}
	
	private void updatePlayer() {
		updateDisplayablePane(player1HandBox, p1.getHand(), p1plays);
		updateDisplayablePane(player1DisplayBox, p1.getDisplay(), false);
		updateDisplayablePane(player2HandBox, p2.getHand(), !p1plays);
		updateDisplayablePane(player2DisplayBox, p2.getDisplay(), false);
	}
		
	private void updateGameAfterMove() {
		if (Board.getForest().size() > 0) {
			Board.updateDecayPile();
		}
		if (Board.getForestCardsPile().pileSize() > 0) {
			Board.getForest().add(Board.getForestCardsPile().drawCard());
		}
		
		p1plays = !p1plays;
		if (p1plays) 
			currentPlayer = p1;
		else
			currentPlayer = p2;
		
		this.updatePlayer();
		this.updateForest();
		this.updateStatus();
		
		if (Board.getForest().size() == 0) {
			if (p1.getScore() > p2.getScore()) {
				promptMessage("Player 1 wins!");
			} else if (p2.getScore() > p1.getScore()) {
				promptMessage("Player 2 wins!");
			} else {
				promptMessage("There was a tie!");
			}
			
			this.initializeGame();
		}
	}
	
	private void updateForest() {
		forestBox.getChildren().clear();
		for (int i = 0; i < Board.getForest().size(); i++) {
			Pane box =  CreateImageBoxForCard(Board.getForest().getElementAt(i));
			forestBox.getChildren().add(box);
			box.setOnMouseClicked(e -> {
				for (int j=0; j<forestBox.getChildren().size(); j++) {
					((Pane)forestBox.getChildren().get(j)).setBorder(null);
				}
				box.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, new CornerRadii(0), new BorderWidths(2))));
			});
		}
	}
	
	private Pane CreateImageBoxForCard(Card card) {
		StackPane box = new StackPane();
		Image img;
		if (card.getType() == CardType.CIDER)
			img = new Image("file:img/cidre.jpg");
		else if (card.getName().equals("morel"))
			img = new Image("file:img/morels.jpg");
		else
			img = new Image("file:img/" + card.getName() + ".jpg");
		
		ImageView imageView = new ImageView(img);
		box.getChildren().add(imageView);
		if (card.getType() == CardType.NIGHTMUSHROOM)
			box.getChildren().add(new Circle(16, Color.YELLOW));
	
		return box;
	}
	
	private void updateDisplayablePane(Pane pane, Displayable display, boolean selectable) {
		pane.getChildren().clear();
		for (int i = 0; i < display.size(); i++) {
			Pane box =  CreateImageBoxForCard(display.getElementAt(i));
			pane.getChildren().add(box);
			
			if (selectable) {
				box.setOnMouseClicked(e -> {
					if (box.getBorder() == null)
						box.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
								new CornerRadii(0), new BorderWidths(2))));
					else
						box.setBorder(null);
				});
			}
		}
	}
	
	private ArrayList<Card> getSelectionInHandBox(){
		ArrayList<Card> selection = new ArrayList<Card>();
		HBox handBox = p1plays? player1HandBox : player2HandBox;
		for (int i=0; i<handBox.getChildren().size(); i++) {
			Pane box = (Pane)handBox.getChildren().get(i);
			if (box.getBorder() != null) {
				selection.add(currentPlayer.getHand().getElementAt(i));
			}
		}
		return selection;
	}

}
