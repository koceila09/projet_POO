package fr.ubx.poo.ubgarden.game.engine;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.bonus.DoorNextClose;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
import fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;
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

import java.util.*;

public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final Game game;
    private final Gardener gardener;
    private final List<Wasps> wasps; // Liste des guêpes
    private final List<Hornets> hornets; // Liste des frelons
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    // Ajoute en haut dans les attributs privés
    private final Map<Position, Timer> nestWaspTimers = new HashMap<>();

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
        // Après avoir créé les sprites pour les décors et les bonus
        for (Decor decor : game.world().getGrid().values()) {
            if (decor instanceof fr.ubx.poo.ubgarden.game.go.decor.NestWasp) {
                nestWaspTimers.put(decor.getPosition(), new Timer(5000)); // 5 secondes
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
        gardener.update(now);

        if (allCarrotsCollected()) {
            removeClosedDoors();
            rebuildSprites();
        }


        game.checkGameState(gardener);

        if (game.isGameOver()) {
            gameLoop.stop();
            if (game.isGameWon()) {
                showMessage("Game Won!", Color.GREEN);
            } else {
                showMessage("Game Over", Color.RED);
            }
        }
        for (Map.Entry<Position, Timer> entry : nestWaspTimers.entrySet()) {
            Position nestPos = entry.getKey();
            Timer timer = entry.getValue();
            timer.update(now);

            if (!timer.isRunning()) {
                List<Position> possiblePositions = new ArrayList<>();

                // Chercher autour du nid (haut, bas, gauche, droite)
                Position up = new Position(nestPos.level(), nestPos.x(), nestPos.y() - 1);
                Position down = new Position(nestPos.level(), nestPos.x(), nestPos.y() + 1);
                Position left = new Position(nestPos.level(), nestPos.x() - 1, nestPos.y());
                Position right = new Position(nestPos.level(), nestPos.x() + 1, nestPos.y());

                List<Position> neighbors = Arrays.asList(up, down, left, right);

                for (Position pos : neighbors) {
                    if (game.world().getGrid().inside(pos)) {
                        var decor = game.world().getGrid().get(pos);

                        // Vérifier que c'est du Grass
                        boolean isGrass = decor instanceof fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;

                        // Vérifier qu'il n'y a PAS déjà une guêpe à cet endroit
                        boolean noWasp = wasps.stream().noneMatch(wasp -> wasp.getPosition().equals(pos));

                        if (isGrass && noWasp) {
                            possiblePositions.add(pos);
                        }
                    }
                }

                if (!possiblePositions.isEmpty()) {
                    Random random = new Random();
                    Position spawnPos = possiblePositions.get(random.nextInt(possiblePositions.size()));

                    Wasps newWasp = new Wasps(game, spawnPos);
                    wasps.add(newWasp);
                    sprites.add(new SpriteWasp(layer, newWasp));
                }

                timer.start(); // Redémarrer le timer
            }
        }
        Random random = new Random();
        for (Wasps wasp : wasps) {
            if (wasp.isDeleted())
                continue; // Si la guêpe est morte, on ne la déplace pas

            // Décider aléatoirement horizontal ou vertical
            boolean moveHorizontal = random.nextBoolean();

            Direction[] possibleDirections;
            if (moveHorizontal) {
                possibleDirections = new Direction[]{Direction.LEFT, Direction.RIGHT};
            } else {
                possibleDirections = new Direction[]{Direction.UP, Direction.DOWN};
            }

            Direction randomDirection = possibleDirections[random.nextInt(possibleDirections.length)];
            Position nextPos = randomDirection.nextPosition(wasp.getPosition());

            if (game.world().getGrid().inside(nextPos)) {
                var decor = game.world().getGrid().get(nextPos);

                boolean isGrass = decor instanceof fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
                boolean noWaspThere = wasps.stream().noneMatch(w -> w != wasp && w.getPosition().equals(nextPos));

                if (isGrass && noWaspThere) {
                    wasp.setPosition(nextPos);
                    wasp.setModified(true); // 🔥 Ajout obligatoire pour que le visuel soit mis à jour
                }
            }
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





    private void checkLevel() {
        if (game.isSwitchLevelRequested()) {
            game.world().setCurrentLevel(game.getSwitchLevel());
            System.out.println("Changement de niveau vers " + game.getSwitchLevel());

            // Nettoyer
            sprites.clear();
            cleanUpSprites.clear();
            layer.getChildren().clear();

            for (var decor : game.world().getGrid().values()){
                if (decor instanceof DoorPrevOpened){
                    gardener.setPosition(decor.getPosition());
                    break;
                }
                else {
                    for (var decor2 : game.world().getGrid().values()) {
                        if (decor2 instanceof DoorNextOpened) {
                            gardener.setPosition(decor2.getPosition());
                            break;
                        }
                    }
                }
            }

            // Mise à jour du monde
            game.clearSwitchLevel();
            initialize(); // Recharge les décors et les sprites
        }
    }




    private void removeClosedDoors() {
        var grid = game.world().getGrid();
        for (Decor decor : grid.values()) {
            var bonus = decor.getBonus();
            if (bonus instanceof DoorNextClose) {
                System.out.println("Toutes les carottes sont mangées, les portes sont ouvertes !");
                decor.setBonus(null); // enlever la porte fermée

                decor.setModified(true); // DEMANDER de redessiner ce décor
            }
        }
    }







    private boolean allCarrotsCollected() {
        return game.world().getGrid().values().stream()
                .noneMatch(decor -> decor.getBonus() instanceof Carrots);
    }

    private void rebuildSprites() {
        for (Sprite sprite : sprites) {
            if (sprite.getGameObject() instanceof fr.ubx.poo.ubgarden.game.go.bonus.DoorNextClose) {
                sprite.remove(); // supprimer seulement l'image de la porte fermée
                cleanUpSprites.add(sprite); // marquer pour suppression
            }
        }
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }




}