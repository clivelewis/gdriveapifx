package com.clivelewis.drivefx.view.controllers;

import com.clivelewis.drivefx.driveAPI.DriveCommonService;
import com.clivelewis.drivefx.driveAPI.DriveDownloadService;
import com.clivelewis.drivefx.utils.FileUtils;
import com.clivelewis.drivefx.view.Model.DriveFileModel;
import com.google.api.services.drive.model.File;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainSceneController implements Initializable {

	@FXML private AnchorPane anchorPane;
	@FXML private TableView<DriveFileModel> tableView;
	@FXML private TableColumn<String, DriveFileModel> nameColumn;
	@FXML private TableColumn<String, DriveFileModel> sizeColumn;
	@FXML private TableColumn<String, DriveFileModel> typeColumn;
	@FXML private TableColumn<String, DriveFileModel> downloadableColumn;
	@FXML private TableColumn<String, DriveFileModel> ownerColumn;
	@FXML private TableColumn<String, DriveFileModel> createdColumn;

	@FXML private TextField textFieldItemName;
	@FXML private TextField textFieldSelectionId;
	@FXML private TextField textFieldSelectionType;
	@FXML private TextField textFieldSelectionSize;
	@FXML private TextField textFieldSelectionIsDownload;
	@FXML private TextField textFieldSelectionIsOwner;
	@FXML private Button buttonDownload;
	@FXML private ImageView imageViewThumbnail;
	@FXML private ProgressIndicator progressIndicator;
	@FXML private Label labelDownloadProgress;

	private List<DriveFileModel> driveFileList = new ArrayList<>();
	private boolean isDownloadInProgress = false;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		progressIndicator.setVisible(false);
		setupTableView();
		fetchFilesAndPopulateList();
	}


	private void showSelectedItemInfo(DriveFileModel item) {
		textFieldItemName.clear();
		textFieldItemName.setText(item.getName());
		textFieldSelectionId.setText(item.getId());
		textFieldSelectionType.setText(item.getType());
		textFieldSelectionSize.setText(item.getSize());
		textFieldSelectionIsDownload.setText(String.valueOf(item.isDownloadable()));
		textFieldSelectionIsOwner.setText(String.valueOf(item.isOwnedByMe()));

		if(item.getHasThumbnail()){
			imageViewThumbnail.setImage(new Image(item.getThumbnailUrl()));
		}else{
			imageViewThumbnail.setImage(null);
		}

		if(item.isFolder()) buttonDownload.setText("Download Folder");
		else buttonDownload.setText("Download");
	}

	private void fetchFilesAndPopulateList(){
		Task<List<File>> task = new Task<>() {
			@Override
			protected List<File> call() throws Exception {
				return DriveCommonService.getAllFiles().getFiles();
			}
		};
		task.setOnSucceeded(event -> {
			task.getValue().forEach(file -> {
				DriveFileModel driveFile = FileUtils.getDriveFileModel(file);
				driveFileList.add(driveFile);
				tableView.getItems().add(driveFile);
			});
			System.out.println("Done");
		});
		new Thread(task).start();
	}

	private void setupTableView() {
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		downloadableColumn.setCellValueFactory(new PropertyValueFactory<>("downloadable"));
		sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
		ownerColumn.setCellValueFactory(new PropertyValueFactory<>("ownedByMe"));
		createdColumn.setCellValueFactory(new PropertyValueFactory<>("formattedTimeCreated"));

		nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.30));
		typeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.30));
		downloadableColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
		sizeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
		ownerColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
		createdColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));

		tableView.setPlaceholder(new Label("Loading information..."));
		tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(tableView.getSelectionModel().getSelectedItem() != null){
				DriveFileModel selectedItem = tableView.getSelectionModel().getSelectedItem();
				showSelectedItemInfo(selectedItem);
			}
		});
	}

	@FXML
	void download(ActionEvent event) {
		if(isDownloadInProgress) return;

		DriveFileModel item = tableView.getSelectionModel().getSelectedItem();
		if(item == null) return;
		Task<Void> downloadTask = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				isDownloadInProgress = true;
				if(item.isFolder()) DriveDownloadService.downloadFolderById(item.getId());
				else DriveDownloadService.downloadFileById(item.getId());
				return null;
			}
		};

		downloadTask.setOnRunning(runningEvent -> {
			labelDownloadProgress.setText("Downloading...");
			progressIndicator.setVisible(true);

		});

		downloadTask.setOnSucceeded(successEvent -> {
			isDownloadInProgress = false;

			progressIndicator.setVisible(false);
			labelDownloadProgress.setTextFill(Color.GREEN);
			labelDownloadProgress.setText("Success!");

			PauseTransition visiblePause = new PauseTransition();
			visiblePause.setDuration(Duration.seconds(3));
			visiblePause.setOnFinished(finished -> {
				labelDownloadProgress.setTextFill(Color.WHITE);
				labelDownloadProgress.setText("");
			});

			visiblePause.play();
		});

		downloadTask.setOnFailed(failEvent -> {
			isDownloadInProgress = false;

			progressIndicator.setVisible(false);
			labelDownloadProgress.setTextFill(Color.RED);
			labelDownloadProgress.setText("Error. Please try again.");

			PauseTransition visiblePause = new PauseTransition();
			visiblePause.setDuration(Duration.seconds(3));
			visiblePause.setOnFinished(finished -> {
				labelDownloadProgress.setTextFill(Color.WHITE);
				labelDownloadProgress.setText("");
			});

			visiblePause.play();
		});

		new Thread(downloadTask).start();
	}
}

