package coconuts;

import javafx.scene.image.Image;

// Represents the beam of light moving from the crab to a coconut; can hit only falling objects
// This is a domain class; do not introduce JavaFX or other GUI components here
public class LaserBeam extends IslandObject implements HitEventObservers{
    private static final int WIDTH = 5; // must be updated with image
    private static final Image laserImage = new Image("file:../images/laser-1.png");
    private boolean coconutDestroyed;

    public LaserBeam(OhCoconutsGameManager game, int eyeHeight, int crabCenterX) {
        super(game, crabCenterX, eyeHeight, WIDTH, laserImage);
        coconutDestroyed = false;
    }

    public boolean isTouching(IslandObject other) {
        return  Math.abs(this.y - other.y) <= 5
                && this.x < other.x + other.width &&
                this.x + this.width > other.x;
    }

    public int hittable_height() {
        return y + WIDTH;
    }


    @Override
    public void step() {
        y -= 3;
    }


    @Override
    public boolean canHit(IslandObject other) {
        return other.isFalling();
    }

    @Override
    public void updateCoconutHitsGround() {

    }

    @Override
    public void updateCrabDies() {

    }

    @Override
    public void updateCoconutDestroyed() {
        coconutDestroyed = true;
    }

    @Override
    public boolean shouldBeRemoved() {
        return y + WIDTH < 0 || coconutDestroyed;
    }


    @Override
    public boolean isLaser() {
        return true;
    }


}
