package fr.ubx.poo.ubgarden.game.engine;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.decor.DoorNextClose;
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
    private final List<Wasps> wasps;
    private final List<Hornets> hornets;
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();

    private final Map<Position, Timer> nestWaspTimers = new HashMap<>();
    private final Map<Position, Timer> nestHornetTimers = new HashMap<>();
    private final Timer hornetTimer;
    private final Timer waspTimer;

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
        game.setGameEngine(this);


        this.wasps = game.getWasps();
        this.hornets = game.getHornets();

        this.hornetTimer = new Timer(game.configuration().hornetMoveFrequency() * 1000);
        this.waspTimer = new Timer(game.configuration().hornetMoveFrequency() * 1000);

        initialize();
        buildAndSetGameLoop();
    }

    public Pane getLayer() {
        return layer;
    }

    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }

    public Pane getRoot() {
        return rootPane;
    }

    private void initialize() {
        int height = game.world().getGrid().height();
        int width = game.world().getGrid().width();
        int sceneWidth = width * ImageResource.size;
        int sceneHeight = height * ImageResource.size;


        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());
        input = new Input(scene);


        root.getChildren().clear();
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight);

        rootPane.getChildren().clear();
        rootPane.setPrefSize(sceneWidth, sceneHeight + StatusBar.height);
        rootPane.getChildren().add(root);


        for (var decor : game.world().getGrid().values()) {
            sprites.add(SpriteFactory.create(layer, decor));
            decor.setModified(true);
            var bonus = decor.getBonus();
            if (bonus != null) {
                sprites.add(SpriteFactory.create(layer, bonus));
                bonus.setModified(true);
            }
        }

        for (Decor decor : game.world().getGrid().values()) {
            if (decor instanceof fr.ubx.poo.ubgarden.game.go.decor.NestWasp) {
                nestWaspTimers.put(decor.getPosition(), new Timer(5000)); // 5 secondes
            }
        }
        for (Decor decor : game.world().getGrid().values()) {
            if (decor instanceof fr.ubx.poo.ubgarden.game.go.decor.NestHornet) {
                nestHornetTimers.put(decor.getPosition(), new Timer(10000)); // 10 secondes
            }
        }

        sprites.add(new SpriteGardener(layer, gardener));

        for (Wasps wasp : wasps) {
            sprites.add(new SpriteWasp(layer, wasp));
        }

        for (Hornets hornet : hornets) {
            sprites.add(new SpriteHornet(layer, hornet));
        }

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
                wasp.interactWith(gardener);
            }
        }

        for (Hornets hornet : game.getHornets()) {
            if (hornet.getPosition().equals(gardener.getPosition())) {
                hornet.interactWith(gardener);
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
            game.world().getGrid().removeClosedDoors();
            rebuildSprites();
            sprites.addAll(game.world().getGrid().values().stream()
                    .filter(decor -> decor instanceof DoorNextOpened)
                    .map(decor -> SpriteFactory.create(layer, decor))
                    .toList());
        }

        game.checkGameState(gardener);
        if (game.isGameOver()) {
            gameLoop.stop();
            showMessage(game.isGameWon() ? "Game Won!" : "Game Over", game.isGameWon() ? Color.GREEN : Color.RED);
            return;
        }

        // Spawning Wasps
        for (Map.Entry<Position, Timer> entry : nestWaspTimers.entrySet()) {
            Position nestPos = entry.getKey();
            Timer timer = entry.getValue();
            timer.update(now);

            if (!timer.isRunning()) {
                List<Position> possiblePositions = new ArrayList<>();
                for (Direction dir : Direction.values()) {
                    Position candidate = dir.nextPosition(nestPos);
                    if (game.world().getGrid().inside(candidate)) {
                        Decor decor = game.world().getGrid().get(candidate);
                        boolean isGrass = decor instanceof fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
                        boolean noWasp = wasps.stream().noneMatch(w -> w.getPosition().equals(candidate));
                        if (isGrass && noWasp) {
                            possiblePositions.add(candidate);
                        }
                    }
                }

                if (!possiblePositions.isEmpty()) {
                    Random random = new Random();
                    Position spawnPos = possiblePositions.get(random.nextInt(possiblePositions.size()));
                    Wasps newWasp = new Wasps(game, spawnPos);
                    wasps.add(newWasp);
                    sprites.add(new SpriteWasp(layer, newWasp));

                    // Bonus bombe
                    addRandomBombNear(spawnPos);
                }

                timer.start();
            }




        }

        // Spawning Hornets
        for (Map.Entry<Position, Timer> entry : nestHornetTimers.entrySet()) {
            Position nestPos = entry.getKey();
            Timer timer = entry.getValue();
            timer.update(now);

            if (!timer.isRunning()) {
                // Créer un frelon directement dans son nid
                Hornets newHornet = new Hornets(game, nestPos);
                hornets.add(newHornet);
                sprites.add(new SpriteHornet(layer, newHornet));

                // 🔥 Chercher deux positions libres pour placer deux bombes
                List<Position> freePositions = new ArrayList<>();
                for (int x = 0; x < game.world().getGrid().width(); x++) {
                    for (int y = 0; y < game.world().getGrid().height(); y++) {
                        Position p = new Position(game.world().currentLevel(), x, y);
                        Decor decor = game.world().getGrid().get(p);
                        if (decor instanceof fr.ubx.poo.ubgarden.game.go.decor.ground.Grass && decor.getBonus() == null) {
                            freePositions.add(p);
                        }
                    }
                }

                Random random = new Random();
                for (int i = 0; i < 2 && !freePositions.isEmpty(); i++) {
                    Position selected = freePositions.remove(random.nextInt(freePositions.size()));
                    Decor target = game.world().getGrid().get(selected);
                    var bomb = new fr.ubx.poo.ubgarden.game.go.bonus.Bombe_insecticide(selected, target);
                    target.setBonus(bomb);
                    bomb.setModified(true);
                    sprites.add(SpriteFactory.create(layer, bomb));
                }

                timer.start();
            }
        }

        for (Wasps wasp : wasps) {
            if (!wasp.isDeleted()) wasp.update(now);
        }

        for (Hornets hornet : hornets) {
            if (!hornet.isDeleted()) hornet.update(now);
        }

        waspTimer.update(now);
        if (!waspTimer.isRunning()) {
            for (Wasps w : wasps) {
                if (!w.isDeleted()) w.update(now);
            }
            waspTimer.start();
        }

        hornetTimer.update(now);
        if (!hornetTimer.isRunning()) {
            for (Hornets h : hornets) {
                if (!h.isDeleted()) h.update(now);
            }
            hornetTimer.start();
        }

        if(gardener.getEnergy() <= 0){
            System.out.println("Le jardinier est mort ! Game Over.");
            game.endGame(false); // Terminer la partie en indiquant la défaite
        }
    }

    private void addRandomBombNear(Position center) {
        List<Position> bombPositions = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            Position bombPos = dir.nextPosition(center);
            if (game.world().getGrid().inside(bombPos)) {
                Decor bombDecor = game.world().getGrid().get(bombPos);
                if (bombDecor instanceof fr.ubx.poo.ubgarden.game.go.decor.ground.Grass &&
                        bombDecor.getBonus() == null) {
                    bombPositions.add(bombPos);
                }
            }
        }
        if (!bombPositions.isEmpty()) {
            Random random = new Random();
            Position selected = bombPositions.get(random.nextInt(bombPositions.size()));
            Decor target = game.world().getGrid().get(selected);
            var bomb = new fr.ubx.poo.ubgarden.game.go.bonus.Bombe_insecticide(selected, target);
            target.setBonus(bomb);
            bomb.setModified(true);
            sprites.add(SpriteFactory.create(layer, bomb));
        }
    }


    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                System.out.println("Sprite marqué pour suppression : " + sprite.getGameObject().getClass().getSimpleName());
                sprite.remove();
                cleanUpSprites.add(sprite);
            }
        });
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }


    private void render() {
        sprites.forEach(Sprite::updateImage);
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


    private void checkLevel() {
        if (game.isSwitchLevelRequested()) {
            int currentLevel = game.world().currentLevel();
            game.world().setCurrentLevel(game.getSwitchLevel());
            System.out.println("Changement de niveau vers " + game.getSwitchLevel());


            sprites.clear();
            cleanUpSprites.clear();
            layer.getChildren().clear();

            for (var decor : game.world().getGrid().values()){
                if (decor instanceof DoorPrevOpened && game.getSwitchLevel() > currentLevel) {
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


            game.clearSwitchLevel();
            initialize();
        }
    }

    private boolean allCarrotsCollected() {
        return game.world().getGrid().values().stream()
                .noneMatch(decor -> decor.getBonus() instanceof Carrots);
    }

    private void rebuildSprites() {
        for (Sprite sprite : sprites) {
            if (sprite.getGameObject() instanceof DoorNextClose) {
                sprite.remove();
                cleanUpSprites.add(sprite);
            }
        }
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }

}