package jakebellotti.steamvrlauncher.model;

import java.io.File;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class SteamVRManifestFile {

	private final int id;
	private final int steamFolderID;
	private final String fileLocation;
	private final long lastModified;

	public SteamVRManifestFile(int id, int steamFolderID, String fileLocation, long lastModified) {
		super();
		this.id = id;
		this.steamFolderID = steamFolderID;
		this.fileLocation = fileLocation;
		this.lastModified = lastModified;
	}
	
	public final File asFile() {
		return new File(getFileLocation());
	}

	public int getId() {
		return id;
	}

	public int getSteamFolderID() {
		return steamFolderID;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	public long getLastModified() {
		return lastModified;
	}

}
