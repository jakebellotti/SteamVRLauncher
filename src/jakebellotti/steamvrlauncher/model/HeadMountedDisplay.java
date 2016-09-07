package jakebellotti.steamvrlauncher.model;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class HeadMountedDisplay {

	private final int id;
	private final String name;
	private final int resoultionWidth;
	private final int resolutionHeight;

	public HeadMountedDisplay(int id, String name, int resoultionWidth, int resolutionHeight) {
		super();
		this.id = id;
		this.name = name;
		this.resoultionWidth = resoultionWidth;
		this.resolutionHeight = resolutionHeight;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getResoultionWidth() {
		return resoultionWidth;
	}

	public int getResolutionHeight() {
		return resolutionHeight;
	}

}
