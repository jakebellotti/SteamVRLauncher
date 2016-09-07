package jakebellotti.steamvrlauncher.io;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import jakebellotti.steamvrlauncher.Config;
import jakebellotti.steamvrlauncher.model.FileModificationHistory;
import jakebellotti.steamvrlauncher.model.SteamApp;
import jakebellotti.steamvrlauncher.model.SteamAppSettings;
import jakebellotti.steamvrlauncher.model.SteamFolder;
import jakebellotti.steamvrlauncher.model.SteamVRApp;
import jakebellotti.steamvrlauncher.model.SteamVRManifestFile;
import jakebellotti.steamvrlauncher.resources.Resources;
import javafx.scene.image.Image;

/**
 *
 * 
 * @author Jake Bellotti
 * @since 27 Jul 2016
 */
public abstract class DatabaseConnection {

	public static final String[] EXPECTED_TABLE_NAMES = { "tblSteamFolder", "tblSteamVRManifestFile", "tblImage", "tblSteamApp" };
	public static final String SELECT_TABLES = "SHOW TABLES";
	public static final String TABLE_NAME = "TABLE_NAME";

	protected Connection connection;

	public void createTablesCheck() {
		if (!allTablesExist()) {
			try (final Scanner scanner = new Scanner(Resources.class.getResource("databaseCreateSchema.sql").openStream())) {
				final StringBuilder builder = new StringBuilder();
				while (scanner.hasNextLine())
					builder.append(scanner.nextLine() + "\n");

				final PreparedStatement statement = connection.prepareStatement(builder.toString());
				statement.executeUpdate();
				statement.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public final int insertSteamFolder(final File file) {
		final String query = "INSERT INTO tblSteamFolder(fldFolderLocation) VALUES(?)";
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, file.getAbsolutePath());
			statement.executeUpdate();
			return selectLastInsertID();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public final ArrayList<SteamApp> selectAllSteamApps() {
		final String sql = "SELECT * FROM tblSteamApp ORDER BY fldName";
		final ArrayList<SteamApp> toReturn = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			final ResultSet set = statement.executeQuery();

			while (set.next()) {
				final int id = set.getInt("fldID");
				final int steamVRManifestFileID = set.getInt("fldFKSteamVRManifestFileID");
				final int selectedImageID = set.getInt("fldFKSelectedImageID");
				final String appKey = set.getString("fldAppKey");
				final String launchType = set.getString("fldLaunchType");
				final String name = set.getString("fldName");
				final String imagePath = set.getString("fldImagePath");
				final String launchPath = set.getString("fldLaunchPath");

				final Optional<Image> image = selectImage(selectedImageID);
				final Optional<SteamAppSettings> settings = selectSteamAppSettings(id);

				toReturn.add(new SteamApp(id, steamVRManifestFileID, image.get(), appKey, launchType, name, imagePath, launchPath, settings.get()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	private final Optional<SteamAppSettings> selectSteamAppSettings(final int steamAppID) {
		final String sql = "SELECT fldRenderTargetMultiplier, fldAllowReprojection FROM tblSteamAppVRSettings WHERE fldFKSteamAppID = ? LIMIT 1";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, steamAppID);
			final ResultSet set = statement.executeQuery();
			while (set.next()) {
				final int renderTargetMultiplier = set.getInt("fldRenderTargetMultiplier");
				final boolean allowReprojection = set.getBoolean("fldAllowReprojection");
				set.close();

				return Optional.of(new SteamAppSettings(renderTargetMultiplier, allowReprojection));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public final ArrayList<FileModificationHistory> selectAllFileModifications() {
		final ArrayList<FileModificationHistory> toReturn = new ArrayList<>();
		final String sql = "SELECT * FROM tblFileModificationHistory ORDER BY fldID DESC";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			final ResultSet set = statement.executeQuery();

			while (set.next()) {
				final int id = set.getInt("fldID");
				final String fileLocation = set.getString("fldFileLocation");
				final String changeComment = set.getString("fldChangeComment");
				final String oldContent = set.getString("fldOldContent");
				final String newContent = set.getString("fldNewContent");
				toReturn.add(new FileModificationHistory(id, fileLocation, changeComment, oldContent, newContent));
			}
			set.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public void insertFileModificationHistory(final String fileLocation, final String changeComment, final String oldContent, final String newContent) {
		final String sql = "INSERT INTO tblFileModificationHistory(fldFileLocation, fldChangeComment, fldOldContent, fldNewContent) VALUES(?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, fileLocation);
			statement.setString(2, changeComment);
			statement.setString(3, oldContent);
			statement.setString(4, newContent);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateSteamAppSettings(final int id, int renderTargetMultiplier, boolean reprojection) {
		final String sql = "UPDATE tblSteamAppVRSettings SET fldRenderTargetMultiplier = ?, fldAllowReprojection = ? WHERE fldFKSteamAppID = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, renderTargetMultiplier);
			statement.setBoolean(2, reprojection);
			statement.setInt(3, id);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final Optional<Image> selectImage(final int imageID) {
		final String sql = "SELECT fldImageData FROM tblImage WHERE fldID = ? LIMIT 1";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, imageID);
			final ResultSet set = statement.executeQuery();
			while (set.next()) {
				final Image image = new Image(set.getBlob("fldImageData").getBinaryStream());
				set.close();
				return Optional.ofNullable(image);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	/**
	 * When given a List of steam application keys, will return all the
	 * application keys that were given that aren't in the database.
	 * 
	 * @param given
	 * @return
	 */
	public final ArrayList<String> selectUniqueSteamApps(final ArrayList<String> given) {
		final ArrayList<String> toReturn = new ArrayList<>();
		toReturn.addAll(given);
		String sql = "SELECT fldAppKey FROM tblSteamApp WHERE fldAppKey IN(";

		for (int i = 0; i < given.size(); i++) {
			sql += "?";
			sql += (i + 1 == given.size()) ? ")" : ",";
		}
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			for (int i = 0; i < given.size(); i++)
				statement.setString(i + 1, given.get(i));

			final ResultSet set = statement.executeQuery();
			while (set.next())
				toReturn.remove(set.getString("fldAppKey"));
			set.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public final ArrayList<SteamVRManifestFile> selectAllSteamVRManifestFiles() {
		final ArrayList<SteamVRManifestFile> toReturn = new ArrayList<>();
		final String sql = "SELECT * FROM tblSteamVRManifestFile";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			final ResultSet set = statement.executeQuery();

			while (set.next()) {
				final int id = set.getInt("fldID");
				final int steamFolderID = set.getInt("fldFKSteamFolderID");
				final String fileLocation = set.getString("fldFileLocation");
				final long lastModified = set.getLong("fldLastModified");
				toReturn.add(new SteamVRManifestFile(id, steamFolderID, fileLocation, lastModified));
			}
			set.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public final int insertSteamManifestFile(final int steamFolderID, final File manifestFile) {
		final String sql = "INSERT INTO tblSteamVRManifestFile(fldFKSteamFolderID, fldFileLocation, fldLastModified) VALUES(?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, steamFolderID);
			statement.setString(2, manifestFile.getAbsolutePath());
			statement.setLong(3, manifestFile.lastModified());
			statement.executeUpdate();
			return selectLastInsertID();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public final int insertSteamApp(final int manifestID, final SteamVRApp app) {
		final String sql = "INSERT INTO tblSteamApp(fldFKSteamVRManifestFileID, fldAppKey, fldLaunchType, fldName, fldImagePath, fldLaunchPath) VALUES(?, ?, ?, ?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, manifestID);
			statement.setString(2, app.getAppKey());
			statement.setString(3, app.getLaunchType());
			statement.setString(4, app.getName());
			statement.setString(5, app.getImagePath());
			statement.setString(6, app.getLaunchURL());
			statement.executeUpdate();

			final int steamAppID = selectLastInsertID();
			insertDefaultSteamAppSettings(steamAppID);
			return steamAppID;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public final void insertDefaultSteamAppSettings(final int steamAppID) {
		final String sql = "INSERT INTO tblSteamAPPVRSettings(fldFKSteamAppID, fldRenderTargetMultiplier, fldAllowReprojection) VALUES(?, ?, ?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, steamAppID);
			statement.setInt(2, Config.DEFAULT_RENDER_TARGET_MULTIPLIER);
			statement.setBoolean(3, Config.DEFAULT_ALLOW_REPROJECTION);
			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void assignImageToSteamApp(final int steamAppID, final int imageID) {
		final String sql = "UPDATE tblSteamApp SET fldFKSelectedImageID = ? WHERE fldID = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, imageID);
			statement.setInt(2, steamAppID);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final int insertImage(final InputStream image) {
		final String sql = "INSERT INTO tblImage(fldImageData) VALUES(?)";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setBlob(1, image);
			statement.executeUpdate();
			return selectLastInsertID();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public final int selectLastInsertID() {
		final String sql = "SELECT LAST_INSERT_ID() AS fldID";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			final ResultSet set = statement.executeQuery();

			while (set.next()) {
				final int lastInsertID = set.getInt("fldID");
				set.close();
				return lastInsertID;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public final ArrayList<SteamFolder> selectAllSteamFolders() {
		final String query = "SELECT * FROM tblSteamFolder ORDER BY fldID";
		final ArrayList<SteamFolder> toReturn = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			final ResultSet set = statement.executeQuery();

			while (set.next()) {
				final int id = set.getInt("fldID");
				final String folderLocation = set.getString("fldFolderLocation");
				toReturn.add(new SteamFolder(id, folderLocation));
			}
			set.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}

	public final int selectSteamAppsFolders(final File folderLocation) {
		final String sql = "SELECT COUNT(*) AS COUNT FROM tblSteamFolder WHERE fldFolderLocation = ?";
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, folderLocation.getAbsolutePath());
			final ResultSet set = statement.executeQuery();

			while (set.next()) {
				int count = set.getInt("COUNT");
				set.close();
				return count;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private boolean allTablesExist() {
		try {
			final ArrayList<String> left = new ArrayList<>();
			for (String s : EXPECTED_TABLE_NAMES)
				left.add(s.toUpperCase());

			final ResultSet set = connection.createStatement().executeQuery(SELECT_TABLES);
			while (set.next()) {
				final String currentTableName = set.getString(TABLE_NAME).toUpperCase();
				left.remove(currentTableName);
			}
			set.close();
			return left.size() == 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public abstract DatabaseConnectionResult connect();

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
