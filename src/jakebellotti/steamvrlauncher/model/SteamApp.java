package jakebellotti.steamvrlauncher.model;

import java.util.Optional;

import javafx.scene.image.Image;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class SteamApp {

	private final int id;
	private final int steamVRManifestFileID;
	private final Optional<Image> image;
	private final String appKey;
	private final String launchType;
	private final String name;
	private final String imagePath;
	private final String launchPath;
	private SteamAppSettings savedSettings;
	private SteamAppSettings currentSettings;

	public SteamApp(int id, int steamVRManifestFileID, final Optional<Image> image, String appKey, String launchType, String name, String imagePath, String launchPath,
			SteamAppSettings settings) {
		super();
		this.id = id;
		this.steamVRManifestFileID = steamVRManifestFileID;
		this.image = image;
		this.appKey = appKey;
		this.launchType = launchType;
		this.name = name;
		this.imagePath = imagePath;
		this.launchPath = launchPath;
		this.savedSettings = settings;
		this.setCurrentSettings(settings.copy());
	}

	public int getId() {
		return id;
	}

	public int getSteamVRManifestFileID() {
		return steamVRManifestFileID;
	}

	public Optional<Image> getImage() {
		return image;
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

	public String getLaunchPath() {
		return launchPath;
	}

	public final boolean isDirty() {
		final boolean rtmDirty = currentSettings.getRenderTargetMultiplier() != savedSettings.getRenderTargetMultiplier();
		final boolean reprojectionDirty = currentSettings.isAllowReprojection() != savedSettings.isAllowReprojection();
		return (rtmDirty || reprojectionDirty);
	}

	@Override
	public String toString() {
		return name;
	}

	public SteamAppSettings getSavedSettings() {
		return savedSettings;
	}

	public SteamAppSettings getCurrentSettings() {
		return currentSettings;
	}

	public void setCurrentSettings(SteamAppSettings currentSettings) {
		this.currentSettings = currentSettings;
	}

	public void setSavedSettings(SteamAppSettings savedSettings) {
		this.savedSettings = savedSettings;
	}

}
