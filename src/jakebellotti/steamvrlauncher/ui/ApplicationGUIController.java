package jakebellotti.steamvrlauncher.ui;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakebellotti.steamvrlauncher.Config;
import jakebellotti.steamvrlauncher.SteamConstants;
import jakebellotti.steamvrlauncher.SteamUtils;
import jakebellotti.steamvrlauncher.SteamVRLauncher;
import jakebellotti.steamvrlauncher.io.SteamDBParser;
import jakebellotti.steamvrlauncher.io.SteamVRAppsFileParser;
import jakebellotti.steamvrlauncher.model.ApplicationsListViewModifier;
import jakebellotti.steamvrlauncher.model.FileModificationHistory;
import jakebellotti.steamvrlauncher.model.SteamApp;
import jakebellotti.steamvrlauncher.model.SteamAppSettings;
import jakebellotti.steamvrlauncher.model.SteamFolder;
import jakebellotti.steamvrlauncher.model.SteamVRApp;
import jakebellotti.steamvrlauncher.model.alvm.ImageTileApplicationsListViewModifier;
import jakebellotti.steamvrlauncher.model.alvm.LabelApplicationsListViewModifier;
import jakebellotti.steamvrlauncher.resources.Resources;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import jblib.javafx.Alerts;
import jblib.javafx.JavaFXUtils;

/**
 * TODO add a way to delete file modification history (when the program gets
 * used too much it will start to lag)
 * 
 * TODO save the currently selected list view mode
 * 
 * @author Jake Bellotti
 *
 */
public class ApplicationGUIController {

	private final Stage stage;
	private final ArrayList<SteamApp> loadedApps = new ArrayList<>();

	@FXML
	private AnchorPane rootPane;

	@FXML
	private Button rescanSteamFoldersButton;

	@FXML
	private Label gamesLabel;

	@FXML
	private ListView<SteamApp> gamesListView;

	@FXML
	private Button browseSteamAppsButton;

	@FXML
	private Label renderTargetMultiplierLabel;

	@FXML
	private Label currentRenderTargetMultiplierLabel;

	@FXML
	private Label savedRenderTargetMultiplierLabel;

	@FXML
	private TextField searchApplicationsTextField;

	@FXML
	private Button upApplicationsButton;

	@FXML
	private Button downApplicationsButton;

	@FXML
	private Slider renderTargetMultiplierSlider;

	@FXML
	private TextField currentRenderTargetMultiplierTextField;

	@FXML
	private TextField savedRenderTargetMultiplierTextField;

	@FXML
	private ImageView plusRTMImageView;

	@FXML
	private ImageView minusRTMImageView;

	@FXML
	private CheckBox reprojectionCheckBox;

	@FXML
	private Label currentGameLabel;

	@FXML
	private Button launchApplicationButton;

	@FXML
	private Button saveApplicationButton;

	@FXML
	private ImageView currentApplicationImageView;

	@FXML
	private HBox currentAppImageHBox;

	@FXML
	private AnchorPane currentAppImagePane;

	@FXML
	private ListView<SteamFolder> steamFoldersListView;

	@FXML
	private ListView<FileModificationHistory> fileVersionHistoryListView;

	@FXML
	private TextArea fileHistoryModifiedTextArea;

	@FXML
	private TextArea fileHistoryOriginalTextArea;

	@FXML
	private Button revertFileModificationButton;

	@FXML
	private Hyperlink aboutProjectHyperLink;

	@FXML
	private Hyperlink aboutRedditHyperlink;

	@FXML
	private Hyperlink aboutDistributionsHyperLink;

	@FXML
	private Label aboutVersionLabel;

	@FXML
	private TextField outputResolutionTextField;

	@FXML
	private Label outputResolutionLabel;

	@FXML
	private ComboBox<ApplicationsListViewModifier> applicationsListViewLookComboBox;

	public ApplicationGUIController(final Stage stage) {
		this.stage = stage;
	}

