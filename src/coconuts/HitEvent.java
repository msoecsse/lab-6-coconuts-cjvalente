package coconuts;

import java.util.List;

// An abstraction of all objects that can be hit by another object
// This captures the Subject side of the Observer pattern; observers of the hit event will take action
//   to process that event
// This is a domain class; do not introduce JavaFX or other GUI components here
public class HitEvent {
    private List<HitEventObservers> observers;

    public void attach(HitEventObservers observer){
        observers.add(observer);
    }

    public void detach(HitEventObservers observer){
        observers.remove(observer);
    }

    public void notifyObservers(int updateType){
        if(updateType == 1){
            for(HitEventObservers observer : observers){
                observer.updateCoconutHitsGround();
            }
        } else if(updateType == 2){
            for(HitEventObservers observer : observers){
                observer.updateCrabDies();
            }
        } else if(updateType == 3){
            for(HitEventObservers observer : observers){
                observer.updateCoconutDestoryed();
            }
        }

        //notify scoreboard to update, crab object, coconuts
    }

    //update scoreboard and make coconut disappear
    public void coconutHitsGround(){
        notifyObservers(1);
    }

    //notify scoreboard (stop time), crab
    public void crabDies(){
        notifyObservers(2);
    }

    //notify scoreboard, coconut, laser
    public void coconutDestroyed(){
        notifyObservers(3);
    }
}
