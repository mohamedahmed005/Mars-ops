package marsops.emergencies;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import marsops.core.Agent;
import marsops.core.EmergencyState;
import marsops.core.MarsColony;
import marsops.core.ResourceType;


// Owner: Person 4
public abstract class Emergency {
    protected String emergencyId;
    protected String location;
    protected EmergencyState state;
    protected String unresolvedReason;
    protected List<Agent> responders;
    protected Map<ResourceType, Integer> requiredResources;
    

    public Emergency(String emergencyId, String location, Map<ResourceType, Integer> requiredResources) {
        if (emergencyId == null || emergencyId.isBlank()
        || location == null || location.isBlank()|| requiredResources == null) {
        throw new IllegalArgumentException("Invalid emergency data");}

        this.emergencyId = emergencyId;
        this.location = location;
        this.state = EmergencyState.TRIGGERED;
        this.unresolvedReason = null;
        this.responders = new ArrayList<>();
        this.requiredResources = new EnumMap<>(ResourceType.class);
        this.requiredResources.putAll(requiredResources);
    }

    // Getters
    public String getEmergencyId() {return emergencyId;}
    public String getLocation() {return location;}
    public EmergencyState getState() {return state;}
    public String getUnresolvedReason() {return unresolvedReason;}
    public List<Agent> getResponders() {return new ArrayList<>(responders);}
    public Map<ResourceType, Integer> getRequiredResources() {return new EnumMap<>(requiredResources);}

    //setters
    protected void setState(EmergencyState state) {this.state = state;}
    protected void setUnresolvedReason(String reason) {this.unresolvedReason = reason;}
    protected void markResolved() {
    state = EmergencyState.RESOLVED;
    unresolvedReason = null;}

    protected void markUnresolved(String reason) {
    state = EmergencyState.UNRESOLVED;
    unresolvedReason = reason;}

    public abstract void handle(MarsColony colony);


    // Dispatch responders
    protected void dispatchResponders(List<Agent> selectedResponders) {

    if (selectedResponders == null || selectedResponders.isEmpty()) {
        throw new IllegalArgumentException("Responders list cannot be null or empty");}
    // Validate all responders before dispatching any of them
    for (Agent agent : selectedResponders) {
        if (agent == null || !agent.isAvailable()) {
            throw new IllegalArgumentException("All responders must be available");}}
    // Dispatch responders
    for (Agent agent : selectedResponders)
        {
        agent.setAvailable(false);
        agent.setLocation(this.location);
        if (!responders.contains(agent)) {
            responders.add(agent);}}
    }

    // Release responders after the response is finished
    protected void releaseResponders() {
        for (Agent agent : responders)
        {
            agent.setAvailable(true);
        }
        responders.clear();
    }

    // Check if all required resources are available
    protected boolean hasSufficientResources(MarsColony colony) {
        for (Map.Entry<ResourceType, Integer> entry
                : requiredResources.entrySet())
        {
            ResourceType resource = entry.getKey();
            int requiredAmount = entry.getValue();
            if (colony.getTotalResource(resource) < requiredAmount) {
                return false;}
        }
        return true;
    }

    // Consume all required resources atomically
    protected void consumeRequiredResources(MarsColony colony)
    {
        boolean consumed =colony.consumeResourcesAtomically(requiredResources);
        if (!consumed) {
            throw new IllegalStateException("Insufficient resources to handle emergency");}
    }

    // Display emergency information
    @Override
    public String toString() {
        return String.format
        (
                "[%s] %s at %s | State: %s%s",
                emergencyId,
                getClass().getSimpleName(),
                location,
                state,
                (unresolvedReason != null
                        ? " | Reason: " + unresolvedReason
                        : "")
        );
    }
}        



