package marsops.emergencies;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import marsops.agents.CrewMember;
import marsops.core.Agent;
import marsops.core.Capability;
import marsops.core.MarsColony;
import marsops.core.ResourceType;
import marsops.facilities.Laboratory;

// Owner: Person 4
public class FireEmergency extends Emergency {
    private final Laboratory affectedLab;
    private final int oxygenConsumedByFire;

    public FireEmergency(
        String emergencyId,
        Laboratory affectedLab,
        int oxygenConsumedByFire,
        Map<ResourceType, Integer> requiredResources) {
    super(emergencyId,(affectedLab != null ? affectedLab.getLocation() : null),requiredResources);

    if (affectedLab == null || oxygenConsumedByFire < 0) {
    throw new IllegalArgumentException("Invalid laboratory or oxygen consumption amount");}
    
    this.affectedLab = affectedLab;
    this.oxygenConsumedByFire = oxygenConsumedByFire;
}

    public Laboratory getAffectedLab() {return affectedLab;}
    public int getOxygenConsumedByFire() {return oxygenConsumedByFire;}

    
    @Override
    public void handle(MarsColony colony) {
        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null");}
    // 1. Find crew members in the affected laboratory.
    List<CrewMember> injuredCrew = new ArrayList<>();
        for (Agent agent : colony.getAgents()) {
        if (agent != null && location != null && location.equals(agent.getLocation()) && agent instanceof CrewMember) {
            injuredCrew.add((CrewMember) agent);}}

        Agent fireResponder = null;
        Agent medicalResponder = null;

        for (Agent agent : colony.getAgents()) {
            if (agent == null|| !agent.isAvailable()|| !agent.canRespond(this)) {continue;}
            if (fireResponder == null&& agent.hasCapability(Capability.ENGINEERING_REPAIR)) {fireResponder = agent;}
            if (!injuredCrew.isEmpty()&& medicalResponder == null&& agent.hasCapability(Capability.MEDICAL_CARE)) {medicalResponder = agent;}
        }

        if (fireResponder == null|| (!injuredCrew.isEmpty() && medicalResponder == null)) {
            markUnresolved("Required fire-response or medical personnel are unavailable");
            return;}

        if (!hasSufficientResources(colony)) {
            markUnresolved("Insufficient resources to extinguish the fire or treat the crew");
            return;}

        List<Agent> selectedResponders = new ArrayList<>();
        selectedResponders.add(fireResponder);
        if (medicalResponder != null&& !selectedResponders.contains(medicalResponder)) {
            selectedResponders.add(medicalResponder);}

        try {
    dispatchResponders(selectedResponders);

    // Apply fire consequences
    affectedLab.setOperational(false);
    affectedLab.setOnFire(true);
    affectedLab.damageAllEquipment();
    affectedLab.deductResource(ResourceType.OXYGEN, oxygenConsumedByFire);

    // Injure crew members present in the lab
    for (CrewMember patient : injuredCrew) {
        patient.takeDamage(25);}
    // Consume required resources
    consumeRequiredResources(colony);
    // Treat injured crew
    for (CrewMember patient : injuredCrew) {
        patient.heal(25);}
    // Finish handling the fire
    affectedLab.setOnFire(false);
    affectedLab.setOperational(true);
    markResolved();}
    finally {
        releaseResponders();}
    }

    @Override
    public String toString() {
        return String.format(
            "%s | Affected Lab: %s | Oxygen Consumed: %d",
            super.toString(),
            affectedLab.getName(),
            oxygenConsumedByFire);
}
}
