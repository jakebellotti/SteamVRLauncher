package jakebellotti.steamvrlauncher.io;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

import jakebellotti.steamvrlauncher.model.SteamVRApp;

public class SteamDBParser {
	
	public static final Optional<InputStream> getImage(final SteamVRApp app) {
		try {
			URL url = new URL(app.getImagePath());
			InputStream in = new BufferedInputStream(url.openStream());
			return Optional.ofNullable(in);
		} catch(Exception e) {
			return Optional.empty();
		}
	}

}
