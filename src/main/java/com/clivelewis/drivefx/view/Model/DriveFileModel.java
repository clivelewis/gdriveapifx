package com.clivelewis.drivefx.view.Model;

import com.google.api.client.util.DateTime;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class DriveFileModel {
	private String id;
	private String name;
	private Long size;
	private String type;
	private boolean downloadable;
	private boolean ownedByMe;
	private boolean hasThumbnail;
	private String thumbnailUrl;
	private Date timeCreated;
	private String formattedTimeCreated;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		if(size == null) return "NULL";
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		return decimalFormat.format(size / 1000000.0D) + " MB";
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDownloadable() {
		return downloadable;
	}

	public void setDownloadable(boolean downloadable) {
		this.downloadable = downloadable;
	}

	public boolean isOwnedByMe() {
		return ownedByMe;
	}

	public void setOwnedByMe(boolean ownedByMe) {
		this.ownedByMe = ownedByMe;
	}

	public boolean getHasThumbnail(){
		return hasThumbnail;
	}

	// TODO: Remake.
	public void setHasThumbnail(Boolean hasThumbnail) {
		if(getType().contains("vnd.google-apps")){
			this.hasThumbnail = false;
		}else{
			this.hasThumbnail = Objects.requireNonNullElse(hasThumbnail, false);
		}
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Date getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(Date timeCreated) {
		this.timeCreated = timeCreated;
	}

	public String getFormattedTimeCreated(){
		String pattern = "dd/MM/yyyy HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(timeCreated);
	}
	public boolean isFolder(){
		return getType().contains("vnd.google-apps.folder");
	}
}
