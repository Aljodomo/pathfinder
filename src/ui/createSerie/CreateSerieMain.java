package ui.createSerie;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class CreateSerieMain {

	Stage window;

	CreateSerieController controller;

	public void start() {
		this.window = new Stage();
		this.window.setOnCloseRequest(event -> {
			this.controller.handleCancel();
		});
		this.mainWindow();
	}

	public void mainWindow() {
		try {
			URL u = CreateSerieMain.class.getResource("CreateSerieView.fxml");
			FXMLLoader loader = new FXMLLoader(u);
			TabPane pane = (TabPane) loader.load();

			controller = loader.getController();
			controller.setMain(this);
			controller.ini();

			Scene scene = new Scene(pane);

			this.window.setScene(scene);
			this.window.show();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public static void main(String[] args) {
	// launch();
	// }
}
