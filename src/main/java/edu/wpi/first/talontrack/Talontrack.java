package edu.wpi.first.talontrack;

import edu.wpi.first.math.WPIMathJNI;
import edu.wpi.first.talontrack.extensions.ExtensionManager;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Talontrack extends Application {
  public static Scene mainScene;

  @Override
  public void start(Stage primaryStage) throws IOException {
    WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
    WPIMathJNI.Helper.setExtractOnStaticLoad(false);
    CombinedRuntimeLoader.loadLibraries(Talontrack.class, "wpiutiljni",
        "wpimathjni");

    ExtensionManager.getInstance().refresh();
    Pane root = FXMLLoader.load(getClass().getResource("welcomeScreen.fxml"));
    this.mainScene = new Scene(root);
    primaryStage.setTitle("Talon Track 5427 - Swerve");
    // Work around dialog bug
    // See
    // https://stackoverflow.com/questions/55190380/javafx-creates-alert-dialog-which-is-too-small
    primaryStage.setResizable(true);
    primaryStage.onShownProperty().addListener(
        e -> Platform.runLater(() -> primaryStage.setResizable(false)));
    primaryStage.setScene(this.mainScene);
    primaryStage.show();
    mainScene.getStylesheets().add(
        getClass().getResource("dark.css").toExternalForm());
    Loggers.setupLoggers();
  }

  /**
   * The version of this build of talontrack.
   * 
   * @return String representing the version of talontrack.
   */
  public static String getVersion() {
    String version = Talontrack.class.getPackage().getImplementationVersion();
    if (version == null) {
      return "Your mother.";
    }
    return version;
  }
}
