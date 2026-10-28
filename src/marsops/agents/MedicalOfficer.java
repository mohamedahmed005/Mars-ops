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
    public void treat(CrewMember patient){
        if(patient==this){
            System.out.println("Medical officer cannot treat Himself");
            return;
        } if (patient == null) {
            System.out.println("Medical Officer " + this.name + " cannot treat");
            return;
        }

        if (!this.isAvailable()) {
            System.out.println("Medical Officer " + this.name +" is not available for treat the patient");
            return;
        }
        patient.heal(20);
        this.increaseFatigue(5);
        System.out.println("Medical Officer " + this.name + " successfully treat the patient ");


    }

    @Override
    public boolean canPerform(Mission mission) {
        return mission!=null&&this.isAvailable()&&this.hasCapability(Capability.MEDICAL_CARE);
    }

    @Override
    public boolean canRespond(Emergency emergency) {
        return emergency != null && this.isAvailable() && this.hasCapability(Capability.MEDICAL_CARE);
    }

    @Override
    public void performDuty() {
        System.out.println("Medical Officer " + this.name + " is taking care of his patients health");
    }
}