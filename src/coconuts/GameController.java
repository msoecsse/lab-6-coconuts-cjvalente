package coconuts;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;

// JavaFX Controller class for the game - generally, JavaFX elements (other than Image) should be here
public class GameController {

    /**
     * Time between calls to step() (ms)
     */
    private static final double MILLISECONDS_PER_STEP = 1000.0 / 30;
    private Timeline coconutTimeline;
    private boolean started = false;

    @FXML
    private Pane gamePane;
    @FXML
    private Pane theBeach;
    @FXML
    private HBox scoreboardContainer;
    private OhCoconutsGameManager theGame;
    private Scoreboard scoreboard;

    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private AnimationTimer movementTimer;

    @FXML
    public void initialize() {
        theGame = new OhCoconutsGameManager((int) (gamePane.getPrefHeight() - theBeach.getPrefHeight() - scoreboardContainer.getPrefHeight() / 2),
                (int) (gamePane.getPrefWidth()), gamePane);

        gamePane.setFocusTraversable(true);
        scoreboard = new Scoreboard();
        scoreboard.setLayoutX(10);
        scoreboard.setLayoutY(10);
        scoreboardContainer.setPrefHeight(100);
        scoreboardContainer.getChildren().add(scoreboard);
        theGame.addHitObserver(scoreboard);


        coconutTimeline = new Timeline(new KeyFrame(Duration.millis(MILLISECONDS_PER_STEP), (e) -> {
            theGame.tryDropCoconut();
            theGame.advanceOneTick();
            if (theGame.done())
                coconutTimeline.pause();
        }));


        coconutTimeline.setCycleCount(Timeline.INDEFINITE);
        setupMovementHandlers();
    }

    @FXML
    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.SPACE) {
            if (!started) {
                coconutTimeline.play();
                started = true;
                scoreboard.startTime();
            } else if (theGame.getIsGameOver()) {
                coconutTimeline.pause();
                started = true;
                scoreboard.stopTimer();
            } else {
                coconutTimeline.pause();
                started = false;
                scoreboard.pauseTimer();

            }
        }


    }

    private void setupMovementHandlers() {
        gamePane.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        gamePane.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        movementTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (theGame == null || theGame.done()) {
                    return;
                }

                Crab crab = theGame.getCrab();
                int speed = 5;
                if (pressedKeys.contains(KeyCode.RIGHT)) {
                    crab.crawl(speed);
                }
                if (pressedKeys.contains(KeyCode.LEFT)) {
                    crab.crawl(-speed);
                }

                if (pressedKeys.contains(KeyCode.UP)) {
                    LaserBeam laser = crab.shootLaser();
                    Rectangle rect = new Rectangle(4, 20, Color.RED);
                    rect.setLayoutX(laser.getX());
                    rect.setLayoutY(laser.getY());
                    gamePane.getChildren().add(rect);
                    Timeline laserTimeline = new Timeline(new KeyFrame(Duration.millis(16), e -> {
                        laser.step();
                        rect.setLayoutY(laser.getY());
                        if (laser.shouldBeRemoved()) {
                            gamePane.getChildren().remove(rect);
                        }
                    }));
                    laserTimeline.setCycleCount(Timeline.INDEFINITE);
                    laserTimeline.play();
                }
            }
        };
        movementTimer.start();
    }
}
