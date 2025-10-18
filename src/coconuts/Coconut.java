package coconuts;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;

// Represents the falling object that can kill crabs. If hit by a laser, the coconut disappears
// This is a domain class; other than Image, do not introduce JavaFX or other GUI components here
public class Coconut extends HittableIslandObject implements HitEventObservers {
    private static final int WIDTH = 50;
    private Image coconutImage;
    OhCoconutsGameManager game;

    public Coconut(OhCoconutsGameManager game, int x) {
        super(game, x, 0, WIDTH, new Image("file:images/coco-1.png"));
        coconutImage = new Image("file:images/coco-1.png");
        this.game = game;
    }

    @Override
    public void step() {
        y += 5;
    }

    @Override
    public boolean isFalling() {
        return true;
    }

    @Override
    public boolean canHit(IslandObject other) {
        return other.isGroundObject() || other.isGround();
    }

    @Override
    public boolean isGroundObject() {
        return true;
    }

    public void updateCoconutHitsGround() {
        y = game.getBeach().getY();
        game.scheduleForDeletion(this);
        //wait 1 second and then make coconut disappear
    }

    public void updateCrabDies() {
        //coconuts don't do anything
    }

    public void updateCoconutDestroyed() {
        coconutImage = new Image("file:images/exploding_coco.png");
        Timeline oneSecond = new Timeline(
                new KeyFrame(Duration.seconds(1), _ -> {
                })
        );
        oneSecond.play();
        game.scheduleForDeletion(this);

    }
}
