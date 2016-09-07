package jakebellotti.steamvrlauncher.resources;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;

import javafx.scene.image.Image;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class Resources {

	private static final HashMap<String, Object> resourceCache = new HashMap<>();

	public static final Image getImageNotFound() {
		final String resourceName = "image_not_found.jpg";
		if (!resourceCache.containsKey(resourceName)) {
			final Optional<InputStream> optImage = getResource(resourceName);
			optImage.ifPresent(res -> resourceCache.put(resourceName, new Image(res)));
		}
		return (Image) resourceCache.get(resourceName);
	}

	private static final Optional<InputStream> getResource(final String resourceName) {
		try {
			return Optional.ofNullable(Resources.class.getResource(resourceName).openStream());
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
