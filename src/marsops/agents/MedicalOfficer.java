package marsops.agents;
// Owner: Person 2
import marsops.core.*;
import marsops.emergencies.*;
import marsops.missions.*;
public class MedicalOfficer extends CrewMember  {
    public MedicalOfficer(String name, String id, int health, int fatigue, String location) {
        super(name, id, health, fatigue, location);
        this.skills.add(Capability.MEDICAL_CARE);
    }
    
@Override
public boolean canPerform(Mission mission) {
    return mission != null && this.isAvailable()&& this.hasCapability(Capability.MEDICAL_CARE)&& mission instanceof RescueMission;
}


@Override
public boolean canRespond(Emergency emergency) {
    return emergency != null && this.isAvailable()&& this.hasCapability(Capability.MEDICAL_CARE)&& (emergency instanceof MedicalEmergency || emergency instanceof FireEmergency);
}

    @Override
    public void performDuty() {
        System.out.println("Medical Officer " + this.name + " is taking care of his patients health");
    }
}