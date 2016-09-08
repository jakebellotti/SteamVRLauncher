package jakebellotti.steamvrlauncher.model;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class ScreenResolution {

	private final int resolutionWidth;
	private final int resolutionHeight;

	public ScreenResolution(int resolutionWidth, int resolutionHeight) {
		super();
		this.resolutionWidth = resolutionWidth;
		this.resolutionHeight = resolutionHeight;
	}

	public int getResolutionWidth() {
		return resolutionWidth;
	}

	public int getResolutionHeight() {
		return resolutionHeight;
	}

}
