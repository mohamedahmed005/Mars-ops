package marsops.missions;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import marsops.core.Agent;
import marsops.core.EnvironmentCondition;
import marsops.core.MarsColony;
import marsops.core.MissionState;
import marsops.core.ResourceType;
import marsops.facilities.Facility;

public abstract class Mission {
    protected String missionId;
    protected MissionState state;
    protected int duration;
    protected int elapsedTime;
    protected String failureReason;
    protected List<Agent> assignedAgents;
    protected Facility targetFacility;
    protected Map<ResourceType, Integer> requiredResources;
    private boolean agentsReserved = false;

    public Mission(String missionId, int duration) {
        if (missionId == null || missionId.isBlank()) {
        throw new IllegalArgumentException("Mission id cannot be null or blank");
    }
    if (duration <= 0) {
        throw new IllegalArgumentException("Mission duration must be a positive number");
    }
    this.missionId = missionId.trim();
    this.duration = duration;
        this.elapsedTime = 0;
        this.state = MissionState.ASSIGNED;
        this.assignedAgents = new ArrayList<>();
        this.requiredResources = new EnumMap<>(ResourceType.class);   
    }

    public String getMissionId() {
        return missionId;
    }

    public MissionState getState() {
        return state;
    }

    public void setState(MissionState state) {
         if (state == null) {
        throw new IllegalArgumentException("Mission state cannot be null");
    }
    this.state = state;
    }

    public String getFailureReason() {
        return failureReason;
    }

    
    public int getDuration() {
        return duration;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public List<Agent> getAssignedAgents() {
        return new ArrayList<>(assignedAgents);
    }

    public Facility getTargetFacility() {
        return targetFacility;
    }

    public void setTargetFacility(Facility targetFacility) {
        this.targetFacility = targetFacility;
    }

    public Map<ResourceType, Integer> getRequiredResources() {
        return requiredResources;
    }

    public void addRequiredResource(ResourceType type, int amount) {
        public void addRequiredResource(ResourceType type, int amount) {
    if (type == null) {
        throw new IllegalArgumentException("Resource type cannot be null");
    }
    if (amount < 0) {
        throw new IllegalArgumentException("Resource amount cannot be negative");
    }
    requiredResources.put(type, amount);
}
    

    
    public abstract boolean validateRequirements(MarsColony colony);

    
    public void assignAgents(List<Agent> agents) {
        if (agents != null) {
            for (Agent agent : agents) {
                this.assignedAgents.add(agent);
            }
        }
    }

    
    public void reserveAgents() {
        for (Agent agent : assignedAgents) {
            if (agent != null) {
                agent.setAvailable(false);
            }
        }
        agentsReserved = true;
    }

    
    public void start() {
        if (this.state != MissionState.ASSIGNED) {
            throw new IllegalStateException("Mission " + missionId + " cannot start from state " + state);
        }
        this.state = MissionState.IN_PROGRESS;
    }

   
    public void advanceOneTimeUnit(EnvironmentCondition env) {
        if (this.state != MissionState.IN_PROGRESS) {
            return;
        }
        this.elapsedTime++;
        if (this.elapsedTime >= this.duration) {
            complete();
        }
    }
    public void onEnvironmentChange(EnvironmentCondition env) { }
    public void fail(String reason) {

        if (this.state == MissionState.COMPLETED || this.state == MissionState.FAILED) {
            return;
        }
        this.state = MissionState.FAILED;
                if (reason == null || reason.isBlank()) {
    reason = "No reason was given";
}
this.failureReason = reason.trim();
        if (agentsReserved) {
            releaseAgents();
        }
    }


    public void complete() {
        this.state = MissionState.COMPLETED;
        releaseAgents();
    }

    @Override
    public String toString() {
        return missionId + " [" + state + "]"
                + (failureReason != null ? " (reason: " + failureReason + ")" : "");
    }
    public void releaseAgents() {
        for (Agent agent : assignedAgents) {
            if (agent != null) {
                agent.setAvailable(true);
            }
        }
        agentsReserved = false;   
    }
}
