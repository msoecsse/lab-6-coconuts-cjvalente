package coconuts;

public interface HitEventObservers {
    //crab, coconut, scoreboard, laser needs to implement this class

    void updateCoconutHitsGround();

    void updateCrabDies();

    void updateCoconutDestroyed();


}
