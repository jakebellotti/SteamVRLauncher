package jakebellotti.steamvrlauncher.model;

import javafx.scene.control.ListView;

/**
 * 
 * @author Jake Bellotti
 *
 */
public abstract class ApplicationsListViewModifier {

	public abstract void setModification(final ListView<SteamApp> list);
	public abstract String getName();

	@Override
	public String toString() {
		return getName();
	}

}
