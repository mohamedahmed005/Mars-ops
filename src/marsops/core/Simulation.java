package marsops.core;
import marsops.emergencies.Emergency;
import marsops.missions.Mission;

// Owner: Person 3
public class Simulation {
    private int currentTime;
    private final MarsColony colony;

    public Simulation(MarsColony colony) {
    if (colony == null) {
        throw new IllegalArgumentException("Colony cannot be null");}
    this.colony = colony;
    this.currentTime = 0;}

    public int getCurrentTime() {return currentTime;}
    public MarsColony getColony() {return colony;}

    public void advanceTime(int units) {
    if (units <= 0) {
        throw new IllegalArgumentException("Time units must be positive");}

    for (int i = 0; i < units; i++) {
        currentTime++;
        advanceOneTick();}
    }

    private void advanceOneTick() {
    Mission activeMission = colony.getActiveMission();
    if (activeMission != null) {
        if (activeMission.getState() == MissionState.ASSIGNED) {
            activeMission.start();}

        if (activeMission.getState() == MissionState.IN_PROGRESS) {
            activeMission.advanceOneTimeUnit(colony.getEnvironment());}

        if (activeMission.getState() == MissionState.COMPLETED
                || activeMission.getState() == MissionState.FAILED) {
            colony.clearActiveMission();}
        }
    }
    public void changeEnvironment(EnvironmentCondition condition) {
    if (condition == null) {
        throw new IllegalArgumentException("Environment condition cannot be null");}
    colony.setEnvironment(condition);

    Mission activeMission = colony.getActiveMission();
    if (activeMission != null && activeMission.getState() == MissionState.IN_PROGRESS) {
        activeMission.onEnvironmentChange(condition);
        if (activeMission.getState() == MissionState.FAILED) {
            colony.clearActiveMission();
        }
    }
}

    public void triggerEmergency(Emergency emergency) {
    if (emergency == null) {
        throw new IllegalArgumentException("Emergency cannot be null");}
    colony.handleEmergency(emergency);}
}