	public static final Stage load() {
		final Stage stage = new Stage();
		try {
			final FXMLLoader loader = new FXMLLoader();
			stage.setTitle("Steam Enhanced Launcher - Version " + Config.VERSION + " - Jake Bellotti");
			loader.setController(new ApplicationGUIController(stage));
			stage.setScene(new Scene(loader.load(ApplicationGUIController.class.getResource("ApplicationGUI.fxml").openStream())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stage;
	}

	@FXML
	public void initialize() {
		// Set cursors
		browseSteamAppsButton.setCursor(Cursor.HAND);
		this.minusRTMImageView.setCursor(Cursor.HAND);
		this.plusRTMImageView.setCursor(Cursor.HAND);
		this.launchApplicationButton.setCursor(Cursor.HAND);
		this.saveApplicationButton.setCursor(Cursor.HAND);
		this.upApplicationsButton.setCursor(Cursor.HAND);
		this.downApplicationsButton.setCursor(Cursor.HAND);
		this.rescanSteamFoldersButton.setCursor(Cursor.HAND);
		this.revertFileModificationButton.setCursor(Cursor.HAND);

		// Set control event handlers
		browseSteamAppsButton.setOnMouseClicked(this::browseSteamAppsButtonMouseClicked);
		this.downApplicationsButton.setOnMouseClicked(this::downApplicationsButtonMouseClicked);
		this.upApplicationsButton.setOnMouseClicked(this::upApplicationsButtonMouseClicked);
		this.minusRTMImageView.setOnMousePressed(this::minusRTMImageViewMousePressed);
		this.launchApplicationButton.setOnMouseClicked(this::launchApplicationButtonMouseClicked);
		this.plusRTMImageView.setOnMousePressed(this::plusRTMImageViewMousePressed);
		this.saveApplicationButton.setOnMouseClicked(this::saveApplicationButtonMouseClicked);
		searchApplicationsTextField.setOnKeyReleased(this::searchApplicationsTextFieldKeyReleased);
		fileVersionHistoryListView.getSelectionModel().selectedItemProperty().addListener(l -> fileVersionHistoryListViewItemSelected());
		revertFileModificationButton.setOnMouseClicked(this::revertFileModificationButtonMouseClicked);
		this.rescanSteamFoldersButton.setOnMouseClicked(this::rescanSteamFoldersButtonMouseClicked);
		this.applicationsListViewLookComboBox.getSelectionModel().selectedItemProperty().addListener(l -> this.applicationsListViewLookComboBoxItemSelected());
		addOutputResolutionEventHandlers();

		// Set property value listeners
		this.currentApplicationImageView.fitWidthProperty().bind(currentAppImageHBox.widthProperty());
		this.renderTargetMultiplierSlider.valueProperty().addListener(l -> renderTargetMultiplierSliderValueChanged());
		this.reprojectionCheckBox.selectedProperty().addListener(l -> this.updateCurrentSettings());
		gamesListView.getItems().addListener((ListChangeListener<SteamApp>) e -> gamesListViewListChanged());
		gamesListView.getSelectionModel().selectedItemProperty().addListener(l -> gamesListViewItemSelected());

		// Add data

		setupAccountPage();
		setImages();
		changeListView();
		refreshApps();
		refreshSteamFolders();
		refreshFileModificationHistory();
		// TODO add the event handlers for this
		addSteamAppListViewComboBoxData();

		updateControls(false);

		// Select default data
		gamesListView.getSelectionModel().selectFirst();
		this.fileVersionHistoryListView.getSelectionModel().selectFirst();
		this.applicationsListViewLookComboBox.getSelectionModel().selectFirst();
	}
	
	private final void applicationsListViewLookComboBoxItemSelected() {
		final ApplicationsListViewModifier selected = applicationsListViewLookComboBox.getSelectionModel().getSelectedItem();
		if(selected != null) {
			selected.setModification(this.gamesListView);
		}
	}

	private final void addSteamAppListViewComboBoxData() {
		this.applicationsListViewLookComboBox.getItems().clear();
		this.applicationsListViewLookComboBox.getItems().add(new ImageTileApplicationsListViewModifier());
		this.applicationsListViewLookComboBox.getItems().add(new LabelApplicationsListViewModifier());
	}

	private final void addOutputResolutionEventHandlers() {
		// TODO on hover, on click, when off hover
		this.outputResolutionTextField.setOnMouseEntered(e -> {
			this.outputResolutionTextField.setStyle("-fx-text-fill: white; -fx-background-color:black;");
			this.outputResolutionTextField.setText("Compare");
			this.outputResolutionTextField.setCursor(Cursor.HAND);
		});
		this.outputResolutionTextField.setOnMouseExited(e -> {
			this.outputResolutionTextField.setStyle("");
			final SteamApp selected = this.gamesListView.getSelectionModel().getSelectedItem();
			if (selected != null)
				this.updateTargetResolution(selected.getCurrentSettings().getRenderTargetMultiplier());
		});
	}

	private final void updateTargetResolution(final int renderTargetMultiplier) {
		// TODO add a way to change current HMD
		// TODO just make it go by the current apps settings, rather than
		// supplying the value
		final int hmdWidth = 2560;
		final int hmdHeight = 1200;

		final int newResolutionWidth = (renderTargetMultiplier * hmdWidth) / 10;
		final int newResolutionHeight = (renderTargetMultiplier * hmdHeight) / 10;
		this.outputResolutionTextField.setText(newResolutionWidth + " x " + newResolutionHeight);
	}

	private final void setupAccountPage() {
		this.aboutVersionLabel.setText("Version " + Config.VERSION);
		this.aboutDistributionsHyperLink.setOnMouseClicked(e -> openURI(aboutDistributionsHyperLink.getText()));
		this.aboutProjectHyperLink.setOnMouseClicked(e -> openURI(aboutProjectHyperLink.getText()));
		this.aboutRedditHyperlink.setOnMouseClicked(e -> openURI(aboutRedditHyperlink.getText()));
	}

	private final void openURI(final String link) {
		try {
			Desktop.getDesktop().browse(new URI(link));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final void rescanSteamFoldersButtonMouseClicked(final MouseEvent e) {
		// TODO implement file rescanning
		// TODO clean this up, make it more readable
		SteamVRLauncher.getConnection().selectAllSteamVRManifestFiles().forEach(manifest -> {
			if (!manifest.asFile().exists()) {
				Alerts.showErrorAlert("File not found", "Steam VR Apps Manifest file not found",
						"Make sure your steam folders are indexed and this file exists.");
				return;
			}

			final Optional<ArrayList<SteamVRApp>> found = SteamVRAppsFileParser.parseManifest(manifest.asFile());

			found.ifPresent(o -> {
				final ArrayList<String> gameNames = new ArrayList<>();
				o.forEach(s -> gameNames.add(s.getAppKey()));
				final ArrayList<String> uniqueNames = SteamVRLauncher.getConnection().selectUniqueSteamApps(gameNames);

				final Iterator<SteamVRApp> iterator = o.iterator();
				while (iterator.hasNext()) {
					final SteamVRApp current = iterator.next();
					if (!uniqueNames.contains(current.getAppKey()))
						iterator.remove();
				}

				if (o.size() == 0) {
					Alerts.showInformationAlert("Rescan Steam Folders", "No new applications were added", "");
					return;
				}

				final AnchorPane loadingScreen = LoadingOverlayController.getSingleton().getRoot();
				rootPane.getChildren().add(loadingScreen);
				loadingScreen.toFront();

				loadingScreen.prefWidthProperty().bind(rootPane.widthProperty());
				loadingScreen.prefHeightProperty().bind(rootPane.heightProperty());

				SteamVRLauncher.submitRunnable(() -> {

					Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel().setText("Saving " + o.size() + " games"));
					final HashMap<Integer, SteamVRApp> gameIDs = new HashMap<>();

					for (SteamVRApp currentApp : o) {
						final int appDatabaseID = SteamVRLauncher.getConnection().insertSteamApp(manifest.getId(), currentApp);
						if (appDatabaseID > 0) {
							gameIDs.put(appDatabaseID, currentApp);
						}
					}

					final Iterator<Integer> iter = gameIDs.keySet().iterator();
					while (iter.hasNext()) {
						final Integer currentKey = iter.next();
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
					Platform.runLater(() -> rootPane.getChildren().remove(loadingScreen));
					refreshApps();
					refreshSteamFolders();
				} , "Indexing steam apps");

				Alerts.showInformationAlert("Rescan Steam Folders", "New applications were added", "" + o.size() + " new applications were added.");

			});
		});
	}

	private final void revertFileModificationButtonMouseClicked(final MouseEvent event) {
		// TODO implement file reverting
		final FileModificationHistory selected = fileVersionHistoryListView.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;
		if (!selected.asFile().exists()) {
			// TODO maybe add an option to recreate the file?
			Alerts.showErrorAlert("File not found", "The file '" + selected.asFile().getName() + "' does not exist.",
					"The file could not be reverted to it's original contents.");
			return;
		}

		try (FileWriter writer = new FileWriter(selected.asFile())) {
			writer.write(selected.getOldContent());
			writer.close();
			Alerts.showInformationAlert("Success", "File contents have been reverted",
					"Successfully reverted the file '" + selected.asFile().getName() + "' to its original contents.");
		} catch (Exception e) {
			Alerts.showErrorAlert("Error", "An error occurred when reverting the file", "Cause: " + e.getMessage());
		}
	}

	private final void fileVersionHistoryListViewItemSelected() {
		final FileModificationHistory selected = fileVersionHistoryListView.getSelectionModel().getSelectedItem();
		final boolean selectedExists = selected != null;

		this.revertFileModificationButton.setVisible(selectedExists);
		this.fileHistoryOriginalTextArea.setVisible(selectedExists);
		this.fileHistoryModifiedTextArea.setVisible(selectedExists);

		if (!selectedExists) {
			this.fileHistoryOriginalTextArea.clear();
			this.fileHistoryModifiedTextArea.clear();
			return;
		}

		this.fileHistoryOriginalTextArea.setText(selected.getOldContent());
		this.fileHistoryModifiedTextArea.setText(selected.getNewContent());
	}

	private final void minusRTMImageViewMousePressed(final MouseEvent e) {
		final double currentWidth = this.minusRTMImageView.getFitWidth();
		this.minusRTMImageView.setFitWidth(0.85d * currentWidth);

		this.minusRTMImageView.setOnMouseReleased(released -> {
			this.minusRTMImageView.setFitWidth(currentWidth);
			decreaseRTM();
		});
	}

	private final void renderTargetMultiplierSliderValueChanged() {
		final String rtm = "" + renderTargetMultiplierSlider.getValue();
		final int decimalPoint = rtm.indexOf(".");
		final String value = rtm.substring(0, decimalPoint + 2);

		this.currentRenderTargetMultiplierTextField.setText(value);

		final SteamApp selected = this.gamesListView.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;
		final double currentRTMValue = (this.renderTargetMultiplierSlider.getValue() * 10);

		selected.setCurrentSettings(new SteamAppSettings((int) currentRTMValue, selected.getCurrentSettings().isAllowReprojection()));
		checkDirty();

		this.updateTargetResolution(selected.getCurrentSettings().getRenderTargetMultiplier());
	}

	private final void plusRTMImageViewMousePressed(final MouseEvent e) {
		final double currentWidth = this.plusRTMImageView.getFitWidth();
		this.plusRTMImageView.setFitWidth(0.85d * currentWidth);

		this.plusRTMImageView.setOnMouseReleased(released -> {
			this.plusRTMImageView.setFitWidth(currentWidth);
			increaseRTM();
		});
	}

	private final void gamesListViewListChanged() {
		gamesLabel.setText("Applications (" + gamesListView.getItems().size() + ")");
	}

	private final void searchApplicationsTextFieldKeyReleased(KeyEvent e) {
		this.updateShownApps();
	}

	@SuppressWarnings("unchecked")
	private final void launchApplicationButtonMouseClicked(MouseEvent event) {
		final SteamApp selected = this.gamesListView.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;
		// TODO finish this
		final ArrayList<File> folders = new ArrayList<>();
		this.steamFoldersListView.getItems().forEach(sf -> folders.add(sf.asFile()));

		final Optional<File> steamVRSettingsFile = SteamUtils.findSteamVRSettings(folders);
		if (!steamVRSettingsFile.isPresent()) {
			Alerts.showErrorAlert("Error", "Steam VR Settings not found",
					"Ensure that the folder containing this file is indexed (Maybe Steam is installed over multiple drives).");
			return;
		}

		final String taskList = getTaskList();
		final boolean vrMonitorRunning = isSteamVRMonitorRunning(taskList);

		try {
			final StringBuilder builder = new StringBuilder();
			final Scanner scanner = new Scanner(steamVRSettingsFile.get());
			while (scanner.hasNextLine())
				builder.append(scanner.nextLine() + "\n");
			scanner.close();

			final JSONParser parser = new JSONParser();
			final JSONObject settings = (JSONObject) parser.parse(builder.toString());
			JSONObject steamVR = (JSONObject) settings.get("steamvr");

			if (steamVR == null) {
				steamVR = new JSONObject();
				steamVR.put("allowReprojection", selected.getCurrentSettings().isAllowReprojection());
				steamVR.put("renderTargetMultiplier", ((double) (selected.getCurrentSettings().getRenderTargetMultiplier() / 10d)));
				settings.put("steamvr", steamVR);
			} else {
				steamVR.put("allowReprojection", selected.getCurrentSettings().isAllowReprojection());
				steamVR.put("renderTargetMultiplier", ((double) (selected.getCurrentSettings().getRenderTargetMultiplier() / 10d)));
			}

			final Gson gson = new GsonBuilder().setPrettyPrinting().create();
			final String jsonOutput = gson.toJson(settings);

			final FileWriter writer = new FileWriter(steamVRSettingsFile.get());
			writer.write(jsonOutput);
			writer.close();

			final String changeComment = "Changed settings before launching '" + selected.getName() + "'";

			SteamVRLauncher.getConnection().insertFileModificationHistory(steamVRSettingsFile.get().getAbsolutePath(), changeComment, builder.toString(),
					jsonOutput);
			refreshFileModificationHistory();

			// TODO organise the JSON when exporting

			if (vrMonitorRunning) {
				final Process killVRMonitor = Runtime.getRuntime().exec("taskkill /F /IM " + SteamConstants.STEAM_VR_MONITOR_PROCESS_NAME);
				killVRMonitor.waitFor();
			}

			// TODO maybe even find another way to deal with these SteamVR bugs
			// TODO extract this to a settings variable
			Thread.sleep(5000);

			URI uri = new URI(selected.getLaunchPath());
			Desktop.getDesktop().browse(uri);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final void refreshFileModificationHistory() {
		// TODO make this have better peformance
		this.fileVersionHistoryListView.getItems().setAll(SteamVRLauncher.getConnection().selectAllFileModifications());
	}

	private final void refreshSteamFolders() {
		this.steamFoldersListView.getItems().setAll(SteamVRLauncher.getConnection().selectAllSteamFolders());
	}

	public final String getTaskList() {
		final StringBuilder builder = new StringBuilder();
		try {
			final Process taskList = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\tasklist.exe");
			final BufferedReader input = new BufferedReader(new InputStreamReader(taskList.getInputStream()));
			String line;
			while ((line = input.readLine()) != null)
				builder.append(line + "\n");
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return builder.toString().trim();
	}

	public final boolean isSteamVRMonitorRunning(final String taskList) {
		return taskList.contains(SteamConstants.STEAM_VR_MONITOR_PROCESS_NAME);
	}

	public final boolean isSteamVRServerRunning(final String taskList) {
		return taskList.contains(SteamConstants.STEAM_VR_SERVER_PROCESS_NAME);
	}

	private final void upApplicationsButtonMouseClicked(MouseEvent e) {
		// TODO make this scroll down from the first visible index
		final SteamApp selected = this.gamesListView.getSelectionModel().getSelectedItem();
		if (selected != null) {
			final int selectedIndex = this.gamesListView.getItems().indexOf(selected);
			if (selectedIndex > -1 && selectedIndex != 0) {
				final int newIndex = selectedIndex - 1;
				this.gamesListView.getSelectionModel().select(newIndex);
				this.gamesListView.scrollTo(newIndex);
			}
			Platform.runLater(() -> gamesListView.requestFocus());
		}
	}

	private final void downApplicationsButtonMouseClicked(MouseEvent e) {
		final SteamApp selected = this.gamesListView.getSelectionModel().getSelectedItem();
		if (selected != null) {
			final int maxIndexes = this.gamesListView.getItems().size();
			final int selectedIndex = this.gamesListView.getItems().indexOf(selected);
			final int newIndex = selectedIndex + 1;
			if (selectedIndex > -1 && newIndex < maxIndexes) {
				this.gamesListView.getSelectionModel().select(newIndex);
				this.gamesListView.scrollTo(newIndex);
			}
			Platform.runLater(() -> gamesListView.requestFocus());
		}
	}

	private final void saveApplicationButtonMouseClicked(final MouseEvent e) {
		final SteamApp selected = this.gamesListView.getSelectionModel().getSelectedItem();
		if (selected != null) {
			SteamVRLauncher.getConnection().updateSteamAppSettings(selected.getId(), selected.getCurrentSettings().getRenderTargetMultiplier(),
					selected.getCurrentSettings().isAllowReprojection());
			selected.setSavedSettings(selected.getCurrentSettings().copy());
			gamesListViewItemSelected();
			Platform.runLater(() -> gamesListView.requestFocus());
			checkDirty();
		}
	}

	private final void updateCurrentSettings() {
		final SteamApp selected = this.gamesListView.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;
		final double currentRTMValue = (this.renderTargetMultiplierSlider.getValue() * 10);

		selected.setCurrentSettings(new SteamAppSettings((int) currentRTMValue, this.reprojectionCheckBox.isSelected()));
		checkDirty();
	}

	private final void updateControls(boolean toggle) {
		this.currentGameLabel.setVisible(toggle);
		this.currentApplicationImageView.setVisible(toggle);
		this.renderTargetMultiplierSlider.setVisible(toggle);
		this.minusRTMImageView.setVisible(toggle);
		this.plusRTMImageView.setVisible(toggle);
		this.currentRenderTargetMultiplierTextField.setVisible(toggle);
		this.savedRenderTargetMultiplierTextField.setVisible(toggle);
		this.reprojectionCheckBox.setVisible(toggle);
		this.launchApplicationButton.setVisible(toggle);
		this.saveApplicationButton.setVisible(toggle);
		this.currentRenderTargetMultiplierLabel.setVisible(toggle);
		this.savedRenderTargetMultiplierLabel.setVisible(toggle);
		this.renderTargetMultiplierLabel.setVisible(toggle);
		this.outputResolutionLabel.setVisible(toggle);
		this.outputResolutionTextField.setVisible(toggle);
	}

	private final void gamesListViewItemSelected() {
		final SteamApp selected = gamesListView.getSelectionModel().getSelectedItem();
		final boolean isAnySelected = selected != null;

		updateControls(isAnySelected);

		if (!isAnySelected)
			return;

		this.currentGameLabel.setText(selected.getName());
		if (selected.getImage().isPresent()) {
			this.currentApplicationImageView.setImage(selected.getImage().get());
		} else {
			// TODO maybe set as a custom image that says 'image not found'
			this.currentApplicationImageView.setImage(Resources.getImageNotFound());
		}

		final double currentRTM = (selected.getCurrentSettings().getRenderTargetMultiplier() / 10d);
		final double savedRTM = (selected.getSavedSettings().getRenderTargetMultiplier() / 10d);

		this.currentRenderTargetMultiplierTextField.setText("" + currentRTM);
		this.savedRenderTargetMultiplierTextField.setText("" + savedRTM);

		this.renderTargetMultiplierSlider.setValue(currentRTM);
		this.reprojectionCheckBox.setSelected(selected.getCurrentSettings().isAllowReprojection());

		checkDirty();
	}

	private final void checkDirty() {
		final SteamApp selected = gamesListView.getSelectionModel().getSelectedItem();
		if (selected != null) {
			final boolean dirty = selected.isDirty();
			this.saveApplicationButton.setDisable(!dirty);
		}
	}

	private final void decreaseRTM() {
		int rtm = gamesListView.getSelectionModel().getSelectedItem().getCurrentSettings().getRenderTargetMultiplier();
		rtm = (rtm - 1);
		if (rtm < 0)
			rtm = 0;
		final double currentValue = (rtm / 10d);
		this.renderTargetMultiplierSlider.setValue(currentValue);
	}

	private final void increaseRTM() {
		// TODO make it so the setting goes by a constant instead of just 30
		int rtm = gamesListView.getSelectionModel().getSelectedItem().getCurrentSettings().getRenderTargetMultiplier();
		rtm = (rtm + 1);
		if (rtm > 30)
			rtm = 30;
		final double currentValue = (rtm / 10d);
		this.renderTargetMultiplierSlider.setValue(currentValue);
	}

	private final void setImages() {
		try {
			plusRTMImageView.setImage(new Image(Resources.class.getResource("plus_icon.png").openStream()));
			minusRTMImageView.setImage(new Image(Resources.class.getResource("minus_icon.png").openStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private final void changeListView() {
		// TODO make this either an image or the name of the app
		JavaFXUtils.setListViewCellFactory(gamesListView, (cell, item, b) -> {
			final Optional<Image> image = item.getImage();
			if (!image.isPresent()) {
				final Label label = new Label(item.getName());
				label.setStyle("-fx-font-size: 20px;");
				cell.setGraphic(label);
				return;
			}

			final ImageView view = new ImageView(image.get());
			view.setPreserveRatio(true);

			this.gamesListView.widthProperty().addListener(l -> {
				view.setFitWidth(gamesListView.getWidth() * 0.92d);
			});
			cell.setGraphic(view);
			view.setFitWidth(gamesListView.getWidth() * 0.92d);
		});
	}

	private final void refreshApps() {
		// TODO check to see if there are any at all...
		Platform.runLater(() -> {
			final AnchorPane loadingScreen = LoadingOverlayController.getSingleton().getRoot();
			rootPane.getChildren().add(loadingScreen);
			loadingScreen.toFront();
			loadingScreen.prefWidthProperty().bind(rootPane.widthProperty());
			loadingScreen.prefHeightProperty().bind(rootPane.heightProperty());
			LoadingOverlayController.getSingleton().getCurrentTaskLabel().setText("Refreshing application list...");

			this.loadedApps.clear();
			this.loadedApps.addAll(SteamVRLauncher.getConnection().selectAllSteamApps());
			updateShownApps();

			rootPane.getChildren().remove(loadingScreen);
		});
	}

	private final void updateShownApps() {
		final ArrayList<SteamApp> showing = new ArrayList<>();
		final String searchQuery = this.searchApplicationsTextField.getText().trim();
		final boolean queryNull = (searchQuery == null || searchQuery.length() < 1);

		this.gamesListView.getItems().clear();

		for (SteamApp a : this.loadedApps) {
			if (queryNull) {
				showing.add(a);
				continue;
			} else {
				if (a.getName().toLowerCase().contains(searchQuery.toLowerCase()))
					showing.add(a);
			}

		}

		this.gamesListView.getItems().setAll(showing);
	}

	private final void browseSteamAppsButtonMouseClicked(MouseEvent e) {
		final DirectoryChooser chooser = new DirectoryChooser();
		final File chosen = chooser.showDialog(stage);
		if (chosen != null) {
			int count = SteamVRLauncher.getConnection().selectSteamAppsFolders(chosen);
			if (count > 0) {
				Alerts.showErrorAlert("Error", "Folder already exists", "The given folder has already been indexed. You may refresh though.");
				return;
			}
			final AnchorPane loadingScreen = LoadingOverlayController.getSingleton().getRoot();
			rootPane.getChildren().add(loadingScreen);
			loadingScreen.toFront();

			loadingScreen.prefWidthProperty().bind(rootPane.widthProperty());
			loadingScreen.prefHeightProperty().bind(rootPane.heightProperty());

			// TODO clean this up

			// TODO a thread here never closes
			SteamVRLauncher.submitRunnable(() -> {
				Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel().setText("Saving directory"));
				final int folderID = SteamVRLauncher.getConnection().insertSteamFolder(chosen);
				final File vrManifestFile = new File(chosen.getAbsolutePath() + SteamConstants.STEAM_VR_APPS_MANIFEST_FILE_LOCATION);

				if (!vrManifestFile.exists()) {
					Platform.runLater(() -> {
						rootPane.getChildren().remove(loadingScreen);
						Alerts.showInformationAlert("Warning", "No VR manifest file found",
								"No SteamVR apps manifest was found, so no new games were added to the list.");
						refreshSteamFolders();
					});
					return;
				}

				Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel().setText("Saving manifest file location"));
				final int manifestID = SteamVRLauncher.getConnection().insertSteamManifestFile(folderID, vrManifestFile);

				Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel().setText("Parsing manifest file"));
				final Optional<ArrayList<SteamVRApp>> apps = SteamVRAppsFileParser.parseManifest(vrManifestFile);
				if (!apps.isPresent()) {
					// TODO be more specific with this...
					// TODO add error log
					Platform.runLater(() -> {
						rootPane.getChildren().remove(loadingScreen);
						Alerts.showErrorAlert("Error", "No VR apps were found or an error occurred",
								"Look in the 'errors.log' file to see if there was an error.");
					});
					return;
				}

				Platform.runLater(() -> LoadingOverlayController.getSingleton().getCurrentTaskLabel().setText("Saving " + apps.get().size() + " games"));
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
				Platform.runLater(() -> rootPane.getChildren().remove(loadingScreen));
				refreshApps();
				refreshSteamFolders();
			} , "Indexing steam apps");
		}
	}

	public Stage getStage() {
		return stage;
	}

}
