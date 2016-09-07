package jakebellotti.steamvrlauncher.model;

import java.io.File;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class SteamFolder {

	private final int id;
	private final String folderLocation;

	public SteamFolder(int id, String folderLocation) {
		super();
		this.id = id;
		this.folderLocation = folderLocation;
	}
	
	/**
	 * 
	 * @return This Steam Folder as a File object.
	 */
	public final File asFile() {
		return new File(folderLocation);
	}
	
	@Override
	public String toString() {
		return folderLocation;
	}

	public int getId() {
		return id;
	}

	public String getFolderLocation() {
		return folderLocation;
	}

}
