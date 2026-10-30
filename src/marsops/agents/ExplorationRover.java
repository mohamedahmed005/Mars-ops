package marsops.agents;
// Owner: Person 2
import java.util.*;
import marsops.core.*;
import marsops.emergencies.*;
import marsops.facilities.*;
import marsops.missions.*;

public class ExplorationRover extends RoboticUnit {
    private final List<Equipment> cargo;
    

    public ExplorationRover(String id, String model, String location, int batteryLevel, boolean damaged) {
        super(id, model, location, batteryLevel, damaged);
        this.capabilities.add(Capability.SURFACE_EXPLORATION);
        this.capabilities.add(Capability.HEAVY_TRANSPORT);
        this.cargo = new ArrayList<>();
    }

    public List<Equipment> getCargo() {
        return this.cargo;
    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission != null && this.isAvailable() && this.hasCapability(Capability.SURFACE_EXPLORATION)&&(mission instanceof ExplorationMission || mission instanceof RescueMission);
    }

    @Override
    public boolean canRespond(Emergency emergency) {
        return false;
    }
}