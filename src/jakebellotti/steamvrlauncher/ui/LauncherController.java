package jakebellotti.steamvrlauncher.ui;

import java.util.Scanner;

import jakebellotti.steamvrlauncher.Config;
import jakebellotti.steamvrlauncher.SteamVRLauncher;
import jakebellotti.steamvrlauncher.io.DatabaseConnectionResult;
import jakebellotti.steamvrlauncher.resources.Resources;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import jblib.javafx.Alerts;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class LauncherController {

	private final Stage stage;

	@FXML
	private Label infoLabel;

	@FXML
	private WebView releaseNotesWebView;

	@FXML
	private Button launchButton;

	public LauncherController(final Stage stage) {
		this.stage = stage;
	}

	@FXML
	public void initialize() {
		launchButton.setCursor(Cursor.HAND);
		infoLabel.setText("Version " + Config.VERSION + " By Jake Bellotti (" + Config.CREATOR_REDDIT_PAGE_LINK + ")");
		launchButton.setOnMouseClicked(this::launchButtonMouseClicked);
		try (Scanner scanner = new Scanner(Resources.class.getResource("releaseNotes.html").openStream())) {
			final StringBuilder builder = new StringBuilder();
			while (scanner.hasNextLine())
				builder.append(scanner.nextLine());
			Platform.runLater(() -> {
				releaseNotesWebView.getEngine().loadContent(builder.toString());
			});
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private final void launchButtonMouseClicked(MouseEvent e) {
		stage.close();
		final DatabaseConnectionResult result = SteamVRLauncher.getConnection().connect();
		if (!result.isSuccess()) {
			Alerts.showErrorAlert("Error", "Could not connect to localhost database", result.getMessage());
			return;
		}

		SteamVRLauncher.getConnection().createTablesCheck();

		ApplicationGUIController.load().show();
		// TODO check for new games etc
	}

}
