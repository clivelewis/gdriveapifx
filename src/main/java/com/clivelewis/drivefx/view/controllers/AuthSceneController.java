package com.clivelewis.drivefx.view.controllers;

import com.clivelewis.drivefx.driveAPI.DriveAuthHandler;
import com.clivelewis.drivefx.utils.FileUtils;
import com.clivelewis.drivefx.view.SceneService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AuthSceneController {


	@FXML
	private AnchorPane mainAnchorPane;

	@FXML
	private Button credentialsSelectButton;

	@FXML
	private Label pathLabel;

	@FXML
	private Button connectButton;

	@FXML
	private Label progressLabel;

	@FXML
	private ProgressIndicator progressIndicator;


	private File selectedFile;

	@FXML
	public void initialize(){
		mainAnchorPane.setMaxHeight(500);
		mainAnchorPane.setMaxWidth(600);
		progressLabel.setVisible(false);
		progressIndicator.setVisible(false);
		pathLabel.setText("");
	}

	@FXML
	void loadCredentials(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON file (*.json)","*.json"));
		selectedFile = fileChooser.showOpenDialog(mainAnchorPane.getScene().getWindow());

		if(selectedFile != null){
			pathLabel.setText(selectedFile.getAbsolutePath());
		}
	}
	@FXML
	void connectToDrive(ActionEvent event) {
		if(selectedFile == null){
			noFileAlert();
			return;
		}
		progressIndicator.setVisible(true);
		progressLabel.setText("Generating file...");
		progressLabel.setVisible(true);
		FileUtils.createCredentialsFile(selectedFile);
		try{
			progressLabel.setText("Connecting to Google Drive API...");
			handleConnectionToDrive();
		}catch (Exception e){
			progressLabel.setText("");
			progressIndicator.setVisible(false);
			loadingError();
		}

	}

	private void handleConnectionToDrive() {
		Task<Boolean> task = new Task<>() {
			@Override
			protected Boolean call() throws Exception {
				DriveAuthHandler.getDriveService();
				return true;
			}
		};

		task.setOnSucceeded(event -> {
			try {
				SceneService.goToMainScene(mainAnchorPane);
			} catch (IOException e) {
				progressLabel.setText("ERROR!");
				progressLabel.setTextFill(Color.RED);
				progressIndicator.setVisible(false);
				e.printStackTrace();
			}
		});

		new Thread(task).start();
	}

	private void noFileAlert() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("No credentials file");
		alert.setContentText("Please select json file with credentials");
		alert.show();
	}

	private void loadingError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setContentText("Something went wrong. Please make sure that you have selected" +
				" correct file and try again.");
		alert.show();
	}


}
