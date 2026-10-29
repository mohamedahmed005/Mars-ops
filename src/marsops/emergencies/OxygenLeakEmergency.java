package marsops.emergencies;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import marsops.agents.CrewMember;
import marsops.agents.RoboticUnit;
import marsops.core.Agent;
import marsops.core.MarsColony;
import marsops.core.ResourceType;
import marsops.facilities.Habitat;

// Owner: Person 4
public class OxygenLeakEmergency extends Emergency {
    private final Habitat affectedHabitat;
    private final int oxygenLossAmount;

        public OxygenLeakEmergency(
            String emergencyId,
            Habitat affectedHabitat,
            int oxygenLossAmount,
            Map<ResourceType, Integer> requiredResources) {
        super(emergencyId,affectedHabitat != null ? affectedHabitat.getLocation() : null,requiredResources);

        if (affectedHabitat == null || oxygenLossAmount < 0) {
            throw new IllegalArgumentException("Invalid habitat or oxygen loss amount");}
        this.affectedHabitat = affectedHabitat;
        this.oxygenLossAmount = oxygenLossAmount;
    }

    public Habitat getAffectedHabitat() {return affectedHabitat;}
    public int getOxygenLossAmount() {return oxygenLossAmount;}

    @Override
    public void handle(MarsColony colony) {
        if (colony == null) {
            throw new IllegalArgumentException("Colony cannot be null");}

        affectedHabitat.reduceOxygen(oxygenLossAmount);
        affectedHabitat.setOperational(false);

        Agent humanResponder = null;
        Agent robotResponder = null;

        for (Agent agent : colony.getAgents()) {
            if (agent != null && agent.isAvailable() && agent.canRespond(this)) {
                if (humanResponder == null && agent instanceof CrewMember) {
                    humanResponder = agent;}
                else if (robotResponder == null && agent instanceof RoboticUnit) {
                    robotResponder = agent;}
            }
        }

        if (humanResponder == null || robotResponder == null) {
        applyUnresolvedPenalty("Required responders unavailable");
        return;
    }

    if (!hasSufficientResources(colony)) {
        applyUnresolvedPenalty("Insufficient repair resources");
        return;
    }
    List<Agent> selectedResponders = new ArrayList<>();
    selectedResponders.add(humanResponder);
    selectedResponders.add(robotResponder);

    try {
    dispatchResponders(selectedResponders);
    consumeRequiredResources(colony);
    affectedHabitat.setOperational(true);
    markResolved();}
    finally {
        releaseResponders();}
    }

    private void applyUnresolvedPenalty(String reason) {
    affectedHabitat.reduceOxygen(oxygenLossAmount);
    affectedHabitat.setOperational(false);
    markUnresolved(reason);
}
    @Override
    public String toString() {
        return String.format(
            "%s | Affected Habitat: %s | Oxygen Loss: %d",
            super.toString(),
            affectedHabitat.getName(),
            oxygenLossAmount);
    }
}
