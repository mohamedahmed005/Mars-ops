package marsops.missions;



import java.util.Map;
import marsops.core.Agent;
import marsops.core.Capability;
import marsops.core.EnvironmentCondition;
import marsops.core.MarsColony;
import marsops.core.MissionState;
import marsops.core.ResourceType;
import marsops.facilities.Equipment;
import marsops.facilities.Laboratory;

public class ResearchMission extends Mission {

    public ResearchMission(String missionId, int duration) {
        super(missionId, duration);
    }

    
    private boolean hasUsableScientificEquipment() {
        for (Equipment e : targetFacility.getEquipmentList()) {
            if (e.isUsable() && e.getProvidedCapability() == Capability.SCIENTIFIC_RESEARCH) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean validateRequirements(MarsColony colony) {

        if (targetFacility == null) {
            fail("No laboratory was set for the research.");
            return false;
        }
        if (!(targetFacility instanceof Laboratory)) {
            fail("The target facility is not a laboratory.");
            return false;
        }
        if (((Laboratory) targetFacility).isOnFire()) {
            fail("The laboratory is on fire.");
            return false;
        }
        if (!targetFacility.isOperational()) {
            fail("The laboratory is not operational.");
            return false;
        }

        if (assignedAgents.isEmpty()) {
            fail("No agents were assigned to the mission.");
            return false;
        }
        boolean hasquipment = hasUsableScientificEquipment();
        boolean hasScientist = false;
        for (Agent agent : assignedAgents) {
            if (!agent.isAvailable()) {
                fail("Agent " + agent.getName() + " is not available.");
                return false;
            }
            if (!agent.canPerform(this)) {
                fail("Agent " + agent.getName() + " does not possess the required capability.");
                return false;
            }
            if (agent.hasCapability(Capability.SCIENTIFIC_RESEARCH)) {
                hasScientist = true;
            }
        }
        if (!hasquipment) {
            fail("No suitable equipment for this research");
            return false;
        }
        if (!hasScientist) {
            fail("No agent with the required scientific skill was assigned.");
            return false;
        }

       
        for (Map.Entry<ResourceType, Integer> entry : requiredResources.entrySet()) {
            int available = colony.getTotalResource(entry.getKey());
            if (available < entry.getValue()) {
                fail("Insufficient " + entry.getKey() + ": required " + entry.getValue()
                        + ", available " + available + ".");
                return false;
            }
        }
        return true;
    }

   
    @Override
    public void start() {
        super.start();
        for (Agent agent : assignedAgents) {
            if (agent.hasCapability(Capability.SCIENTIFIC_RESEARCH) && targetFacility != null) {
                agent.setLocation(targetFacility.getLocation());
            }
        }
    }

    
    @Override
    public void advanceOneTimeUnit(EnvironmentCondition env) {
        if (state == MissionState.IN_PROGRESS && targetFacility != null) {
            if (!targetFacility.isOperational()) {
                fail("The laboratory stopped being operational during the research.");
                return;
            }
            if (!hasUsableScientificEquipment()) {
                fail("Equipment failure: no usable scientific equipment is left in the laboratory.");
                return;
            }
        }
        super.advanceOneTimeUnit(env);
    }
}
