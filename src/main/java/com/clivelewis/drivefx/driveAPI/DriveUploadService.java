package com.clivelewis.drivefx.driveAPI;

import com.clivelewis.drivefx.driveAPI.logging.ConsoleLoggingService;
import com.clivelewis.drivefx.driveAPI.logging.ILoggingService;
import com.clivelewis.drivefx.driveAPI.logging.LogLevel;
import com.clivelewis.drivefx.utils.FileUtils;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class DriveUploadService {
	private static final ILoggingService logger = new ConsoleLoggingService();

	public static void uploadFile(String filePath, String driveFolderId) throws IOException, GeneralSecurityException {
		java.io.File file = FileUtils.getFile(filePath);
		if(file == null) {
			logger.log("File not found. Path: " + filePath, LogLevel.ERROR);
			return;
		}
		File fileMetadata = new File();
		fileMetadata.setName(file.getName());
		fileMetadata.setParents(Collections.singletonList(driveFolderId));

		uploadFile(file, fileMetadata);


	}
	public static void uploadFile(String filePath) throws IOException, GeneralSecurityException {
		java.io.File file = FileUtils.getFile(filePath);
		if(file == null){
			logger.log("File not found. Path: " + filePath, LogLevel.ERROR);
			return;
		}else{
			uploadFile(file);
		}
	}

	public static void uploadFile(java.io.File file) throws IOException, GeneralSecurityException {
		if(file == null){
			logger.log("File is NULL. Can't upload", LogLevel.ERROR);
			return;
		}

		logger.log("File Found. Preparing...", LogLevel.INFO);
		File fileMetadata = new File();
		fileMetadata.setName(file.getName());

		uploadFile(file, fileMetadata);

	}

	private static void uploadFile(java.io.File file, File fileMetadata) throws IOException, GeneralSecurityException {
		FileContent fileContent = new FileContent("",file);
		logger.log("Starting file upload...", LogLevel.INFO);

		File uploadedFile = DriveAuthHandler.getDriveService().files().create(fileMetadata, fileContent)
				.setFields("id, name").execute();

		logger.log(uploadedFile.getName() + " uploaded. ID: " + uploadedFile.getId(), LogLevel.INFO);
	}
}
