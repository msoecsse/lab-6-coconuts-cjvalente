package coconuts;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Scanner;

public class Scoreboard extends VBox implements HitEventObservers{
    private Label scoreLabel;
    private int highScore;
    private int coconutsDestroyed;
    private int coconutsLanded;
    private long timeInMillis;
    private int time_seconds;
    private Label destroyedLabel;
    private Label landedLabel;
    private Label highScoreLabel;
    private Label timeLabel;
    private Instant startTime;
    private Thread timerThread;
    private boolean paused = false;
    private static final File scoreFile = new File("Scores.txt");
    private Timeline stopwatch;
    private int elapsedSeconds;
    private boolean running;

    public Scoreboard() {
        this.coconutsDestroyed = 0;
        this.coconutsLanded = 0;
        this.highScore = loadHighScore();
        this.timeInMillis = 0;
        this.running = false;
        buildScoreboard();
    }

    private void buildScoreboard() {
        destroyedLabel = new Label("Coconuts Destroyed: " + coconutsDestroyed);
        highScoreLabel = new Label("High Score: " + highScore);
        landedLabel = new Label("Coconuts Landed: " + coconutsLanded);
        timeLabel = new Label("Time: 0s");

        HBox topRow = new HBox(40, destroyedLabel, highScoreLabel, landedLabel);
        topRow.setAlignment(Pos.CENTER);
        topRow.setPadding(new Insets(10));

        // Bottom row for time
        HBox bottomRow = new HBox(timeLabel);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.setPadding(new Insets(5));

        // Combine into main VBox
        this.getChildren().addAll(topRow, bottomRow);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);

    }

    public int loadHighScore() {
        int score = 0;
        try (Scanner scanner = new Scanner(scoreFile)) {
            if (scanner.hasNextInt()) {
                score = scanner.nextInt();
            }
        } catch (IOException e) {
            System.err.println("Error loading high score: " + e.getMessage());
        }

        return score;
    }

    public int getHighScore() {
        return highScore;
    }

    private void saveHighScore() {
        try (PrintWriter pw = new PrintWriter(scoreFile)) {
            pw.println(highScore);
        } catch (FileNotFoundException e) {
            System.err.println("Error writing high score to file: " + highScore);
        }
    }


    public void setHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            saveHighScore();
            System.out.println("Saved new highscore to file: " + highScore);
        }
    }

    public void startTime(){
        running = true;
        if(!paused) {
            paused = false;
            elapsedSeconds = 0;
            timeLabel.setText("Time: 0s");

            stopwatch = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                elapsedSeconds++;
                timeLabel.setText("Time: " + elapsedSeconds + "s");
            }));
            stopwatch.setCycleCount(Timeline.INDEFINITE);
        }
        stopwatch.play();
    }

    public void pauseTimer(){
        if (stopwatch != null) {
            stopwatch.stop();
            paused = true;
        }
    }

    public void stopTimer() {
        if (stopwatch != null) {
            stopwatch.stop();
        }
    }

    public void resetTimer() {
        stopTimer();
        elapsedSeconds = 0;
        timeLabel.setText("Time: 0s");
    }

    @Override
    public void updateCoconutHitsGround() {
        if(running) {
            coconutsLanded++;
            landedLabel.setText("Coconuts Landed: " + coconutsLanded);
        }
    }

    @Override
    public void updateCrabDies() {
        stopTimer();
        running = false;
    }

    @Override
    public void updateCoconutDestroyed() {
        coconutsDestroyed++;
        destroyedLabel.setText("Coconuts Destroyed: " + coconutsDestroyed);

        if (coconutsDestroyed > highScore) {
            setHighScore(coconutsDestroyed);
            highScoreLabel.setText("High Score: " + highScore);
            if(!running) {
                saveHighScore();
            }
        }
    }
}
