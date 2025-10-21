package coconuts;

// https://stackoverflow.com/questions/42443148/how-to-correctly-separate-view-from-model-in-javafx

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

// This class manages the game, including tracking all island objects and detecting when they hit
public class OhCoconutsGameManager {
    private final Collection<IslandObject> allObjects = new LinkedList<>();
    private final Collection<HittableIslandObject> hittableIslandSubjects = new LinkedList<>();
    private final Collection<IslandObject> scheduledForRemoval = new LinkedList<>();
    private final Collection<LaserBeam> lasers = new ArrayList<>();
    private final HitEventSubject subject = new HitEventSubject();
    private final int height, width;
    private final int DROP_INTERVAL = 10;
    private final int MAX_TIME = 100;
    private Pane gamePane;
    private Crab theCrab;
    private Beach theBeach;
    private boolean isGameOver;
    /* game play */
    private int coconutsInFlight = 0;
    private int gameTick = 0;

    public OhCoconutsGameManager(int height, int width, Pane gamePane) {
        this.height = height;
        this.width = width;
        this.gamePane = gamePane;
        this.isGameOver = false;

        this.theCrab = new Crab(this, height, width);
        registerObject(theCrab);
        gamePane.getChildren().add(theCrab.getImageView());

        this.theBeach = new Beach(this, height, width);
        registerObject(theBeach);
        if (theBeach.getImageView() != null)
            System.out.println("Unexpected image view for beach");
    }

    private void registerObject(IslandObject object) {
        allObjects.add(object);
        if (object.isHittable()) {
            HittableIslandObject asHittable = (HittableIslandObject) object;
            hittableIslandSubjects.add(asHittable);
        }
    }

    public int getHeight() {
        return height;
    }

    public boolean getIsGameOver(){
        return isGameOver;
    }

    public int getWidth() {
        return width;
    }

    public void coconutDestroyed() {
        coconutsInFlight -= 1;
    }

    public void tryDropCoconut() {
        if (gameTick % DROP_INTERVAL == 0 && theCrab != null) {
            coconutsInFlight += 1;
            Coconut c = new Coconut(this, (int) (Math.random() * width));
            registerObject(c);
            gamePane.getChildren().add(c.getImageView());
        }
        gameTick++;
    }

    public Beach getBeach() {
        return theBeach;
    }

    public Crab getCrab() {
        return theCrab;
    }

    public void killCrab() {
        scheduleForDeletion(theCrab);
        theCrab = null;
    }

    public void laserShot(LaserBeam laser){
        gamePane.getChildren().add(laser.getImageView());
        registerObject(laser);
    }

    public void advanceOneTick() {
        scheduledForRemoval.clear();
        for (IslandObject o : allObjects) {
            o.step();
            o.display();

        }
        // see if objects hit; the hit itself is something you will add
        // you can't change the lists while processing them, so collect
        //   items to be removed in the first pass and remove them later
        scheduledForRemoval.clear();
        for (IslandObject thisObj : allObjects) {
            for (HittableIslandObject hittableObject : hittableIslandSubjects) {
                if (thisObj.canHit(hittableObject) && thisObj.isTouching(hittableObject)) {
                    if(thisObj.isGround() && hittableObject.isFalling()){
                        //isGround asks for beach and isFalling is only true for coconuts
                        Coconut coconut = (Coconut) hittableObject;
                        subject.coconutHitsGround(coconut);
                        scheduledForRemoval.add(coconut);
                    }
                    if(thisObj.isFalling() && (hittableObject.isGroundObject() && !hittableObject.isFalling())){
                        //asks if thisObj is coconut and hittableObject is crab
                        subject.crabDies((HitEventObservers) hittableObject);
                        subject.detach((HitEventObservers)  hittableObject);
                        isGameOver = true;
                        killCrab();
                    }
                    if(thisObj.isLaser() && hittableObject.isFalling()){
                        //asks if thisObj is laser and hittableobject is a coconut
                        subject.coconutDestroyed((HitEventObservers) hittableObject);
                        subject.detach((HitEventObservers)  hittableObject);
                    }
                    scheduledForRemoval.add(hittableObject);
                    gamePane.getChildren().remove(hittableObject.getImageView());
                }
            }
        }
        // actually remove the objects as needed
        for (IslandObject thisObj : scheduledForRemoval) {
            allObjects.remove(thisObj);
            if (thisObj.isHittable()) {
                hittableIslandSubjects.remove((HittableIslandObject) thisObj);
            }
        }
        scheduledForRemoval.clear();
    }

    public void addHitObserver(HitEventObservers o) {
        subject.attach(o);
    }

    public void scheduleForDeletion(IslandObject islandObject) {
        if (islandObject.isHittable()) {
            subject.detach((HitEventObservers) islandObject);
        }
        scheduledForRemoval.add(islandObject);
    }

    public boolean done() {
        return coconutsInFlight == 0 && gameTick >= MAX_TIME;
    }


}
