package marsops.agents;
// Owner: Person 2
import marsops.core.Capability;
import marsops.missions.Mission;
import marsops.emergencies.Emergency;
public class Scientist extends CrewMember{
    private String researchSpecialty;

    public Scientist(String name, String id, int health, int fatigue, String location, String researchSpecialty) {
        super(name, id, health, fatigue, location);
        if(researchSpecialty == null||researchSpecialty.isBlank()){
            throw new IllegalArgumentException();
        }
        this.researchSpecialty = researchSpecialty;
        this.skills.add(Capability.SCIENTIFIC_RESEARCH);
    }

    public String getResearchSpecialty() {
        return this.researchSpecialty;
    }

    @Override
    public boolean canPerform(Mission mission) {
        if (mission == null || !this.isAvailable()) {
            return false;
        }
        for (Capability required : mission.getRequiredCapabilities()) {
            if (this.hasCapability(required)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canRespond(Emergency emergency) {
        return false;
    }

    @Override
    public void performDuty() {
        System.out.println("Scientist " + this.name + " is conducting research in " + this.researchSpecialty);
    }
}