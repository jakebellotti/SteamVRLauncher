package jakebellotti.steamvrlauncher.model;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class SteamVRApp {

	private final int databaseID;
	private final String appKey;
	private final String launchType;
	private final String name;
	private final String imagePath;
	private final String launchURL;

	public SteamVRApp(int databaseID, String appKey, String launchType, String name, String imagePath, String launchURL) {
		super();
		this.databaseID = databaseID;
		this.appKey = appKey;
		this.launchType = launchType;
		this.name = name;
		this.imagePath = imagePath;
		this.launchURL = launchURL;
	}
	
	@Override
	public String toString() {
		return name + " - " +appKey;
	}

	public int getDatabaseID() {
		return databaseID;
	}

	public String getAppKey() {
		return appKey;
	}

	public String getLaunchType() {
		return launchType;
	}

	public String getName() {
		return name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public String getLaunchURL() {
		return launchURL;
	}

}
