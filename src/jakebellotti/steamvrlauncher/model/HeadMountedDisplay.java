package jakebellotti.steamvrlauncher.model;

/**
 * 
 * @author Jake Bellotti
 *
 */
public interface HeadMountedDisplay {
	
	public int getID();
	public String getName();
	public int getResolutionWidth();
	public int getResolutionHeight();
	public int getRefreshRate();
	public ScreenResolution calculateOutputResolution(final int renderTargetMultiplier);

}
