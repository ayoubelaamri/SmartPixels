package application;
	
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class App extends Application {
	
	static ConnectDB connection;
	static Stage window;
	static Scene LandingScene, AppScene;
	static BorderPane root;
	static Parent landing, search, similar, manage, insert, about, team;
	
	 
	@Override
	public void start(Stage primaryStage) {
		
		window = primaryStage;
		
		try {
			search = FXMLLoader.load(getClass().getResource("/application/SearchEngine.fxml"));
			manage = FXMLLoader.load(getClass().getResource("/application/ManageDatabase.fxml"));
			insert = FXMLLoader.load(getClass().getResource("/application/InsertImage.fxml"));
			about = FXMLLoader.load(getClass().getResource("/application/About.fxml"));
			team = FXMLLoader.load(getClass().getResource("/application/Team.fxml"));
					
			landing = FXMLLoader.load(getClass().getResource("/application/Landing.fxml"));
			LandingScene = new Scene(landing,700,500);
			LandingScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
			root = FXMLLoader.load(getClass().getResource("/application/Root.fxml"));
			AppScene = new Scene(root,700,500);
			AppScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			window.initStyle(StageStyle.UNDECORATED);
			window.setScene(LandingScene);
			window.setTitle("Smart Pixels - Images processing App");
			window.setX(280);
			window.setY(100);
			window.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			connection = new ConnectDB();
			System.out.println("Checking driver : OK.");
			System.out.println("Establishing connection : OK.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		launch(args);
	}
}
