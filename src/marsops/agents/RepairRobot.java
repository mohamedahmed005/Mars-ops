package marsops.agents;
// Owner: Person 2
import marsops.core.*;
import marsops.emergencies.*;
import marsops.missions.*;



public class RepairRobot extends RoboticUnit {
public RepairRobot(String id, String model, String location, int batteryLevel, boolean damaged) {
        super(id, model, location, batteryLevel, damaged);
        this.capabilities.add(Capability.ENGINEERING_REPAIR);
}
    @Override
    public boolean canPerform(Mission mission) {
        return mission != null && this.isAvailable() && this.hasCapability(Capability.ENGINEERING_REPAIR)&& mission instanceof RepairMission;
    }
    @Override
    public  boolean canRespond(Emergency emergency){
        return emergency != null && this.isAvailable() && this.hasCapability(Capability.ENGINEERING_REPAIR)&& (emergency instanceof OxygenLeakEmergency|| emergency instanceof FireEmergency|| emergency instanceof RoverBreakdownEmergency);
    }


}
