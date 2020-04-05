package com.clivelewis.drivefx.utils;

import com.clivelewis.drivefx.view.Model.DriveFileModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class FileUtils {
	public static File getFile(String filePath){
		Path path = Paths.get(filePath);
		if(path.toFile().exists()){
			return path.toFile();
		}else{
			return null;
		}
	}

	public static void createCredentialsFile(File file){
		Path resourcesFolder = Paths.get(FileUtils.class.getResource("/").getPath());
		Path resourcePath = Paths.get(resourcesFolder.toAbsolutePath() + File.separator + "credentials.json");

		try (FileInputStream fis = new FileInputStream(file);
		   FileOutputStream fos = new FileOutputStream(resourcePath.toFile())) {
		    int len;
		    byte[] buffer = new byte[4096];
		    while ((len = fis.read(buffer)) > 0) {
		        fos.write(buffer, 0, len);
		    }
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public static DriveFileModel getDriveFileModel(com.google.api.services.drive.model.File file) {
		DriveFileModel driveFile = new DriveFileModel();
		driveFile.setId(file.getId());
		driveFile.setName(file.getName());
		driveFile.setSize(file.getSize());
		driveFile.setOwnedByMe(file.getOwnedByMe());
		driveFile.setType(file.getMimeType());
		driveFile.setDownloadable(file.getCapabilities().getCanDownload());
		driveFile.setHasThumbnail(file.getHasThumbnail());
		driveFile.setThumbnailUrl(file.getThumbnailLink());
		driveFile.setTimeCreated(new Date(file.getCreatedTime().getValue()));
		return driveFile;
	}
}
