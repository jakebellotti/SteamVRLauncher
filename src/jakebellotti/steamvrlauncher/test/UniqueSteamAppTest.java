package jakebellotti.steamvrlauncher.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.Test;

import jakebellotti.steamvrlauncher.SteamVRLauncher;
import jakebellotti.steamvrlauncher.io.SteamVRAppsFileParser;
import jakebellotti.steamvrlauncher.model.SteamVRApp;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class UniqueSteamAppTest {

	@Test
	public void test() {
		final Optional<ArrayList<SteamVRApp>> found = SteamVRAppsFileParser.parseManifest(new File("./test/steamapps.vrmanifest"));

		found.ifPresent(o -> {
			final ArrayList<String> gameNames = new ArrayList<>();

			System.out.println("Found VR games:");
			for (SteamVRApp a : o) {
				System.out.println("\t" + a.getName() + ", " + a.getAppKey());
				gameNames.add(a.getAppKey());
			}

			SteamVRLauncher.getConnection().connect();

			System.out.println("VR Games Not in database:");

			SteamVRLauncher.getConnection().selectUniqueSteamApps(gameNames).forEach(System.out::println);

		});
	}

}
