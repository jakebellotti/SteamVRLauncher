package jakebellotti.steamvrlauncher.ui.tab;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

import jakebellotti.steamvrlauncher.SteamConstants;
import jakebellotti.steamvrlauncher.SteamVRLauncher;
import jakebellotti.steamvrlauncher.io.SteamDBParser;
import jakebellotti.steamvrlauncher.io.SteamVRAppsFileParser;
import jakebellotti.steamvrlauncher.model.SteamFolder;
import jakebellotti.steamvrlauncher.model.SteamVRApp;
import jakebellotti.steamvrlauncher.ui.ApplicationGUIController;
import jakebellotti.steamvrlauncher.ui.LoadingOverlayController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import jblib.javafx.Alerts;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class SettingsTabController {

	private final ApplicationGUIController applicationGUIController;

	@FXML
	private Button browseSteamAppsButton;

	@FXML
	private ListView<SteamFolder> steamFoldersListView;

	public SettingsTabController(final ApplicationGUIController applicationGUIController) {
		this.applicationGUIController = applicationGUIController;
	}

	@FXML
	public void initialize() {
		browseSteamAppsButton.setCursor(Cursor.HAND);

		browseSteamAppsButton.setOnMouseClicked(this::browseSteamAppsButtonMouseClicked);
	}

	public final void refreshSteamFolders() {
		this.steamFoldersListView.getItems().setAll(SteamVRLauncher.getConnection().selectAllSteamFolders());
	}

	private final void browseSteamAppsButtonMouseClicked(MouseEvent e) {
		final DirectoryChooser chooser = new DirectoryChooser();
		final File chosen = chooser.showDialog(applicationGUIController.getStage());
		if (chosen != null) {
			int count = SteamVRLauncher.getConnection().selectSteamAppsFolders(chosen);
			if (count > 0) {
				Alerts.showErrorAlert("Error", "Folder already exists",
						"The given folder has already been indexed. You may refresh though.");
				return;
			}
			final AnchorPane loadingScreen = LoadingOverlayController.getSingleton().getRoot();
			applicationGUIController.getRootPane().getChildren().add(loadingScreen);
			loadingScreen.toFront();

			loadingScreen.prefWidthProperty().bind(applicationGUIController.getRootPane().widthProperty());
			loadingScreen.prefHeightProperty().bind(applicationGUIController.getRootPane().heightProperty());

			// TODO clean this up

			// TODO a thread here never closes
			SteamVRLauncher.submitRunnable(() -> {
				Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel()
						.setText("Saving directory"));
				final int folderID = SteamVRLauncher.getConnection().insertSteamFolder(chosen);
				final File vrManifestFile = new File(
						chosen.getAbsolutePath() + SteamConstants.STEAM_VR_APPS_MANIFEST_FILE_LOCATION);

				if (!vrManifestFile.exists()) {
					Platform.runLater(() -> {
						applicationGUIController.getRootPane().getChildren().remove(loadingScreen);
						Alerts.showInformationAlert("Warning", "No VR manifest file found",
								"No SteamVR apps manifest was found, so no new games were added to the list.");
						refreshSteamFolders();
					});
					return;
				}

				Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel()
						.setText("Saving manifest file location"));
				final int manifestID = SteamVRLauncher.getConnection().insertSteamManifestFile(folderID,
						vrManifestFile);

				Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel()
						.setText("Parsing manifest file"));
				final Optional<ArrayList<SteamVRApp>> apps = SteamVRAppsFileParser.parseManifest(vrManifestFile);
				if (!apps.isPresent()) {
					// TODO be more specific with this...
					// TODO add error log
					Platform.runLater(() -> {
						applicationGUIController.getRootPane().getChildren().remove(loadingScreen);
						Alerts.showErrorAlert("Error", "No VR apps were found or an error occurred",
								"Look in the 'errors.log' file to see if there was an error.");
					});
					return;
				}

				Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel()
						.setText("Saving " + apps.get().size() + " games"));
				final HashMap<Integer, SteamVRApp> gameIDs = new HashMap<>();

				for (SteamVRApp currentApp : apps.get()) {
					final int appDatabaseID = SteamVRLauncher.getConnection().insertSteamApp(manifestID, currentApp);
					if (appDatabaseID > 0) {
						gameIDs.put(appDatabaseID, currentApp);
					}
				}

				final Iterator<Integer> iterator = gameIDs.keySet().iterator();
				while (iterator.hasNext()) {
					final Integer currentKey = iterator.next();
					final SteamVRApp currentApp = gameIDs.get(currentKey);
					Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel()
							.setText("Downloading image for '" + currentApp.getName() + "'"));

					final Optional<InputStream> result = SteamDBParser.getImage(currentApp);
					if (result.isPresent()) {
						final int imageID = SteamVRLauncher.getConnection().insertImage(result.get());
						if (imageID > 0) {
							SteamVRLauncher.getConnection().assignImageToSteamApp(currentKey, imageID);
						}
					}
				}
				Platform.runLater(() -> applicationGUIController.getRootPane().getChildren().remove(loadingScreen));
				applicationGUIController.refreshApps();
				refreshSteamFolders();
			} , "Indexing steam apps");
		}
	}

	public ListView<SteamFolder> getSteamFoldersListView() {
		return steamFoldersListView;
	}

}
