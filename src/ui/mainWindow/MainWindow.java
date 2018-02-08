package ui.mainWindow;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.MainTVSM;

public class MainWindow extends Application {

	protected Stage primaryStage;
	public MainWindowController controller;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.mainWindow();
	}

	@Override
	public void stop() {
		MainTVSM.save();
		try {
			super.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void setNotCloseable(boolean value){
		Platform.setImplicitExit(false);
		this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		        event.consume();
		    }
		});
	}

	public void mainWindow() {
		try {
			URL u = MainWindow.class.getResource("MainWindowView.fxml");
			FXMLLoader loader = new FXMLLoader(u);
			AnchorPane pane = (AnchorPane) loader.load();

			controller = loader.getController();
			controller.setMain(this);
			controller.ini();

			Scene scene = new Scene(pane);

			this.primaryStage.setScene(scene);
			this.primaryStage.show();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
