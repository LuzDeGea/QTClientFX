import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import keyboardinput.Keyboard;

public class MainFxTest extends Application {

	private static ObjectOutputStream out;
	private static ObjectInputStream in; // stream con richieste del client

	Stage finestra;
	Scene scene_menu, scene_file, scene_db, scene_error;
	Scene scene_result_file, scene_result_db;

	public static void main(final String[] args) {
		String ip = "127.0.0.1";// args[0];
		int port = 8080;// Integer.parseInt(args[1]);

		try {
			InetAddress addr = InetAddress.getByName(ip); // ip;
			Socket socket = new Socket(addr, port); // Port

			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		finestra = primaryStage;
		finestra.setTitle("QT");

		// Menu //
		Label label_menu = new Label("Seleiona una operazione:");
		GridPane grid_menu = new GridPane();
		grid_menu.setPadding(new Insets(10, 10, 10, 10));
		grid_menu.setVgap(8);
		grid_menu.setHgap(10);

		Button bottone_file = new Button("Carica i cluster da file");
		bottone_file.setOnAction(e -> finestra.setScene(scene_file));
		Button bottone_db = new Button("Carica dati dal Database");
		bottone_db.setOnAction(e -> finestra.setScene(scene_db));

		grid_menu.add(label_menu, 1, 0);
		grid_menu.add(bottone_file, 1, 1);
		grid_menu.add(bottone_db, 1, 2);
		scene_menu = new Scene(grid_menu, 360, 150);

		// pannello risultato file//
		GridPane grid_result_file = new GridPane();
		grid_result_file.setPadding(new Insets(10, 10, 10, 10));
		grid_result_file.setVgap(8);
		grid_result_file.setHgap(10);

		Image img_icon_house = new Image("file:res/images/house_icon.png", 20, 20, false, false);

		Button bottone_file_ripeti = new Button("Nuovo raggio");
		bottone_file_ripeti.setOnAction(e -> finestra.setScene(scene_file));
		bottone_file_ripeti.setWrapText(false);

		Button bottone_menu_file = new Button();
		bottone_menu_file.setOnAction(e -> finestra.setScene(scene_menu));
		bottone_menu_file.setWrapText(false);
		bottone_menu_file.setGraphic(new ImageView(img_icon_house));

		Button bottone_chiudi_file = new Button("Chiudi");
		bottone_chiudi_file.setOnAction(e -> finestra.close());
		bottone_chiudi_file.setWrapText(false);

		TextArea file_result = new TextArea(); // area risultato file
		file_result.setMinHeight(150);
		file_result.setMaxWidth(350);

		grid_result_file.add(bottone_menu_file, 1, 1);
		grid_result_file.add(file_result, 1, 2);
		grid_result_file.add(bottone_file_ripeti, 1, 3);
		grid_result_file.add(bottone_chiudi_file, 2, 3);

		scene_result_file = new Scene(grid_result_file, 450, 300);

		// pannello file (1) //
		Label label_file_name = new Label("Inserisci il nome della tabella: ");
		Label label_file_radius = new Label("Inserisci il raggio calcolato: ");
		GridPane grid_file = new GridPane();
		grid_file.setPadding(new Insets(10, 10, 10, 10));
		grid_file.setVgap(8);
		grid_file.setHgap(10);

		TextField file_name = new TextField("playtennis");
		TextField file_radius = new TextField("2");
		Button bottone_carica_file = new Button("Carica file");
		final Text actiontarget_file = new Text();

		bottone_carica_file.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if ((double) Integer.valueOf(file_radius.getText()) <= 0.0) {
					// se nome db o raggio sono errati
					actiontarget_file.setFill(Color.FIREBRICK);
					actiontarget_file.setText("Raggio errato.");
					grid_file.add(actiontarget_file, 2, 3);
				} else {
					// se va tutto bene
					try {
						String qt = learningFromFile(file_name.getText(), Integer.valueOf(file_radius.getText()));
						file_result.setText(qt);
						finestra.setScene(scene_result_file);
					} catch (SocketException err) {
						file_result.setText(err.getMessage());
						finestra.setScene(scene_result_file);
						return;
					} catch (FileNotFoundException err) {
						file_result.setText(err.getMessage());
						finestra.setScene(scene_result_file);
						return;
					} catch (IOException err) {
						file_result.setText(err.getMessage());
						finestra.setScene(scene_result_file);
						return;
					} catch (ClassNotFoundException err) {
						file_result.setText(err.getMessage());
						finestra.setScene(scene_result_file);
						return;
					} catch (ServerException err) {
						file_result.setText(err.getMessage());
						finestra.setScene(scene_result_file);
					}
				}
			}
		});

		grid_file.add(label_file_name, 1, 0);
		grid_file.add(file_name, 2, 0);
		grid_file.add(label_file_radius, 1, 2);
		grid_file.add(file_radius, 2, 2);
		grid_file.add(bottone_carica_file, 1, 3);
		scene_file = new Scene(grid_file, 360, 150);

		// pannello risultato db //
		GridPane grid_result_db = new GridPane();
		grid_result_db.setPadding(new Insets(10, 10, 10, 10));
		grid_result_db.setVgap(8);
		grid_result_db.setHgap(10);

		Button bottone_db_ripeti = new Button("Nuovo raggio");
		bottone_db_ripeti.setOnAction(e -> finestra.setScene(scene_db));
		bottone_db_ripeti.setWrapText(false);

		Button bottone_menu_db = new Button();
		bottone_menu_db.setOnAction(e -> finestra.setScene(scene_menu));
		bottone_menu_db.setWrapText(false);
		bottone_menu_db.setGraphic(new ImageView(img_icon_house));

		Button bottone_chiudi_db = new Button("Chiudi");
		bottone_chiudi_db.setOnAction(e -> finestra.close());
		bottone_chiudi_db.setWrapText(false);

		TextArea db_result = new TextArea(); // area risultato db
		db_result.setMinHeight(150);
		db_result.setMaxWidth(350);

		grid_result_db.add(bottone_menu_db, 1, 1);
		grid_result_db.add(db_result, 1, 2);
		grid_result_db.add(bottone_db_ripeti, 1, 3);
		grid_result_db.add(bottone_chiudi_db, 2, 3);

		scene_result_db = new Scene(grid_result_db, 450, 300);

		// pannello db (2) //
		Label label_db_name = new Label("Inserisci il nome del db: ");
		Label label_db_radius = new Label("Inserisci il raggio:");
		GridPane grid_db = new GridPane();
		grid_db.setPadding(new Insets(10, 10, 10, 10));
		grid_db.setVgap(8);
		grid_db.setHgap(10);

		TextField db_name = new TextField("playtennis");
		TextField db_radius = new TextField("2");
		Button bottone_carica_db = new Button("Calcola");
		final Text actiontarget_db = new Text();

		bottone_carica_db.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if ((double) Integer.valueOf(db_radius.getText()) <= 0.0) {
					// se nome db o raggio sono errati
					actiontarget_db.setFill(Color.FIREBRICK);
					actiontarget_db.setText("inserire Raggio > 0");
					grid_db.add(actiontarget_db, 2, 3);
				} else {
					// se va tutto bene
					try {
						storeTableFromDb(db_name.getText(), Integer.valueOf(db_radius.getText()));
					} catch (SocketException err) {
						db_result.setText(err.getMessage());
						finestra.setScene(scene_result_db);
					} catch (FileNotFoundException err) {
						db_result.setText(err.getMessage());
						finestra.setScene(scene_result_db);
					} catch (IOException err) {
						db_result.setText(err.getMessage());
						finestra.setScene(scene_result_db);
					} catch (ClassNotFoundException err) {
						db_result.setText(err.getMessage());
						finestra.setScene(scene_result_db);
					} catch (ServerException err) {
						db_result.setText(err.getMessage());
						finestra.setScene(scene_result_db);
					}
				}
			}
		});

		grid_db.add(label_db_name, 1, 0);
		grid_db.add(db_name, 2, 0);
		grid_db.add(label_db_radius, 1, 2);
		grid_db.add(db_radius, 2, 2);
		grid_db.add(bottone_carica_db, 1, 3);

		scene_db = new Scene(grid_db, 360, 150);

		// visulizzazione iniziale //
		finestra.setScene(scene_menu);
		finestra.show();

	}
	///////////////////////////////////////////////////////////

	private String learningFromFile(final String table_name, final double radius)
			throws SocketException, ServerException, IOException, ClassNotFoundException {
		out.writeObject(3);

		out.writeObject(table_name);
		out.writeObject(radius);

		String result = (String) in.readObject();
		if (result.equals("OK")) {
			return (String) in.readObject();
		} else {
			throw new ServerException(result);
		}

	}

	private void storeTableFromDb(final String table_name, final double radius)
			throws SocketException, ServerException, IOException, ClassNotFoundException {
		out.writeObject(0);

		out.writeObject(table_name);
		out.writeObject(radius);

		String result = (String) in.readObject();
		if (!result.equals("OK")) {
			throw new ServerException(result);
		}

	}

	private String learningFromDbTable() throws SocketException, ServerException, IOException, ClassNotFoundException {
		out.writeObject(1);
		double r = 1.0;
		do {
			System.out.print("Radius:");
			r = Keyboard.readDouble();
		} while (r <= 0);
		out.writeObject(r);
		String result = (String) in.readObject();
		if (result.equals("OK")) {
			System.out.println("Number of Clusters:" + in.readObject());
			return (String) in.readObject();
		} else {
			throw new ServerException(result);
		}

	}

	private void storeClusterInFile() throws SocketException, ServerException, IOException, ClassNotFoundException {
		out.writeObject(2);

		String result = (String) in.readObject();
		if (!result.equals("OK")) {
			throw new ServerException(result);
		}

	}

}
