package jakebellotti.steamvrlauncher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import jakebellotti.steamvrlauncher.io.DatabaseConnection;
import jakebellotti.steamvrlauncher.io.EmbeddedDatabaseDriver;
import jakebellotti.steamvrlauncher.ui.LauncherController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author Jake Bellotti
 *
 */
public class SteamVRLauncher extends Application {

	private static final DatabaseConnection connection = new EmbeddedDatabaseDriver();
	private static ExecutorService executor = Executors.newFixedThreadPool(1);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final FXMLLoader loader = new FXMLLoader();
		loader.setController(new LauncherController(primaryStage));
		primaryStage
				.setScene(new Scene(loader.load(LauncherController.class.getResource("Launcher.fxml").openStream())));
		primaryStage.setTitle("Steam Enhanced Launcher - Version " + Config.VERSION + " - Jake Bellotti");
		primaryStage.show();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			executor.shutdownNow();
		}));
	}

	public static final void submitRunnable(final Runnable r, final String threadName) {
		synchronized (executor) {
			executor = Executors.newFixedThreadPool(1, (ThreadFactory) thread -> {
				return new Thread(r, threadName);
			});
			executor.submit(r);
		}
	}

	public static DatabaseConnection getConnection() {
		return connection;
	}

}
