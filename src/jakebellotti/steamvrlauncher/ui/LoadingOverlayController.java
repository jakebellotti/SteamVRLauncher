package jakebellotti.steamvrlauncher.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class LoadingOverlayController {
	
	private static LoadingOverlayController singleton = null;

	@FXML
	private AnchorPane root;

	@FXML
	private ProgressIndicator progressIndicator;

	@FXML
	private Label currentTaskLabel;

	public AnchorPane getRoot() {
		return root;
	}

	public void setRoot(AnchorPane root) {
		this.root = root;
	}

	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}

	public void setProgressIndicator(ProgressIndicator progressIndicator) {
		this.progressIndicator = progressIndicator;
	}

	public Label getCurrentTaskLabel() {
		return currentTaskLabel;
	}

	public void setCurrentTaskLabel(Label currentTaskLabel) {
		this.currentTaskLabel = currentTaskLabel;
	}

	public static LoadingOverlayController getSingleton() {
		if(LoadingOverlayController.singleton == null) {
			try {
				final FXMLLoader loader = new FXMLLoader();
				singleton = new LoadingOverlayController();
				loader.setController(singleton);
				loader.load(LoadingOverlayController.class.getResource("LoadingOverlay.fxml").openStream());
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return singleton;
	}

}
