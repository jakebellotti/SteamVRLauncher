package jakebellotti.steamvrlauncher.model.alvm;

import jakebellotti.steamvrlauncher.model.ApplicationsListViewModifier;
import jakebellotti.steamvrlauncher.model.SteamApp;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import jblib.javafx.JavaFXUtils;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class LabelApplicationsListViewModifier extends ApplicationsListViewModifier {

	@Override
	public void setModification(ListView<SteamApp> list) {
		JavaFXUtils.setListViewCellFactory(list, (cell, item, b) -> {
			final Label label = new Label(item.getName());
			label.setStyle("-fx-font-size: 20px;");
			cell.setGraphic(label);
		});
	}

	@Override
	public String getName() {
		return "View as application names";
	}

}
