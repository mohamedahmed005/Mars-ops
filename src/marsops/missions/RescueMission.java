package marsops.missions;

// Owner: Person 3

import java.util.Map;
import marsops.agents.CrewMember;
import marsops.core.Agent;
import marsops.core.Capability;
import marsops.core.EnvironmentCondition;
import marsops.core.MarsColony;
import marsops.core.MissionState;
import marsops.core.ResourceType;

public class RescueMission extends Mission {

    private Agent strandedEntity;
    private String rescueBase;

    public RescueMission(Agent strandedEntity, String missionId, int duration) {
        super(missionId, duration);
        this.strandedEntity = strandedEntity;
    }

    public Agent getStrandedEntity() {
        return strandedEntity;
    }

    
    private boolean usesSurfaceVehicle() {
        for (Agent agent : assignedAgents) {
            if (agent.hasCapability(Capability.SURFACE_EXPLORATION)) {
                return true;
            }
        }
        return false;
    }
    
    
    @Override
    public boolean validateRequirements(MarsColony colony) {
        
        
        if (colony.getEnvironment() == EnvironmentCondition.COMMUNICATION_DISRUPTION) {
            fail("rescue operations are not allowed during communication disturbance");
            return false;
        }

        if (strandedEntity == null) {
            fail("No stranded entity was specified for the rescue.");
            return false;
        }

        if (!colony.hasActiveCommunicationStation()) {
            fail("No operational communication station is available for the rescue.");
            return false;
        }

     
        if (assignedAgents.isEmpty()) {
            fail("No agents were assigned to the mission.");
            return false;
        }
        boolean hasVehicle = false;
        boolean hasPersonnel = false;
        
        for (Agent agent : assignedAgents) {
            if (agent == strandedEntity) {
                fail("The stranded entity cannot be one of its own rescuers.");
                return false;
            }
            if (!agent.isAvailable()) {
                fail("Agent " + agent.getName() + " is not available.");
                return false;
            }
            if (!agent.canPerform(this)) {
                fail("Agent " + agent.getName() + " does not possess the required capability.");
                return false;
            }
            if (agent.hasCapability(Capability.SURFACE_EXPLORATION)
                    || agent.hasCapability(Capability.HEAVY_TRANSPORT)) {
                hasVehicle = true;
            }
            if (agent instanceof CrewMember) {
                hasPersonnel = true;
            }
        }
        if (!hasVehicle) {
            fail("No suitable vehicle: an agent with surface exploration or heavy transport capability is required.");
            return false;
        }
        if (!hasPersonnel) {
            fail("No available personnel: at least one crew member is required for the rescue.");
            return false;
        }

        if (colony.getEnvironment() == EnvironmentCondition.DUST_STORM && usesSurfaceVehicle()) {
            fail("Surface operations are not allowed during a dust storm.");
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
        if (!assignedAgents.isEmpty()) {
            rescueBase = assignedAgents.get(0).getLocation();
        }
        if (strandedEntity != null) {
            for (Agent agent : assignedAgents) {
                agent.setLocation(strandedEntity.getLocation());
            }
        }
    }

    
    @Override
    public void complete() {
        super.complete();
        if (strandedEntity != null && rescueBase != null) {
            strandedEntity.setLocation(rescueBase);
            for (Agent agent : assignedAgents) {
                agent.setLocation(rescueBase);
            }
        }
    }
    
    @Override
    public void advanceOneTimeUnit(EnvironmentCondition env) {
        if (state == MissionState.IN_PROGRESS) {
            if (env == EnvironmentCondition.COMMUNICATION_DISRUPTION) {
                fail("communication disruption: communication for rescue failed");
                return;
            }
            if (env == EnvironmentCondition.DUST_STORM && usesSurfaceVehicle()) {
                fail("Dust storm: active rover missions fail.");
                return;
            }
        }
        super.advanceOneTimeUnit(env);
    }
}