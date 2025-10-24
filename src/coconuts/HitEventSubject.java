//done by Zoe
package coconuts;


import java.util.ArrayList;
import java.util.List;

// An abstraction of all objects that can be hit by another object
// This captures the Subject side of the Observer pattern; observers of the hit event will take action
//   to process that event
// This is a domain class; do not introduce JavaFX or other GUI components here
public class HitEventSubject {
    private List<HitEventObservers> observers;

    public HitEventSubject() {
        observers = new ArrayList<>();
    }

    public void attach(HitEventObservers observer){
        observers.add(observer);
    }

    public void detach(HitEventObservers observer){
        observers.remove(observer);
    }

    public void notifyObservers(int updateType, HitEventObservers source){
        List<HitEventObservers> observersCopy = new ArrayList<>(observers);
        for (HitEventObservers observer : observersCopy) {
            if (observer != source) {
                if (updateType == 1) {
                    observer.updateCoconutHitsGround();
                } else if (updateType == 2) {
                    observer.updateCrabDies();
                } else if (updateType == 3) {
                    observer.updateCoconutDestroyed();
                }
            }
        }

        //notify scoreboard to update, crab object, coconuts
    }

    //update scoreboard and make coconut disappear
    public void coconutHitsGround(HitEventObservers source){
        source.updateCoconutHitsGround();
        notifyObservers(1,source);
    }

    //notify scoreboard (stop time), crab
    public void crabDies(HitEventObservers source){
        source.updateCrabDies();
        notifyObservers(2, source);
    }

    //notify scoreboard, coconut, laser
    public void coconutDestroyed(HitEventObservers source){
        source.updateCoconutDestroyed();
        notifyObservers(3, source);
    }
}
