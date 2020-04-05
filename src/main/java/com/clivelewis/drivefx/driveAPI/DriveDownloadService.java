package com.clivelewis.drivefx.driveAPI;

import com.clivelewis.drivefx.driveAPI.logging.ConsoleLoggingService;
import com.clivelewis.drivefx.driveAPI.logging.ILoggingService;
import com.clivelewis.drivefx.driveAPI.logging.LogLevel;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;

public class DriveDownloadService {
	private static final ILoggingService logger = new ConsoleLoggingService();

	public static void downloadFile(File file) throws IOException, GeneralSecurityException {
		if(file != null){
			downloadFileById(file.getId());
		}else{
			logger.log("File is null. Can't download", LogLevel.ERROR);
		}
	}

	public static void downloadFile(File file, String folderName) throws IOException, GeneralSecurityException {
		if(file != null){
			downloadFileById(file.getId(), folderName);
		}else{
			logger.log("File is null. Can't download", LogLevel.ERROR);
		}
	}

	public static void downloadFileByName(String fileName) throws IOException, GeneralSecurityException {
		logger.log("Searching for file \"" + fileName + "\" in the Drive....", LogLevel.INFO);
		Optional<File> first = DriveCommonService.getAllFiles().getFiles()
				.stream().filter(file -> file.getName().equals(fileName)).findFirst();

		if(first.isPresent()){
			downloadFileById(first.get().getId());
		}else{
			logger.log("File with name " + fileName + " not found.", LogLevel.ERROR);
		}
	}

	public static void downloadFileById(String fileId, String folderName) throws IOException, GeneralSecurityException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		logger.log("Starting downloading file with ID " + fileId, LogLevel.INFO);
		File driveFile = DriveAuthHandler.getDriveService().files().get(fileId).execute();

		if(driveFile.getMimeType().contains(".folder")){
			logger.log("This ID represents a directory. Use downloadFolderById method", LogLevel.ERROR);
			return;
		}

		logger.log("File name: " + driveFile.getName() + " | Size: " + driveFile.getSize(), LogLevel.INFO);


		if(isExportFile(driveFile)){
			if (driveFile.getMimeType().equals(Constants.DRIVE_DOCUMENT_TYPE)) {
				DriveAuthHandler.getDriveService().files().export(fileId, "application/vnd.oasis.opendocument.text")
						.executeMediaAndDownloadTo(outputStream);
			}else{
				DriveAuthHandler.getDriveService().files().export(fileId, "application/x-vnd.oasis.opendocument.spreadsheet")
						.executeMediaAndDownloadTo(outputStream);
			}
		}else{
			DriveAuthHandler.getDriveService().files().get(fileId)
					.executeMediaAndDownloadTo(outputStream);
		}

		logger.log("Saved to byte stream...", LogLevel.INFO);

		String fullPath;

		if(folderName == null || folderName.isEmpty()){
			fullPath = Constants.DOWNLOAD_DIRECTORY + driveFile.getName();
		}else{
			fullPath = Constants.DOWNLOAD_DIRECTORY + folderName + java.io.File.separator + driveFile.getName();
		}

		java.io.File newFile = new java.io.File(fullPath);
		newFile.getParentFile().mkdirs();

		if(!newFile.createNewFile()){
			logger.log("File already exists! Aborting.", LogLevel.WARNING);
		}else{
			FileOutputStream fos = new FileOutputStream(newFile);
			outputStream.writeTo(fos);
			logger.log("File saved to " + newFile.getAbsolutePath(), LogLevel.INFO);

			fos.flush();
			fos.close();
			outputStream.close();
		}
	}

	public static void downloadFileById(String fileId) throws IOException, GeneralSecurityException {
		downloadFileById(fileId, null);
	}

	public static void downloadFolderByName(String folderName) throws IOException, GeneralSecurityException {
		Optional<File> first = DriveCommonService.getAllFiles().getFiles()
				.stream().filter(file -> file.getName().equals(folderName)).findFirst();

		if(first.isPresent()){
			logger.log("Folder with name '" + folderName + "' found.", LogLevel.INFO);
			downloadFolderById(first.get().getId());
		}else{
			logger.log("Folder with name '" + folderName + "' not found.", LogLevel.ERROR);
			return;
		}
	}

	private static void downloadFolderById(String folderId, String parentFolderName) throws IOException, GeneralSecurityException {
		Optional<File> mainFolder = DriveCommonService.getAllFiles().getFiles()
				.stream().filter(file -> file.getId().equals(folderId)).findFirst();
		if(!mainFolder.isPresent()){
			logger.log("Folder with ID " + folderId + " not found.", LogLevel.ERROR);
			return;
		}

		if(!mainFolder.get().getMimeType().contains(".folder")){
			logger.log("This is a file, not a folder. Use one of the downloadFile methods.", LogLevel.ERROR);
			return;
		}

		logger.log("Folder " + mainFolder.get().getName() + " found.", LogLevel.INFO);
		logger.log("Starting folder content download...", LogLevel.INFO);
		FileList execute = DriveAuthHandler.getDriveService().files().list().setQ("'" + folderId + "'" + " in parents").execute();
		List<File> files = execute.getFiles();
		for (File file : files) {
			if(file.getMimeType().contains(".folder")){
				downloadFolderById(file.getId(), mainFolder.get().getName());
			}else{
				if(parentFolderName != null && !parentFolderName.isEmpty()){
					downloadFile(file, parentFolderName + java.io.File.separator + mainFolder.get().getName());
				}else{
					downloadFile(file, mainFolder.get().getName());
				}
			}
		}
	}
	public static void downloadFolderById(String folderId) throws IOException, GeneralSecurityException {
		downloadFolderById(folderId, null);
	}

	private static boolean isExportFile(File driveFile) {
		String mimeType = driveFile.getMimeType();
		return Constants.EXPORT_TYPES.contains(mimeType);
	}

}
