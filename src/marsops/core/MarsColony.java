package marsops.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import marsops.facilities.Facility;
import marsops.missions.Mission;

// Owner: Person 3
public class MarsColony {
    private String name;
    private Mission activeMission;
    private List<Agent> agents = new ArrayList<>();
    private List<Facility> facilities = new ArrayList<>();

    public MarsColony(String name) {
        this.name = name;
        this.activeMission = null;
    }

    public Mission getActiveMission() {
        return activeMission;
    }

    public void setActiveMission(Mission activeMission) {
        this.activeMission = activeMission;
    }

    // TODO
    public int getTotalResource(ResourceType type) {
    throw new UnsupportedOperationException(
            "getTotalResource() is not implemented yet"
    );
}

public void consumeResource(ResourceType type, int amount) {
    throw new UnsupportedOperationException(
            "consumeResource() is not implemented yet"
    );
}

public boolean consumeResourcesAtomically(
        Map<ResourceType, Integer> resources) {

    throw new UnsupportedOperationException(
            "consumeResourcesAtomically() is not implemented yet"
    );
}
    public List<Agent> getAgents() {
        return new ArrayList<>(agents);
    }

    public void addAgent(Agent agent) {
        if (agent != null && !agents.contains(agent)) {
            agents.add(agent);
        }
    }

    public void clearActiveMission() {
        this.activeMission = null;
    }
}

