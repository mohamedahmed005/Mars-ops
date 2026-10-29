package marsops.agents;
// Owner: Person 2
import marsops.core.*;
import marsops.missions.*;
import marsops.emergencies.*;
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
    return mission != null && this.isAvailable()&& this.hasCapability(Capability.SCIENTIFIC_RESEARCH)&& (mission instanceof ResearchMission || mission instanceof ExplorationMission);
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