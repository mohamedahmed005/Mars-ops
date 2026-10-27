package marsops.agents;
// Owner: Person 2
import marsops.core.Capability;
import marsops.missions.Mission;
import marsops.emergencies.Emergency;
public class MedicalOfficer extends CrewMember  {
    public MedicalOfficer(String name, String id, int health, int fatigue, String location) {
        super(name, id, health, fatigue, location);
        this.skills.add(Capability.MEDICAL_CARE);
    }
    public void treat(CrewMember patient) {
        if (patient == null) {
            System.out.println("Medical Officer " + this.name + " cannot treat: patient is null");
            return;
        }
        if (patient == this) {
            System.out.println("Medical Officer " + this.name + " cannot treat himself");
            return;
        }
        if (!this.isAvailable()) {
            System.out.println("Medical Officer " + this.name + " is not available to treat the patient");
            return;
        }
        patient.heal(20);
        this.increaseFatigue(5);
        System.out.println("Medical Officer " + this.name + " successfully treated " + patient.getName());
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
        System.out.println("Medical Officer " + this.name + " is taking care of his patients health");
    }
}