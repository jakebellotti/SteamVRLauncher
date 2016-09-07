package jakebellotti.steamvrlauncher.io;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class DatabaseConnectionResult {
	
	private final boolean success;
	private final String message;
	
	public DatabaseConnectionResult(final boolean success) {
		this(success, null);
	}
	
	public DatabaseConnectionResult(final boolean success, final String message) {
		this.success = success;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMessage() {
		return message;
	}

}