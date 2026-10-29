package marsops.agents;

// Owner: Person 2
import marsops.core.*;
import marsops.emergencies.*;
import marsops.missions.*;
public class Drone extends RoboticUnit {
   public Drone(String id, String model, String location, int batteryLevel, boolean damaged) {
        super(id, model, location, batteryLevel, damaged);
        this.capabilities.add(Capability.AERIAL_RECON);
    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission != null && this.isAvailable() && this.hasCapability(Capability.AERIAL_RECON)&& mission instanceof ExplorationMission;
    }

    @Override
    public boolean canRespond(Emergency emergency) {
        return false;
    }
}
