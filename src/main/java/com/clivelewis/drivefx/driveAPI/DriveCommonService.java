package com.clivelewis.drivefx.driveAPI;

import com.clivelewis.drivefx.driveAPI.logging.ConsoleLoggingService;
import com.clivelewis.drivefx.driveAPI.logging.ILoggingService;
import com.clivelewis.drivefx.driveAPI.logging.LogLevel;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

public class DriveCommonService {
	private static final ILoggingService logger = new ConsoleLoggingService();
	
	
	public static FileList getAllFiles() throws IOException, GeneralSecurityException {
		return DriveAuthHandler.getDriveService().files().list().setPageSize(1000).setFields("files(id, kind, name, mimeType, ownedByMe, size, " +
				"hasThumbnail, thumbnailLink, createdTime, capabilities)").execute();
	}

	public static File getFileByName(String fileName) throws IOException, GeneralSecurityException {
		FileList allFiles = getAllFiles();
		Optional<File> optionalFile = allFiles.getFiles().stream().filter(file -> file.getName().equalsIgnoreCase(fileName)).findFirst();
		if(optionalFile.isPresent()){
			logger.log("File " + fileName + " found", LogLevel.INFO);
			return optionalFile.get();
		}else{
			logger.log("File " + fileName + " not found in the Drive", LogLevel.ERROR);
			return null;
		}
	}

	public static File getFileById(String fileId) throws IOException, GeneralSecurityException {
		FileList allFiles = getAllFiles();
		Optional<File> optionalFile = allFiles.getFiles().stream().filter(file -> file.getId().equalsIgnoreCase(fileId)).findFirst();
		if(optionalFile.isPresent()){
			logger.log("File with id" + fileId + " found", LogLevel.INFO);
			return optionalFile.get();
		}else{
			logger.log("File not found in the Drive", LogLevel.ERROR);
			return null;
		}
	}

	public static void createRootFolder(String folderName) throws IOException, GeneralSecurityException {
		File fileMetadata = new File();

		fileMetadata.setName(folderName);
		fileMetadata.setMimeType(Constants.DRIVE_FOLDER_TYPE);
		Drive driveService = DriveAuthHandler.getDriveService();

		File file = driveService.files().create(fileMetadata).setFields("id, name").execute();
		logger.log("Folder " + file.getName() + " created in the root directory.", LogLevel.INFO);
	}

	public static void displayAllContent() throws IOException, GeneralSecurityException {
		FileList fileList = getAllFiles();
		List<File> files = fileList.getFiles();
		logger.log("Total files found: " + files.size(), LogLevel.INFO);
		files.forEach(file -> {
			StringBuilder sb = new StringBuilder();
			sb.append("File: ").append(file.getName());
			sb.append("\nisFolder: ").append(file.getMimeType().contains(".folder")).append(" | ");
			if(!file.getMimeType().contains(".folder")){
				sb.append("Size: ").append(file.getSize()).append(" | ");
			}
			sb.append("ID: ").append(file.getId()).append(" | ");
			sb.append("Type: ").append(file.getMimeType()).append(" | ");
			sb.append("canDownload: ").append(file.getCapabilities().getCanDownload()).append(" | ");
			sb.append("ownedByMe: ").append(file.getOwnedByMe()).append(" | ");

			sb.append("\n___________________________");

			logger.log(sb.toString(), LogLevel.INFO);
		});

	}


}
