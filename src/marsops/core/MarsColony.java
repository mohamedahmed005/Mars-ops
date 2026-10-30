package marsops.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import marsops.emergencies.Emergency;
import marsops.facilities.CommunicationStation;
import marsops.facilities.Facility;
import marsops.missions.Mission;

// Owner: Person 3
public class MarsColony {
    private final String name;
    private EnvironmentCondition environment;
    private Mission activeMission;
    private final List<Agent> agents = new ArrayList<>();
    private final List<Facility> facilities = new ArrayList<>();
    private final List<Mission> missionHistory = new ArrayList<>();
    private final List<Emergency> emergencies = new ArrayList<>();

    public MarsColony(String name) {
    if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("Colony name cannot be null or blank");
    }

    this.name = name;
    this.environment = EnvironmentCondition.NORMAL;
    this.activeMission = null;
}
    public String getName() {return name;}
    public EnvironmentCondition getEnvironment() {return environment;}
    public Mission getActiveMission() {return activeMission;}
    public List<Agent> getAgents() {return new ArrayList<>(agents);}
    public List<Facility> getFacilities() {return new ArrayList<>(facilities);}
    public List<Mission> getMissionHistory() {return new ArrayList<>(missionHistory);}
    public List<Emergency> getEmergencies() {return new ArrayList<>(emergencies);}

    public boolean hasActiveCommunicationStation() {
        if (environment == EnvironmentCondition.COMMUNICATION_DISRUPTION) {
            return false;}
        for (Facility facility : facilities) {
            if (facility instanceof CommunicationStation && facility.isOperational()) {
                return true;}
        }
        return false;
    }
    public int getTotalResource(ResourceType type) {
        if (type == null) {
            return 0;}
        int total = 0;
        for (Facility facility : facilities) {
            if (facility != null && facility.isOperational()) {
                total += facility.getResourceAmount(type);}
        }
        return total;
    }
    public void setEnvironment(EnvironmentCondition environment) {
        if (environment == null) {
            throw new IllegalArgumentException("Environment condition cannot be null");}
        this.environment = environment;
}
    public void setActiveMission(Mission activeMission) {
    this.activeMission = activeMission;}

    public void clearActiveMission() {
    this.activeMission = null;}

    public void addAgent(Agent agent) {
        if (agent != null && !agents.contains(agent)) {
            agents.add(agent);}
    }

    public void addFacility(Facility facility) {
    if (facility != null && !facilities.contains(facility)) {
        facilities.add(facility);}
    }

    public void consumeResource(ResourceType type, int amount) {
        if (type == null || amount < 0) {
            throw new IllegalArgumentException("Invalid resource type or amount");
    }
    if (amount == 0) {return;}
    if (getTotalResource(type) < amount) {
        throw new IllegalStateException("Insufficient " + type+ " across operational facilities");
    }
    int remaining = amount;
    for (Facility facility : facilities) {
        if (facility != null&& facility.isOperational()&& remaining > 0) {
            int available = facility.getResourceAmount(type);
            if (available > 0) {
                int toDeduct = Math.min(available, remaining);
                facility.deductResource(type, toDeduct);
                remaining -= toDeduct;
            }
        }
    }
}
public boolean consumeResourcesAtomically(
        Map<ResourceType, Integer> resources) {
    if (resources == null) {
        return false;}
    for (Map.Entry<ResourceType, Integer> entry
            : resources.entrySet()) {
        ResourceType type = entry.getKey();
        Integer reqAmount = entry.getValue();
        if (type == null || reqAmount == null || reqAmount < 0) {
            return false;}
        if (getTotalResource(type) < reqAmount) {
            return false;}
    }
    for (Map.Entry<ResourceType, Integer> entry: resources.entrySet()) {
        consumeResource(entry.getKey(), entry.getValue());}
    return true;}

    public void handleEmergency(Emergency emergency) {
        if (emergency == null) {
            throw new IllegalArgumentException("Emergency cannot be null");}
        emergencies.add(emergency);
        emergency.handle(this);}

    
public boolean submitMission(
        Mission mission,
        List<Agent> assignedAgents) {
        if (mission == null) {
            throw new IllegalArgumentException("Mission cannot be null");}
        missionHistory.add(mission);
        if (activeMission != null) {mission.fail("Another mission is already active");
            return false;}
        if (assignedAgents == null || assignedAgents.isEmpty()) {
            mission.fail("No agents assigned to the mission");
            return false;}

        for (Agent agent : assignedAgents) {
            if (agent == null || !agent.isAvailable()) {
                mission.fail("Agent unavailable");
                return false;}

        if (!agent.canPerform(mission)) {
            mission.fail("Agent does not possess the required capability");
            return false;}
        }
    mission.assignAgents(assignedAgents);

    // Mission-specific checks (environment, equipment, vehicle, resources).
    // validateRequirements() already calls mission.fail(reason) when it rejects.
    if (!mission.validateRequirements(this)) {
        return false;}

    // All-or-nothing consumption of the mission's resources.
    if (!consumeResourcesAtomically(mission.getRequiredResources())) {
        mission.fail("Insufficient resources to start the mission");
        return false;}

    // Agents stay busy until the mission completes or fails.
    mission.reserveAgents();
    this.activeMission = mission;
    return true;}

    public String generateStatusReport() {
    StringBuilder sb = new StringBuilder();

    sb.append("=== COLONY STATUS REPORT: ")
            .append(name).append(" ===\n");

    sb.append("Environment: ")
            .append(environment).append("\n");

    sb.append("Active Mission: ")
            .append(activeMission != null
                    ? activeMission.getMissionId()
                    + " (" + activeMission.getState() + ")"
                    : "None")
            .append("\n");

    sb.append("--- Colony-Wide Resources ---\n");

    for (ResourceType type : ResourceType.values()) {
        sb.append("  ")
                .append(type).append(": ")
                .append(getTotalResource(type))
                .append("\n");
    }

    sb.append("--- Facilities (")
            .append(facilities.size()).append(") ---\n");

    for (Facility f : facilities) {
        sb.append("  ")
                .append(f.getName())
                .append(" [").append(f.getLocation()).append("]")
                .append(" - Operational: ")
                .append(f.isOperational())
                .append("\n");
    }
    sb.append("--- Agents (")
            .append(agents.size()).append(") ---\n");
        for (Agent a : agents) {
            sb.append("  ")
                .append(a.getName())
                .append(" [").append(a.getLocation()).append("]")
                .append(" - Available: ")
                .append(a.isAvailable())
                .append("\n");
        }
    sb.append("--- Emergencies Handled (")
        .append(emergencies.size()).append(") ---\n");

    for (Emergency e : emergencies) {
        sb.append("  ")
                .append(e.toString())
                .append("\n");
    }
    return sb.toString();
}

}

