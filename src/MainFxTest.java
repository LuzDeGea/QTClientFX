import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainFxTest extends Application {

	Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in; // stream con richieste del client

	Stage finestra;
	Scene scene_menu, scene_file, scene_db;
	Scene scene_result_file, scene_result_db, scene_error, scene_server;

	public static void main(final String[] args) {
		launch(args);
	}

/////
	public void connetti(final String ip, final int port) throws IOException {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				System.err.println("Errrore di collegamento al server remoto.");
			}
		}

		// può generare l'eccezione ma la gestisco dove è richiamato connetti
		InetAddress addr = InetAddress.getByName(ip); // ip;
		socket = new Socket(addr, port); // Port

		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());

	}

	@Override
	public void start(final Stage primaryStage) {
		try {
			connetti("127.0.0.1", 8080);
		} catch (IOException e4) {
			e4.printStackTrace();
		}

		finestra = primaryStage;
		finestra.setTitle("QT");
		finestra.getIcons().add(new Image("file:res/images/program_icon.png"));

		// barra menu //

		MenuBar barra_menu = new MenuBar();

		Menu edit_menu = new Menu("Edit");
		Menu help_menu = new Menu("Help");

		Image img_icon_internet = new Image("file:res/images/internet_icon.png", 8, 8, false, false);
		edit_menu.setGraphic(new ImageView(img_icon_internet));

		Menu edit_subMenu = new Menu("Cambia Server");
		MenuItem menuItem1_1 = new MenuItem("localhost (consigliato)");
		MenuItem menuItem1_2 = new MenuItem("nuovo server");
		edit_subMenu.getItems().addAll(menuItem1_1, menuItem1_2);
		edit_menu.getItems().add(edit_subMenu);

		MenuItem help_subMenu1 = new MenuItem("Info");
		MenuItem help_subMenu2 = new MenuItem("Credits");
		help_menu.getItems().addAll(help_subMenu1, help_subMenu2);

		menuItem1_1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				if (socket.getInetAddress().toString().equals("/127.0.0.1")) {
					Alert info = new Alert(AlertType.NONE, "Sei già sul localhost", ButtonType.OK);
					info.show();
				} else {
					String ip = "127.0.0.1";
					int port = 8080;
					try {
						connetti(ip, port);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					Alert info = new Alert(AlertType.NONE, "Server set:\n ip: " + ip + "\n port: " + port + " ",
							ButtonType.OK);
					info.show();
				}
			}
		});

		menuItem1_2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				GridPane grid_server = new GridPane();
				grid_server.setPadding(new Insets(10, 10, 10, 10));
				grid_server.setVgap(12);
				grid_server.setHgap(10);
				grid_server.setAlignment(Pos.CENTER);

				Label label_server_ip = new Label("Inserisci il nuovo ip: ");
				Label label_server_port = new Label("Inserisci la porta: ");
				TextField server_ip = new TextField("127.0.0.1");
				TextField server_port = new TextField("8080");
				// forza ad inserire solo numeri (max. 5 cifre)
				server_port.textProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(final ObservableValue<? extends String> observable, final String oldValue,
							final String newValue) {
						if (!newValue.matches("\\d{0,5}()?")) {
							server_port.setText(oldValue);
						}
					}
				});
				Button bottone_menu_server = new Button();

				Image img_icon_house = new Image("file:res/images/house_icon.png", 20, 20, false, false);
				bottone_menu_server.setOnAction(e2 -> finestra.setScene(scene_menu));
				bottone_menu_server.setWrapText(false);
				bottone_menu_server.setGraphic(new ImageView(img_icon_house));
				bottone_menu_server.setStyle("-fx-border-color: #000000; -fx-background-color: #00ff00");

				Button bottone_cambia_server = new Button("Cambia Server");
				bottone_cambia_server.setOnAction(e3 -> {
					if ((Integer.parseInt(server_port.getText()) < 1)
							|| (Integer.parseInt(server_port.getText()) > 67000)) {
						final Text actiontarget_ser = new Text();

						actiontarget_ser.setFill(Color.FIREBRICK);
						actiontarget_ser.setText("Porta errata");
						grid_server.add(actiontarget_ser, 2, 0);
					} else {
						String ip = server_ip.getText();
						int port = Integer.parseInt(server_port.getText());
						try {
							connetti(ip, port);
							Alert info_c = new Alert(AlertType.NONE,
									"Server set:\n ip: " + ip + "\n port: " + port + " ", ButtonType.OK);
							finestra.setScene(scene_menu);
							info_c.show();
						} catch (UnknownHostException ex) {
							ip = "127.0.0.1";
							port = 8080;
							try {
								connetti(ip, port);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							Alert info_c = new Alert(
									AlertType.NONE, "Impossibile connettersi al Server\n"
											+ "Sei stato riportato al localhost\n ip: " + ip + "\n port: " + port + " ",
									ButtonType.OK);
							finestra.setScene(scene_menu);
							info_c.show();
						} catch (Exception ex2) {
							ex2.printStackTrace();
						}
					}

				});

				grid_server.add(label_server_ip, 1, 1);
				grid_server.add(server_ip, 2, 1);
				grid_server.add(label_server_port, 1, 2);
				grid_server.add(server_port, 2, 2);
				grid_server.add(bottone_menu_server, 1, 0);
				grid_server.add(bottone_cambia_server, 2, 3);

				scene_server = new Scene(grid_server, 400, 200);
				scene_server.getStylesheets().add("StyleProgram.css");
				finestra.setScene(scene_server);
			}//
		});

		help_subMenu1.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				TextArea info_t = new TextArea("a");

				Alert info = new Alert(AlertType.NONE, info_t.getText(), ButtonType.OK);
				info.show();
			}
		});

		help_subMenu2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				TextArea credits_t = new TextArea("a");

				Alert credits = new Alert(AlertType.NONE, credits_t.getText(), ButtonType.OK);
				credits.show();
			}
		});

		barra_menu.getMenus().addAll(edit_menu, help_menu);

		BorderPane root_menu = new BorderPane();
		root_menu.setTop(barra_menu);

		// pannello menu //
		Label label_menu = new Label("Seleiona una operazione:");
		GridPane grid_menu = new GridPane();
		grid_menu.setPadding(new Insets(10, 10, 10, 10));
		grid_menu.setVgap(12);
		grid_menu.setHgap(10);
		grid_menu.setAlignment(Pos.CENTER);

		Button bottone_file = new Button("Carica i cluster da file");
		bottone_file.setOnAction(e -> finestra.setScene(scene_file));
		Button bottone_db = new Button("Carica dati dal Database");
		bottone_db.setOnAction(e -> finestra.setScene(scene_db));

		grid_menu.add(label_menu, 1, 0);
		grid_menu.add(bottone_file, 1, 1);
		grid_menu.add(bottone_db, 1, 2);
		root_menu.setCenter(grid_menu);
		scene_menu = new Scene(root_menu, 360, 180);
		scene_menu.getStylesheets().add("StyleProgram.css");

		// pannello di errore //

		Label label_error = new Label("Errore:");
		label_error.setStyle("-fx-text-fill: #ff0000;");
		GridPane grid_result_error = new GridPane();
		grid_result_error.setPadding(new Insets(10, 10, 10, 10));
		grid_result_error.setVgap(8);
		grid_result_error.setHgap(10);

		Image img_icon_error = new Image("file:res/images/error_icon.png", 20, 20, false, false);
		ImageView imageViewError = new ImageView(img_icon_error);

		Button bottone_chiudi_error = new Button("Chiudi il programma", imageViewError);
		bottone_chiudi_error.setOnAction(e -> finestra.close());
		bottone_chiudi_error.setWrapText(false);
		bottone_chiudi_error.setStyle(" -fx-text-fill: #ff0000; -fx-border-color: #ff0000; -fx-border-width: 2px;");

		TextArea error_result = new TextArea(); // area risultato error
		error_result.setMinHeight(50);
		error_result.setMaxWidth(350);
		error_result.setEditable(false);

		grid_result_error.add(label_error, 1, 1);
		grid_result_error.add(error_result, 1, 2);
		grid_result_error.add(bottone_chiudi_error, 1, 3);

		grid_result_error.setStyle("-fx-background-color: rgb(248,170,175);");

		scene_error = new Scene(grid_result_error, 360, 230);

		// pannello risultato file//
		GridPane grid_result_file = new GridPane();
		grid_result_file.setPadding(new Insets(10, 10, 10, 10));
		grid_result_file.setVgap(8);
		grid_result_file.setHgap(10);

		Button bottone_file_ripeti = new Button("Nuovo raggio");
		bottone_file_ripeti.setOnAction(e -> finestra.setScene(scene_file));
		bottone_file_ripeti.setWrapText(false);

		Image img_icon_house = new Image("file:res/images/house_icon.png", 20, 20, false, false);
		Button bottone_menu_file = new Button();
		bottone_menu_file.setOnAction(e -> finestra.setScene(scene_menu));
		bottone_menu_file.setWrapText(false);
		bottone_menu_file.setGraphic(new ImageView(img_icon_house));
		bottone_menu_file.setStyle("-fx-border-color: #000000; -fx-background-color: #00ff00");

		Button bottone_chiudi_file = new Button("Chiudi");
		bottone_chiudi_file.setOnAction(e -> finestra.close());
		bottone_chiudi_file.setWrapText(false);
		bottone_chiudi_file.setStyle(" -fx-text-fill: rgb(248,107,111)");

		TextArea file_result = new TextArea(); // area risultato file
		file_result.setMinHeight(150);
		file_result.setMaxWidth(350);
		file_result.setEditable(false);

		grid_result_file.add(bottone_menu_file, 1, 1);
		grid_result_file.add(file_result, 1, 2);
		grid_result_file.add(bottone_file_ripeti, 1, 3);
		grid_result_file.add(bottone_chiudi_file, 2, 3);

		scene_result_file = new Scene(grid_result_file, 450, 300);
		scene_result_file.getStylesheets().add("StyleProgram.css");

		// pannello file (1) //
		Label label_file_name = new Label("Inserisci il nome della tabella: ");
		Label label_file_radius = new Label("Inserisci il raggio calcolato: ");
		GridPane grid_file = new GridPane();
		grid_file.setPadding(new Insets(10, 10, 10, 10));
		grid_file.setVgap(8);
		grid_file.setHgap(10);

		TextField file_name = new TextField("playtennis");
		TextField file_radius = new TextField("2");
		file_radius.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> observable, final String oldValue,
					final String newValue) {
				if (!newValue.matches("\\d{0,4}([\\.]\\d{0,3})?")) {
					file_radius.setText(oldValue);
				}
			}
		});

		Button bottone_carica_file = new Button("Carica file");
		final Text actiontarget_file = new Text();

		bottone_carica_file.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent e) {
				handle2();
			}

			public void handle2() {
				if (Double.valueOf(file_radius.getText()) <= 0.0) {
					// se nome db o raggio sono errati
					actiontarget_file.setFill(Color.FIREBRICK);
					actiontarget_file.setText("Raggio errato.");
					grid_file.add(actiontarget_file, 2, 3);
				} else {
					// se va tutto bene
					String ip = socket.getInetAddress().toString().substring(1);
					int port = socket.getPort();
					try {
						String qt = learningFromFile(file_name.getText(), Double.valueOf(file_radius.getText()));
						file_result.setText(qt);
						finestra.setScene(scene_result_file);
					} catch (SocketException err) {
						try {
							finestra.setScene(scene_file);
							connetti(ip, port);
							handle2();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						return;
					} catch (FileNotFoundException | EOFException err) {
						Alert file_ex = new Alert(AlertType.NONE, "File non presente nel sistema.", ButtonType.OK);
						file_ex.show();
						finestra.setScene(scene_file);
						return;
					} catch (IOException err) {
						error_result.setText("(401) Errore di inserimento raggio, \n        Raggio troppo grande. ");
						finestra.setScene(scene_error);
						return;
					} catch (ClassNotFoundException err) {
						error_result.setText("(400) Errore per Classe non trovata \n");
						finestra.setScene(scene_error);
						return;
					} catch (ServerException err) {
						error_result.setText("(503) Errore dal server \n");
						finestra.setScene(scene_error);
						return;
					} catch (Error err) {
						error_result.setText("Errore generico \n");
						finestra.setScene(scene_error);
					}
				}
			}

		});

		grid_file.add(label_file_name, 1, 0);
		grid_file.add(file_name, 2, 0);
		grid_file.add(label_file_radius, 1, 2);
		grid_file.add(file_radius, 2, 2);
		grid_file.add(bottone_carica_file, 2, 3);
		grid_file.setAlignment(Pos.CENTER);

		scene_file = new Scene(grid_file, 450, 170);
		scene_file.getStylesheets().add("StyleProgram.css");

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
		bottone_menu_db.setStyle("-fx-border-color: #000000; -fx-background-color: #00ff00");

		Button bottone_chiudi_db = new Button("Chiudi");
		bottone_chiudi_db.setOnAction(e -> finestra.close());
		bottone_chiudi_db.setWrapText(false);
		bottone_chiudi_db.setStyle(" -fx-text-fill: rgb(248,107,111)");

		TextArea db_result = new TextArea(); // area risultato db
		db_result.setMinHeight(150);
		db_result.setMaxWidth(350);
		db_result.setEditable(false);

		grid_result_db.add(bottone_menu_db, 1, 1);
		grid_result_db.add(db_result, 1, 2);
		grid_result_db.add(bottone_db_ripeti, 1, 3);
		grid_result_db.add(bottone_chiudi_db, 2, 3);

		scene_result_db = new Scene(grid_result_db, 450, 300);
		scene_result_db.getStylesheets().add("StyleProgram.css");

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
				if (Double.valueOf(db_radius.getText()) <= 0.0) {
					// se nome db o raggio sono errati
					actiontarget_db.setFill(Color.FIREBRICK);
					actiontarget_db.setText("inserire Raggio > 0");
					grid_db.add(actiontarget_db, 2, 3);
				} else {
					// se va tutto bene
					try {
						storeTableFromDb(db_name.getText());

						String clusterSet = learningFromDbTable(Double.valueOf(db_radius.getText()));
						db_result.setText(clusterSet);

						finestra.setScene(scene_result_db);
						storeClusterInFile();

					} catch (SocketException err) {
						error_result.setText("(500) Probelma di collegamento. \n");
						finestra.setScene(scene_error);
						return;
					} catch (FileNotFoundException err) {
						error_result.setText("(404) File non trovato. \n");
						finestra.setScene(scene_error);
						return;
					} catch (IOException err) {
						error_result.setText("(401) Errore di inserimento raggio, \n           Raggio troppo grande. ");
						finestra.setScene(scene_error);
						return;
					} catch (ClassNotFoundException err) {
						error_result.setText("(400) Errore per Classe non trovata \n");
						finestra.setScene(scene_error);
						return;
					} catch (ServerException err) {
						error_result.setText("(503) Errore dal server \n");
						finestra.setScene(scene_error);
						return;
					} catch (Error err) {
						error_result.setText("Errore generico \n");
						finestra.setScene(scene_error);
					}
				}
			}
		});

		grid_db.add(label_db_name, 1, 0);
		grid_db.add(db_name, 2, 0);
		grid_db.add(label_db_radius, 1, 2);
		grid_db.add(db_radius, 2, 2);
		grid_db.add(bottone_carica_db, 2, 3);
		grid_db.setAlignment(Pos.CENTER);

		scene_db = new Scene(grid_db, 450, 170);
		scene_db.getStylesheets().add("StyleProgram.css");

		/*
		 * In caso la finestra del programma venga chiusa, verra chiusa con essa anche
		 * la socket.
		 */
		finestra.setOnCloseRequest(event -> {
			try {
				socket.close();
				System.err.println("Programma terminato");
				System.out.println("Socket chiusa con successo.");
			} catch (IOException e1) {
				System.err.println("Programma terminato");
				System.err.println("Probemi di chiusura della socket.");
				finestra.setScene(scene_error);
			}
		});
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

	private void storeTableFromDb(final String table_name)
			throws SocketException, ServerException, IOException, ClassNotFoundException {
		out.writeObject(0);

		out.writeObject(table_name);

		String result = (String) in.readObject();
		if (!result.equals("OK")) {
			throw new ServerException(result);
		}

	}

	private String learningFromDbTable(final double radius)
			throws SocketException, ServerException, IOException, ClassNotFoundException {
		out.writeObject(1);

		out.writeObject(radius);

		String result = (String) in.readObject();
		if (result.equals("OK")) {
			return "Number of Clusters: " + in.readObject() + "\n" + (String) in.readObject();
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

/////
}