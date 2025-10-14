package coconuts;

import javafx.scene.image.Image;

// Represents the object that shoots down coconuts but can be hit by coconuts. Killing the
//   crab ends the game
// This is a domain class; other than Image, do not introduce JavaFX or other GUI components here
public class Crab extends HittableIslandObject {
    private static final int WIDTH = 50; // assumption: height and width are the same
    private static final Image crabImage = new Image("file:images/crab-1.png");

    public Crab(OhCoconutsGameManager game, int skyHeight, int islandWidth) {
        super(game, islandWidth / 2, skyHeight, WIDTH, crabImage);
    }

    @Override
    public void step() {
        // do nothing
    }

    // Captures the crab crawling sideways
    public void crawl(int offset) {
        if (x < 550 && x > 5) {
            x += offset;
            display();
        } else if (x <= 5) {
            offset = Math.abs(offset);          //This method now makes sure crab stops at GUI edges.
            x += offset;
            display();
        } else {
            offset = Math.abs(offset);
            x -= offset;
            display();
        }
    }
}
