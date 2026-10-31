package marsops.emergencies;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import marsops.agents.ExplorationRover;
import marsops.core.Agent;
import marsops.core.MarsColony;
import marsops.core.ResourceType;
import marsops.missions.Mission;
// Owner: Person 4
public class RoverBreakdownEmergency extends Emergency {
    private final ExplorationRover brokenRover;
    private final Mission affectedMission;

    
public RoverBreakdownEmergency(
        String emergencyId,
        ExplorationRover brokenRover,
        Mission affectedMission,
        Map<ResourceType, Integer> requiredResources) {
    super(emergencyId,"Martian Surface",requiredResources);
    if (brokenRover == null) {
        throw new IllegalArgumentException("Broken rover cannot be null");}
            this.brokenRover = brokenRover;
            this.affectedMission = affectedMission;
    }

    public ExplorationRover getBrokenRover() {return brokenRover;}
    public Mission getAffectedMission() {return affectedMission;}

    
    @Override
    public void handle(MarsColony colony) {
        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null");}

    
        brokenRover.setDamaged(true);
        brokenRover.setLocation(this.location);
        brokenRover.setAvailable(false);


        if (affectedMission != null) {
            affectedMission.fail("Rover broke down during exploration mission");
            if (colony.getActiveMission() == affectedMission) {
                colony.clearActiveMission();}}

        Agent repairResponder = null;
        for (Agent agent : colony.getAgents()) {
            if (agent != null && agent != brokenRover && agent.isAvailable()
                    && agent.canRespond(this)) {
                repairResponder = agent;
                break;}}

        if (repairResponder == null) {
            markUnresolved("No available repair responder to fix the broken rover");
            return;}


        if (!hasSufficientResources(colony)) {
            markUnresolved("Insufficient Spare Parts or Battery Power to repair the rover");
            return;}


        List<Agent> selectedResponders = new ArrayList<>();
        selectedResponders.add(repairResponder);

        try {
            dispatchResponders(selectedResponders);


            consumeRequiredResources(colony);


            brokenRover.setDamaged(false);
            brokenRover.setAvailable(true);

            markResolved();}
        finally {
            releaseResponders();}
    }

    @Override
    public String toString() {
        return String.format(
            "%s | Broken Rover: %s | Affected Mission: %s",
            super.toString(),
            brokenRover.getName(),
            affectedMission != null ? affectedMission.toString() : "None");
}
}