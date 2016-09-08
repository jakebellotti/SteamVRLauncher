package jakebellotti.steamvrlauncher.ui.tab;

import java.awt.Desktop;
import java.net.URI;

import jakebellotti.steamvrlauncher.Config;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class AboutTabController {

	@FXML
	private Hyperlink aboutProjectHyperLink;

	@FXML
	private Hyperlink aboutRedditHyperlink;

	@FXML
	private Hyperlink aboutDistributionsHyperLink;

	@FXML
	private Label aboutVersionLabel;
	
	@FXML
	public void initialize() {
		setupAboutPage();
	}
	
	private final void setupAboutPage() {
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

}
