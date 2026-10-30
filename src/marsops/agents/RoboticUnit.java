package marsops.agents;
// Owner: Person 2
import java.util.EnumSet;
import java.util.Set;
import marsops.core.Agent;
import marsops.core.Capability;
import marsops.emergencies.Emergency;
import marsops.missions.Mission;

public abstract class RoboticUnit implements Agent {
    protected String id;
    protected String model;
    protected String location;
    protected int batteryLevel;
    protected boolean damaged;
    protected boolean available;
    protected Set<Capability> capabilities;

    public RoboticUnit(String id, String model, String location, int batteryLevel, boolean damaged) {
        if (id == null || id.isBlank()
                || model == null || model.isBlank()
                || batteryLevel < 0 || batteryLevel > 100
                || location == null || location.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.model = model;
        this.location = location;
        this.batteryLevel = batteryLevel;
        this.damaged = damaged;
        this.available = true;
        this.capabilities = EnumSet.noneOf(Capability.class);
    }

    public int getBatteryLevel() {
        return this.batteryLevel;
    }

    public boolean isDamaged() {
        return this.damaged;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.model;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.location = location;
    }

    @Override
    public boolean isAvailable() {
        return this.available && !this.damaged && this.batteryLevel > 0;
    }

    @Override
    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean hasCapability(Capability capability) {
        return this.capabilities != null && this.capabilities.contains(capability);
    }

    @Override
    public abstract boolean canPerform(Mission mission);

    @Override
    public abstract boolean canRespond(Emergency emergency);

    public void consumeBattery(int amount) {
        if (amount > 0) {
            this.batteryLevel = Math.max(this.batteryLevel - amount, 0);
        }
    }

    public void recharge(int amount) {
        if (amount > 0) {
            this.batteryLevel = Math.min(100, this.batteryLevel + amount);
        }
    }

    public void setDamaged(boolean status) {
        this.damaged = status;
    }

    public Set<Capability> getCapabilities() {
        return this.capabilities;
    }

    public String getModel() {
        return this.model;
    }
}