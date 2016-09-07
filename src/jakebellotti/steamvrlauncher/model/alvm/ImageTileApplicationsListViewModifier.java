package jakebellotti.steamvrlauncher.model.alvm;

import java.util.Optional;

import jakebellotti.steamvrlauncher.model.ApplicationsListViewModifier;
import jakebellotti.steamvrlauncher.model.SteamApp;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jblib.javafx.JavaFXUtils;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class ImageTileApplicationsListViewModifier extends ApplicationsListViewModifier {

	@Override
	public void setModification(ListView<SteamApp> list) {
		JavaFXUtils.setListViewCellFactory(list, (cell, item, b) -> {
			final Optional<Image> image = item.getImage();
			if (!image.isPresent()) {
				final Label label = new Label(item.getName());
				label.setStyle("-fx-font-size: 20px;");
				cell.setGraphic(label);
				return;
			}

			final ImageView view = new ImageView(image.get());
			view.setPreserveRatio(true);

			list.widthProperty().addListener(l -> {
				view.setFitWidth(list.getWidth() * 0.92d);
			});
			cell.setGraphic(view);
			view.setFitWidth(list.getWidth() * 0.92d);
		});
	}

	@Override
	public String getName() {
		return "View as image tiles";
	}

}
