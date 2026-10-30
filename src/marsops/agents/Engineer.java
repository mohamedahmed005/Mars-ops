package marsops.agents;
// Owner: Person 2
import marsops.core.*;
import marsops.emergencies.*;
import marsops.missions.*;
public class Engineer extends CrewMember{
    public Engineer(String name, String id, int health, int fatigue, String location) {
        super(name, id, health, fatigue, location);
        this.skills.add(Capability.ENGINEERING_REPAIR);
    }

    public void repair(Mission mission) {
        if (mission == null) {
            System.out.println("Engineer " + this.name + " cannot perform repair: Mission is null");
            return;
        }

        if (!this.isAvailable()) {
            System.out.println("Engineer " + this.name +" is not available for repair duties");
            return;
        }
        this.increaseFatigue(5);
        System.out.println("Engineer " + this.name + " successfully repaired equipments for mission: " + mission);
    }
@Override
public boolean canPerform(Mission mission) {
    return mission != null && this.isAvailable()&& this.hasCapability(Capability.ENGINEERING_REPAIR) && mission instanceof RepairMission;
}

@Override
public boolean canRespond(Emergency emergency) {
    return emergency != null && this.isAvailable()&& this.hasCapability(Capability.ENGINEERING_REPAIR)&& (emergency instanceof OxygenLeakEmergency|| emergency instanceof FireEmergency|| emergency instanceof RoverBreakdownEmergency);
}

    @Override
    public void performDuty() {
        System.out.println("Engineer " + this.name + " is performing structural maintenance");
    }
}