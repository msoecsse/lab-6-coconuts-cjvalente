//done by CJ
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
import java.io.PrintWriter;
import java.util.Scanner;

public class Scoreboard extends VBox implements HitEventObservers{
    private int highScore;
    private int coconutsDestroyed;
    private int coconutsLanded;
    private int elapsedSeconds;
    private boolean running;
    private boolean paused = false;

    private Label destroyedLabel;
    private Label landedLabel;
    public Label highScoreLabel; // made public so GameController can refresh it
    private Label timeLabel;

    private Timeline stopwatch;
    private static final File scoreFile = new File(System.getProperty("user.dir"), "Scores.txt");

    public Scoreboard() {
        this.coconutsDestroyed = 0;
        this.coconutsLanded = 0;
        this.running = false;
        this.highScore = loadHighScore();
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
        System.out.println("Loading high score from: " + scoreFile.getAbsolutePath());
        if (!scoreFile.exists()) return 0;
        try (Scanner scanner = new Scanner(scoreFile)) {
            return scanner.hasNextInt() ? scanner.nextInt() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getHighScore() {
        return highScore;
    }

    public void saveHighScore() {
        System.out.println("Saving high score to: " + scoreFile.getAbsolutePath());
        try (PrintWriter pw = new PrintWriter(scoreFile)) {
            pw.println(highScore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
            saveHighScore();
            System.out.println("New high score: " + highScore);
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
        }
    }

    public int getCoconutsDestroyed() {
        return coconutsDestroyed;
    }
}
