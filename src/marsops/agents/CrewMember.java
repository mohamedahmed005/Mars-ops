package marsops.agents;
// Owner: Person 2
import marsops.core.*;
import marsops.missions.*;
import marsops.emergencies.*;
import java.util.*;


public abstract class CrewMember implements Agent {
    protected String name;
    protected String id;
    protected int health;
    protected int fatigue;
    protected String location;
    protected boolean available;
    protected Set<Capability> skills;

    public CrewMember(String name, String id, int health, int fatigue, String location) {
        if (name == null || name.isBlank() || id == null || id.isBlank() || health <= 0 || health > 100 || fatigue < 0 || fatigue > 100 || location == null || location.isBlank()) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.id = id;
        this.health = health;
        this.fatigue = fatigue;
        this.location = location;
        this.available = true;
        this.skills = new HashSet<Capability>();
    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
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
        return this.available&& this.health > 0;

    }

    @Override
    public void setAvailable(boolean available) {
        this.available = available;

    }

    public int getHealth() {
        return this.health;
    }

    public int getFatigue() {
        return this.fatigue;
    }

    public Set<Capability> getSkills() {
        return this.skills;
    }

    @Override
    public boolean hasCapability(Capability capability) {
        return this.skills!=null&&this.skills.contains(capability);
    }

    @Override
    public abstract boolean canPerform(Mission mission);

    @Override
    public abstract boolean canRespond(Emergency emergency);

    public void increaseFatigue(int amount) {
        if (amount > 0) {
            this.fatigue = Math.min(100, this.fatigue + amount);
        }
    }

    public void heal(int amount) {
        if (amount > 0) {
            this.health = Math.min(100, this.health + amount);
        }
    }

    public void takeDamage(int amount) {
    if (amount > 0) {
        this.health = Math.max(0, this.health - amount);
    }
    }

    public abstract void performDuty();
}

