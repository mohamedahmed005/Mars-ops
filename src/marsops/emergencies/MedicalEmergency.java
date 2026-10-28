package marsops.emergencies;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import marsops.agents.CrewMember;
import marsops.core.Agent;
import marsops.core.MarsColony;
import marsops.core.ResourceType;
import marsops.facilities.Facility;

// Owner: Person 4
public class MedicalEmergency extends Emergency {
    private final CrewMember patient;
    private final Facility medicalFacility;
    private final int healthRestoreAmount;

    public MedicalEmergency(
        String emergencyId,
        CrewMember patient,
        Facility medicalFacility,
        int healthRestoreAmount,
        Map<ResourceType, Integer> requiredResources) {
    super(
            emergencyId,
            medicalFacility != null? medicalFacility.getLocation(): null,
            requiredResources);
        if (patient == null|| medicalFacility == null|| healthRestoreAmount <= 0) {
            throw new IllegalArgumentException(
            "Invalid patient, medical facility, or health restore amount");}
        this.patient = patient;
        this.medicalFacility = medicalFacility;
        this.healthRestoreAmount = healthRestoreAmount;
}

    public CrewMember getPatient() {return patient;}
    public Facility getMedicalFacility() {return medicalFacility;}
    public int getHealthRestoreAmount() {return healthRestoreAmount;}

    @Override
    public void handle(MarsColony colony) {
    if (colony == null) {
        throw new IllegalArgumentException("Colony cannot be null");}
    if (!medicalFacility.isOperational()) {
        markUnresolved("Medical facility is not operational");
        return;}
    Agent medicalResponder = null;
    for (Agent agent : colony.getAgents()) {
        if (agent != null&& agent.isAvailable()&& agent.canRespond(this)) {
            medicalResponder = agent;
                break;}}
        if (medicalResponder == null) {
            markUnresolved("No available Medical Officer to treat "+ patient.getName());
                return;}
        if (!hasSufficientResources(colony)) {
            markUnresolved("Insufficient Medical Supplies or Oxygen for treatment");
            return;}

    List<Agent> selectedResponders = new ArrayList<>();
    selectedResponders.add(medicalResponder);
    dispatchResponders(selectedResponders);
try {
    consumeRequiredResources(colony);
    patient.setLocation(medicalFacility.getLocation());
    patient.heal(healthRestoreAmount);
    markResolved();}
    finally {
    releaseResponders();}
}
}