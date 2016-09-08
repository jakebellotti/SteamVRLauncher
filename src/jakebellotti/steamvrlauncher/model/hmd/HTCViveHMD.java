package jakebellotti.steamvrlauncher.model.hmd;

import jakebellotti.steamvrlauncher.model.HeadMountedDisplay;
import jakebellotti.steamvrlauncher.model.ScreenResolution;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class HTCViveHMD implements HeadMountedDisplay {
	
	private static final double additionalMultiplier = 1.4;

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public String getName() {
		return "HTC Vive - CV1";
	}
	
	@Override
	public int getRefreshRate() {
		return 90;
	}

	@Override
	public int getResolutionWidth() {
		return 2560;
	}

	@Override
	public int getResolutionHeight() {
		return 1200;
	}

	@Override
	public ScreenResolution calculateOutputResolution(int renderTargetMultiplier) {
		final double rtm = (renderTargetMultiplier) / 10d;
		final double resolutionWidth = (getResolutionWidth() * rtm) * additionalMultiplier;
		final double resolutionHeight = (getResolutionHeight() * rtm) * additionalMultiplier;
		return new ScreenResolution((int) resolutionWidth, (int) resolutionHeight);
	}

}
