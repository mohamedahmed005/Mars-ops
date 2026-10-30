package marsops.missions;

import java.util.Map;
import marsops.core.Agent;
import marsops.core.Capability;
import marsops.core.EnvironmentCondition;
import marsops.core.MarsColony;
import marsops.core.MissionState;
import marsops.core.ResourceType;

public class ExplorationMission extends Mission {
    private final String targetRegion;

    public ExplorationMission(String missionId, int duration, String targetRegion) {
        super(missionId, duration);
        this.targetRegion = targetRegion;
    }

    public String getTargetRegion() {
        return targetRegion;
    }

    @Override
    public boolean validateRequirements(MarsColony colony) {
        if (colony.getEnvironment() == EnvironmentCondition.DUST_STORM) {
            fail("Surface operations are not allowed during a dust storm.");
            return false;
        }

    
        if (assignedAgents.isEmpty()) {
            fail("No agents were assigned to the mission.");
            return false;
        }
        boolean hasVehicle = false;
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
            if (agent.hasCapability(Capability.SURFACE_EXPLORATION)) {
                hasVehicle = true;
            }
            if (agent.hasCapability(Capability.SCIENTIFIC_RESEARCH)) {
                hasScientist = true;
            }
        }
        if (!hasVehicle) {
            fail("No suitable vehicle: an agent with surface exploration capability is required.");
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
            if (agent.hasCapability(Capability.SURFACE_EXPLORATION)) {
                agent.setLocation("Martian Surface");
            }
        }
    }

    @Override
    public void onEnvironmentChange(EnvironmentCondition env) {
            if (env == EnvironmentCondition.DUST_STORM && state == MissionState.IN_PROGRESS) {
            fail("Dust storm: active rover missions fail.");
        }
    }
    @Override
    public void advanceOneTimeUnit(EnvironmentCondition env) {
        if (env == EnvironmentCondition.DUST_STORM && state == MissionState.IN_PROGRESS) {
            fail("Dust storm: active rover missions fail.");
            return;
        }
        super.advanceOneTimeUnit(env);
    }
}