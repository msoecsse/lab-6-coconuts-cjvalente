package coconuts;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Scoreboard extends VBox {
    private Label scoreLabel;
    private int highScore;
    private int coconutsDestroyed;
    private int coconutsLanded;
    private long time_in_millis;
    private int time_seconds;
    private final File scoreFile = new File("coconuts/Scores.txt");

    public Scoreboard() {
        this.coconutsDestroyed = 0;
        this.coconutsLanded = 0;
        this.highScore = loadHighScore();
        scoreLabel = new Label("High Score: " + highScore);
        this.setSpacing(10);
        this.getChildren().add(scoreLabel);
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
}
