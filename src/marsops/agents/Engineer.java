package marsops.agents;
// Owner: Person 2
import marsops.core.Capability;
import marsops.missions.Mission;
import marsops.emergencies.Emergency;


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
        this.fatigue = Math.min(100, this.fatigue+5);
        System.out.println("Engineer " + this.name + " successfully repaired equipments for mission: " + mission);
    }
    @Override
    public boolean canPerform(Mission mission) {
        return mission != null && this.isAvailable() && hasAnyCapability(mission.getRequiredCapabilities());
    }

    @Override
    public boolean canRespond(Emergency emergency) {
        return emergency != null && this.isAvailable() && hasAnyCapability(emergency.getRequiredCapabilities());
    }

    @Override
    public void performDuty() {
        System.out.println("Engineer " + this.name + " is performing structural maintenance");
    }
}


