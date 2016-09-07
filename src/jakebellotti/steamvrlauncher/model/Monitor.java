package jakebellotti.steamvrlauncher.model;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class Monitor {

	private final int name;
	private final int resolutionWidth;
	private final int resolutionHeight;

	public Monitor(int name, int resolutionWidth, int resolutionHeight) {
		super();
		this.name = name;
		this.resolutionWidth = resolutionWidth;
		this.resolutionHeight = resolutionHeight;
	}

	public int getName() {
		return name;
	}

	public int getResolutionWidth() {
		return resolutionWidth;
	}

	public int getResolutionHeight() {
		return resolutionHeight;
	}

}
