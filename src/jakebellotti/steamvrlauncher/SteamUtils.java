package jakebellotti.steamvrlauncher;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class SteamUtils {

	/**
	 * Attempts to find the SteamVR manifest file within the given locations.
	 * The VR manifest files contain the currently installed steam apps.
	 * 
	 * @param indexedSteamFolders
	 * @return
	 */
	public static final Optional<File> findSteamVRManifest(final ArrayList<File> indexedSteamFolders) {
		for (final File f : indexedSteamFolders) {
			final File currentFile = new File(f.getAbsolutePath() + SteamConstants.STEAM_VR_APPS_MANIFEST_FILE_LOCATION);
			if (currentFile.exists())
				return Optional.ofNullable(currentFile);
		}
		return Optional.empty();
	}

	/**
	 * Attempts to find the SteamVR settings within the given locations. The
	 * SteamVR settings file contains the VR settings such as
	 * renderTargetMultiplier and allowReprojection.
	 * 
	 * @param indexedSteamFolders
	 * @return
	 */
	public static final Optional<File> findSteamVRSettings(final ArrayList<File> indexedSteamFolders) {
		for (final File f : indexedSteamFolders) {
			final File currentFile = new File(f.getAbsolutePath() + SteamConstants.STEAM_VR_SETTINGS_LOCATION);
			if (currentFile.exists())
				return Optional.ofNullable(currentFile);
		}
		return Optional.empty();
	}

}
