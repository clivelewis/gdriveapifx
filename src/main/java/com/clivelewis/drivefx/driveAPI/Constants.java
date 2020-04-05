package com.clivelewis.drivefx.driveAPI;

import java.util.Arrays;
import java.util.List;

public class Constants {
	public static final String DOWNLOAD_DIRECTORY = "/home/clive95/repos/Personal/GDriveApiJavaFX/Downloads/";


	public static final String DRIVE_FOLDER_TYPE = "application/vnd.google-apps.folder";
	public static final String DRIVE_SPREADSHEET_TYPE = "application/vnd.google-apps.spreadsheet";
	public static final String DRIVE_DOCUMENT_TYPE = "application/vnd.google-apps.document";

	public static final List<String> EXPORT_TYPES = Arrays.asList(DRIVE_DOCUMENT_TYPE, DRIVE_SPREADSHEET_TYPE);
}
