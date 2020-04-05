package com.clivelewis.drivefx.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneService {

	public static void goToMainScene(Node node) throws IOException {
		Parent root = FXMLLoader.load(SceneService.class.getResource("fxml/MainScene.fxml"));
		Scene scene = new Scene(root, 1200, 850);
		Stage stage = (Stage) node.getScene().getWindow();
		stage.setResizable(true);
		stage.setMinWidth(1200);
		stage.setMinHeight(850);
		stage.setScene(scene);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		stage.show();
	}
}
