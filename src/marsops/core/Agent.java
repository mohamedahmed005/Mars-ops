package marsops.core;
import marsops.missions.Mission;
import marsops.emergencies.Emergency;
// Owner: Shared: frozen after day 1

public interface Agent {
    public abstract String getId();
    public abstract String getName();
    public abstract String getLocation();
    public abstract void setLocation(String location);
    public abstract boolean isAvailable();
    public abstract void setAvailable(boolean available);
    public abstract boolean hasCapability(Capability capability);
    public abstract boolean canPerform(Mission mission);
    public abstract boolean canRespond(Emergency emergency);
}