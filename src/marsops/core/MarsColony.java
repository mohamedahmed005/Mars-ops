package marsops.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Owner: Person 3
public class MarsColony {
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
private List<Agent> agents = new ArrayList<>();
    public List<Agent> getAgents() {
        return new ArrayList<>(agents);
    }

    public void addAgent(Agent agent) {
        if (agent != null && !agents.contains(agent)) {
            agents.add(agent);
        }
    }
}
