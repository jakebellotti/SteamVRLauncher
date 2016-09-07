package jakebellotti.steamvrlauncher.model;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class SteamAppSettings {

	private final int renderTargetMultiplier;
	private final boolean allowReprojection;

	public SteamAppSettings(final int renderTargetMultiplier, final boolean allowReprojection) {
		this.renderTargetMultiplier = renderTargetMultiplier;
		this.allowReprojection = allowReprojection;
	}

	public final SteamAppSettings copy() {
		return new SteamAppSettings(renderTargetMultiplier, allowReprojection);
	}
	
	public int getRenderTargetMultiplier() {
		return renderTargetMultiplier;
	}

	public boolean isAllowReprojection() {
		return allowReprojection;
	}

}
