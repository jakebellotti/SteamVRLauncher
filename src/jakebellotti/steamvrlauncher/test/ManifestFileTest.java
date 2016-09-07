package jakebellotti.steamvrlauncher.test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import jakebellotti.steamvrlauncher.io.SteamVRAppsFileParser;

/**
 * Tests the locating and parsing of SteamApp manifest files.
 * 
 * @author Jake Bellotti
 *
 */
public class ManifestFileTest {

	private static final String TEST_MANIFEST_LOCATION = "./test/steamapps.vrmanifest.json";

	@Test
	public void testParsingManifest() {
		assertTrue(SteamVRAppsFileParser.parseManifest(new File(TEST_MANIFEST_LOCATION)).isPresent());
	}

}
