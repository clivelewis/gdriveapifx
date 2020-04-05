package com.clivelewis.drivefx;

import com.clivelewis.drivefx.driveAPI.DriveAuthHandler;
import com.clivelewis.drivefx.driveAPI.DriveCommonService;
import com.google.api.services.drive.model.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main extends Application {
    private Scene startupScene;
    private boolean isStartupResizable = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Google Drive Api");
        primaryStage.setResizable(isStartupResizable);
        primaryStage.setMinHeight(startupScene.getHeight());
        primaryStage.setMinWidth(startupScene.getWidth());
        primaryStage.setScene(startupScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        Path path = Paths.get("tokens/StoredCredential");
        if(path.toFile().exists()){
            isStartupResizable = true;
            Parent root = FXMLLoader.load(getClass().getResource("view/fxml/MainScene.fxml"));
            startupScene = new Scene(root, 1200, 850);

        }else{
            isStartupResizable = false;
            Parent root = FXMLLoader.load(getClass().getResource("view/fxml/AuthScene.fxml"));
            startupScene = new Scene(root, 600, 500);
        }

    }
}
