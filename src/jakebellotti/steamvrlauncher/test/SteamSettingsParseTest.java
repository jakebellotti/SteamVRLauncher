package jakebellotti.steamvrlauncher.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

public class SteamSettingsParseTest {

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		try {
			final JSONParser parser = new JSONParser();
			final JSONObject loaded = (JSONObject) parser.parse(new FileReader(new File("./test/steamvr.vrsettings.json")));
			final JSONObject steamVR = (JSONObject) loaded.get("steamvr");
			
			steamVR.put("renderTargetMultiplier", 3.0);
			
			System.out.println(steamVR.containsKey("renderTargetMultiplier"));
			System.out.println(steamVR.containsKey("allowReprojection"));
			
			
			System.out.println(loaded.toJSONString());
			
			assertTrue(true);
		} catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}

}
