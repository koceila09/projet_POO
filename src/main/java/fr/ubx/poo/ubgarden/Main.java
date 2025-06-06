package fr.ubx.poo.ubgarden;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.launcher.MapLevelDefaultStart;
import fr.ubx.poo.ubgarden.game.view.GameLauncherView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage)  {
        GameLauncherView launcher = new GameLauncherView(stage);
        Scene scene = new Scene(launcher);
        stage.setTitle("UBGarden 2025");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        stage.toFront();
        stage.requestFocus();
    }


}