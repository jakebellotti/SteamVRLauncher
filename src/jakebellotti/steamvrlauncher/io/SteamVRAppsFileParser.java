package jakebellotti.steamvrlauncher.io;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import jakebellotti.steamvrlauncher.model.SteamVRApp;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;

/**
 * //		https://github.com/indyK1ng/Xogar/blob/master/XogarLib/SimpleValveGameParser.cs
 * @author Jake Bellotti
 *
 */
public class SteamVRAppsFileParser {

	/**
	 * TODO make this not language specific
	 * @param manifest
	 * @return
	 */
	public static final Optional<ArrayList<SteamVRApp>> parseManifest(final File manifest) {
		final JSONParser parser = new JSONParser();
		try {
			final ArrayList<SteamVRApp> toReturn = new ArrayList<>();
			final JSONObject object = (JSONObject) parser.parse(new FileReader(manifest));
			final JSONArray applications = (JSONArray) object.get("applications");
			final Iterator<?> iterator = applications.iterator();
			while (iterator.hasNext()) {
				final JSONObject currentApplication = (JSONObject) iterator.next();
				
				final String appKey = (String) currentApplication.get("app_key");
				final String launchType = (String) currentApplication.get("launch_type");
				final JSONObject strings = (JSONObject) currentApplication.get("strings");
				
				JSONObject english = (JSONObject) strings.get("en_us");
				final String name = (String) english.get("name");
				final String imagePath = (String) currentApplication.get("image_path");
				final String launchURL = (String) currentApplication.get("url");
				
				toReturn.add(new SteamVRApp(-1, appKey, launchType, name, imagePath, launchURL));
			}
			if(toReturn.size() > 0)
				return Optional.of(toReturn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Optional.empty();
	}

}
