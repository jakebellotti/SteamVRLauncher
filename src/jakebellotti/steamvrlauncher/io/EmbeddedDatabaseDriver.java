package jakebellotti.steamvrlauncher.io;

import java.sql.DriverManager;

public class EmbeddedDatabaseDriver extends DatabaseConnection {

	@Override
	public DatabaseConnectionResult connect() {
		try {
			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection("jdbc:h2:./data/data", "", "");
		} catch(Exception e) {
			e.printStackTrace();
			return new DatabaseConnectionResult(false, e.getMessage());
		}
		return new DatabaseConnectionResult(true, "Success.");
	}

}