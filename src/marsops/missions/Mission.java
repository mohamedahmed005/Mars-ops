package marsops.missions;

import java.util.ArrayList;
import java.util.List;
import marsops.core.Agent;
import marsops.core.MissionState;

public abstract class Mission {
    protected String missionId;
    protected MissionState state;
    protected int duration;
    protected int elapsedTime;
    protected String failureReason;
    protected List<Agent> assignedAgents;

    public Mission(String missionId, int duration) {
        this.missionId = missionId;
        this.duration = duration;
        this.elapsedTime = 0;
        this.state = MissionState.ASSIGNED;
        this.assignedAgents = new ArrayList<>();
    }

    public String getMissionId() {
        return missionId;
    }

    public MissionState getState() {
        return state;
    }

    public void setState(MissionState state) {
        this.state = state;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void start() {
        this.state = MissionState.IN_PROGRESS;
    }

    public void assignAgents(List<Agent> agents) {
        if (agents != null) {
            for (Agent agent : agents) {
                agent.setAvailable(false);
                this.assignedAgents.add(agent);
            }
        }
    }

    public void fail(String reason) {
        this.state = MissionState.FAILED;
        this.failureReason = reason;
        releaseAgents();
    }


    public void complete() {
        this.state = MissionState.COMPLETED;
        releaseAgents();
    }

    public void releaseAgents() {
        for (Agent agent : assignedAgents) {
            if (agent != null) {
                agent.setAvailable(true);
            }
        }
    }
}