package fr.ubx.poo.ubgarden.game.engine;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Hornets;
import fr.ubx.poo.ubgarden.game.go.personage.Wasps;
import fr.ubx.poo.ubgarden.game.view.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import fr.ubx.poo.ubgarden.game.go.personage.Hornets;
import fr.ubx.poo.ubgarden.game.go.*;

import java.util.*;

public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final Game game;
    private final Gardener gardener;
    private final List<Wasps> wasps; // Liste des guêpes
    private final List<Hornets> hornets; // Liste des frelons
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();

    private final Scene scene;

    private StatusBar statusBar;

    private final Pane rootPane = new Pane();
    private final Group root = new Group();
    private final Pane layer = new Pane();
    private Input input;


    public GameEngine(Game game, Scene scene) {
        this.game = game;
        this.scene = scene;
        this.gardener = game.getGardener();

        // Initialiser la liste de guêpes
        this.wasps = game.getWasps();     // ⚠️ correction ici
        this.hornets = game.getHornets();


        initialize();
        buildAndSetGameLoop();
    }

    public Pane getRoot() {
        return rootPane;
    }

    private void initialize() {
        int height = game.world().getGrid().height();
        int width = game.world().getGrid().width();
        int sceneWidth = width * ImageResource.size;
        int sceneHeight = height * ImageResource.size;

        // Ajouter les styles CSS
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());
        input = new Input(scene);

        // Effacer et redimensionner le panneau principal
        root.getChildren().clear();
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight);

        rootPane.getChildren().clear();
        rootPane.setPrefSize(sceneWidth, sceneHeight + StatusBar.height);
        rootPane.getChildren().add(root);

        // Créer des sprites pour les décors et les bonus
        for (var decor : game.world().getGrid().values()) {
            sprites.add(SpriteFactory.create(layer, decor));
            decor.setModified(true);
            var bonus = decor.getBonus();
            if (bonus != null) {
                sprites.add(SpriteFactory.create(layer, bonus));
                bonus.setModified(true);
            }
        }

        // Ajouter un sprite pour le jardinier
        sprites.add(new SpriteGardener(layer, gardener));

        // Ajouter des sprites pour chaque guêpe
        for (Wasps wasp : wasps) {
            sprites.add(new SpriteWasp(layer, wasp));
        }

        // Ajouter des sprites pour chaque frelon
        for (Hornets hornet : hornets) {
            sprites.add(new SpriteHornet(layer, hornet));
        }

        // Redimensionner la scène
        resizeScene(sceneWidth, sceneHeight);
    }

    void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                checkLevel();
                processInput();
                gardener.update(now);
                update(now);
                checkCollision();
                cleanupSprites();
                render();
                statusBar.update(game);
            }
        };
    }

    private void checkLevel() {
        if (game.isSwitchLevelRequested()) {
            // TODO: Implémenter le changement de niveau
        }
    }

    private void checkCollision() {
        for (Wasps wasp : game.getWasps()) {
            if (wasp.getPosition().equals(gardener.getPosition())) {
                wasp.interactWith(gardener); // Interaction avec une guêpe
            }
        }

        for (Hornets hornet : game.getHornets()) {
            if (hornet.getPosition().equals(gardener.getPosition())) {
                hornet.interactWith(gardener); // Interaction avec un frelon
            }
        }
    }

    private void processInput() {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        } else if (input.isMoveDown()) {
            gardener.requestMove(Direction.DOWN);
        } else if (input.isMoveLeft()) {
            gardener.requestMove(Direction.LEFT);
        } else if (input.isMoveRight()) {
            gardener.requestMove(Direction.RIGHT);
        } else if (input.isMoveUp()) {
            gardener.requestMove(Direction.UP);
        }
        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text message = new Text(msg);
        message.setTextAlignment(TextAlignment.CENTER);
        message.setFont(new Font(60));
        message.setFill(color);

        StackPane pane = new StackPane(message);
        pane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
        rootPane.getChildren().clear();
        rootPane.getChildren().add(pane);

        new AnimationTimer() {
            public void handle(long now) {
                processInput();
            }
        }.start();
    }

    private void update(long now) {
        // Mettre à jour les informations du jardinier
        gardener.update(now);

        // Vérifier l'état de la partie
        game.checkGameState(gardener);

        // Si la partie est terminée, arrêter la boucle et afficher le message
        if (game.isGameOver()) {
            gameLoop.stop(); // Arrêter la boucle de jeu

            // Afficher le message correspondant
            if (game.isGameWon()) {
                showMessage("Game Won!", Color.GREEN); // Victoire
            } else {
                showMessage("Game Over", Color.RED); // Défaite
            }
            return; // Sortir de la méthode pour éviter d'exécuter le reste du code
        }
    }


    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                System.out.println("Sprite marqué pour suppression : " + sprite.getGameObject().getClass().getSimpleName());
                sprite.remove(); // ← Ajout direct ici
                cleanUpSprites.add(sprite);
            }
        });
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }


    private void render() {
        sprites.forEach(Sprite::updateImage); // ← mise à jour d'image ou suppression si deleted
        sprites.forEach(Sprite::render);
    }


    public void start() {
        gameLoop.start();
    }

    private void resizeScene(int width, int height) {
        rootPane.setPrefSize(width, height + StatusBar.height);
        layer.setPrefSize(width, height);
        Platform.runLater(() -> scene.getWindow().sizeToScene());
    }

    public void handle(long now) {
        checkLevel();
        processInput();

        gardener.update(now); // ⬅️ AJOUT ICI

        update(now);
        checkCollision();
        cleanupSprites();
        render();
        statusBar.update(game);
    }



}