package finalProject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * COS216 Final Project front-end
 * 
 * @author mjg29296
 * @version Spring 2023
 */

public class View extends Application
		implements PropertyChangeListener, EventHandler<ActionEvent>, ChangeListener<String> {

	/** Instance of the model */
	private Model myModel;

	/** Combobox for the drop down menu featuring different board sizes */
	private ComboBox<String> choices;

	/**
	 * GridPane that stores all buttons within the board (apart from the reset
	 * button)
	 */
	private GridPane grid = new GridPane();

	/** 2D array of all buttons within the board (apart from the reset button) */
	private Button[][] layout;

	/** Stores previous column that player 1 was at */
	private int p1CurCol;
	/** Stores previous row that player 1 was at */
	private int p1CurRow;
	/** Stores previous column that player 2 was at */
	private int p2CurCol;
	/** Stores previous row that player 2 was at */
	private int p2CurRow;

	/** Informs front-end whose turn it is */
	private int pCount = 0;

	/** Resets the game */
	private Button reset;

	/** Provides feedback on the game */
	private Label feedback;

	/**
	 * Ensures no other code can run within handle(ActionEvent arg0) before a board
	 * size has been selected
	 */
	private boolean flag = false;

	/**
	 * start - initializes front-end
	 */
	@Override
	public void start(Stage primaryStage) {
		myModel = new Model(5);
		myModel.addPropertyChangeListener(this);

		try {
			BorderPane root = new BorderPane();
			root.setStyle("-fx-background-color: #333333");
			Scene scene = new Scene(root, 670, 665);
			primaryStage.setTitle(myModel.getTitle());

			choices = new ComboBox<String>();
			choices.setPromptText("Grid size");
			choices.getItems().addAll("5x5", "7x7", "11x11");
			choices.setOnAction(this);
			root.setTop(choices);

			root.setCenter(grid);

			reset = new Button("Reset");
			reset.setMinSize(50, 50);
			reset.setOnAction(this);
			root.setRight(reset);

			feedback = new Label();
			feedback.setWrapText(true);
			root.setBottom(feedback);

			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * main - main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * builder - creates a grid of buttons of arbitrary size
	 */
	public void builder() {
		try {
			grid.getChildren().clear();
			layout = new Button[myModel.getSize() * 2 + 1][myModel.getSize() * 2 + 1];
			for (int k = 0; k < myModel.getSize() * 2 + 1; k++) {
				for (int z = 0; z < myModel.getSize() * 2 + 1; z++) {
					Button temp = new Button();
					temp.setOnAction(this);
					if (k % 2 == 0) {
						if (z % 2 == 0) {
							temp.setMinHeight(15);
							temp.setMaxHeight(15);
							temp.setMinWidth(15);
							temp.setMaxWidth(15);
							temp.setDisable(true);
						} else {
							temp.setMinHeight(40);
							temp.setMaxHeight(40);
							temp.setMinWidth(15);
							temp.setMaxWidth(15);
						}
					} else {
						if (z % 2 == 0) {
							temp.setMinHeight(15);
							temp.setMaxHeight(15);
							temp.setMinWidth(40);
							temp.setMaxWidth(40);
						} else {
							temp.setMinHeight(40);
							temp.setMaxHeight(40);
							temp.setMinWidth(40);
							temp.setMaxWidth(40);
						}
					}

					if (k == 0 || z == 0 || k == myModel.getSize() * 2 || z == myModel.getSize() * 2) {
						temp.setDisable(true);
					}

					layout[k][z] = temp;
					layout[k][z].textProperty().addListener(this);
					grid.add(temp, k, z);
				}
			}
			layout[(myModel.getSize() * 2 + 1) / 2][1].setStyle("-fx-background-color: #add8e6");
			p1CurCol = (myModel.getSize() * 2 + 1) / 2;
			p1CurRow = 1;
			layout[(myModel.getSize() * 2 + 1) / 2][(myModel.getSize() * 2 + 1) - 2]
					.setStyle("-fx-background-color: #FFA500");
			p2CurCol = (myModel.getSize() * 2 + 1) / 2;
			p2CurRow = (myModel.getSize() * 2 + 1) - 2;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * propertyChange - receives calls via pcs from the back-end in regard to
	 * updating the front-end
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals("newSize")) {
			builder();
		}

		if (event.getPropertyName().equals("newMove")) {
			if (pCount % 2 == 0) {
				layout[p1CurCol][p1CurRow].setStyle(null);
				layout[Integer.parseInt(String.valueOf(event.getOldValue()))][Integer
						.parseInt(String.valueOf(event.getNewValue()))].setStyle("-fx-background-color: #add8e6");
				p1CurCol = Integer.parseInt(String.valueOf(event.getOldValue()));
				p1CurRow = Integer.parseInt(String.valueOf(event.getNewValue()));
				pCount++;
			} else {
				layout[p2CurCol][p2CurRow].setStyle(null);
				layout[Integer.parseInt(String.valueOf(event.getOldValue()))][Integer
						.parseInt(String.valueOf(event.getNewValue()))].setStyle("-fx-background-color: #FFA500");
				p2CurCol = Integer.parseInt(String.valueOf(event.getOldValue()));
				p2CurRow = Integer.parseInt(String.valueOf(event.getNewValue()));
				pCount++;
			}
		}

		if (event.getPropertyName().equals("newMove++")) {
			if (pCount % 2 == 0) {
				layout[p1CurCol][p1CurRow].setStyle(null);
				layout[Integer.parseInt(String.valueOf(event.getOldValue()))][Integer
						.parseInt(String.valueOf(event.getNewValue())) - 1].setStyle("-fx-background-color: #add8e6");
				p1CurCol = Integer.parseInt(String.valueOf(event.getOldValue()));
				p1CurRow = Integer.parseInt(String.valueOf(event.getNewValue())) - 1;
				pCount++;
			} else {
				layout[p2CurCol][p2CurRow].setStyle(null);
				layout[Integer.parseInt(String.valueOf(event.getOldValue()))][Integer
						.parseInt(String.valueOf(event.getNewValue())) - 1].setStyle("-fx-background-color: #FFA500");
				p2CurCol = Integer.parseInt(String.valueOf(event.getOldValue()));
				p2CurRow = Integer.parseInt(String.valueOf(event.getNewValue())) - 1;
				pCount++;
			}
		}

		if (event.getPropertyName().equals("newBarrier")) {
			layout[Integer.parseInt(String.valueOf(event.getOldValue()))][Integer
					.parseInt(String.valueOf(event.getNewValue()))].setStyle("-fx-background-color: #222222");
			layout[Integer.parseInt(String.valueOf(event.getOldValue()))][Integer
					.parseInt(String.valueOf(event.getNewValue()))].setDisable(true);
			pCount++;
		}

		if (event.getPropertyName().equals("rejected")) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Invalid Action");
			alert.setContentText("Only legal moves are allowed.");
			alert.showAndWait();
		}

		feedback.setText(myModel.getFeedback());
		feedback.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 14));
		feedback.setTextFill(Color.WHITE);
	}

	/**
	 * handle - recognizes when any button is pressed
	 */
	@Override
	public void handle(ActionEvent arg0) {
		if (arg0.getSource() == choices) {
			flag = true;
			if (choices.getValue() == "5x5") {
				myModel.setSize(5);
			} else if (choices.getValue() == "7x7") {
				myModel.setSize(7);
			} else {
				myModel.setSize(11);
			}
			pCount = 0;
		}

		if (flag) {
			for (int k = 0; k < myModel.getSize() * 2 + 1; k++) {
				for (int z = 0; z < myModel.getSize() * 2 + 1; z++) {
					if (arg0.getSource() == layout[k][z]) {
						if (k % 2 != 0 && z % 2 != 0) {
							myModel.player(k, z);
						} else {
							if (k % 2 == 0) {
								myModel.barrier(k - 1, z, k + 1, z);
							} else {
								myModel.barrier(k, z - 1, k, z + 1);
							}
						}
					}
				}
			}

			if (arg0.getSource() == reset) {
				myModel.setSize(myModel.getSize());
				pCount = 0;
			}
		}
	}

	/**
	 * changes - mandatory implemented method, unused for this project
	 */
	@Override
	public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
	}

}
