package marsops.missions;

import marsops.core.MissionState;

public class ExplorationMission extends Mission {
    private final String targetRegion;

    public ExplorationMission(String missionId, int duration, String targetRegion) {
        super(missionId, duration);
        this.targetRegion = targetRegion;
        this.state = MissionState.IN_PROGRESS;
    }

    public String getTargetRegion() {
        return targetRegion;
    }
}