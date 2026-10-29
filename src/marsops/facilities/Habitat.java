package marsops.facilities;

import marsops.core.ResourceType;

// Owner: Person 1
public class Habitat extends Facility {
    // TODO
    private final LifeSupportSystem lifeSupport;

    public Habitat(String name ,String location, int capacity){
        super(name, location, capacity);
        this.lifeSupport =new LifeSupportSystem();   
    }

    public int getOxygenLevel(){
      return this.getResourceAmount(ResourceType.OXYGEN);
    }
    
    public void reduceOxygen(int amount){
        deductResource(ResourceType.OXYGEN, amount);
    }

    public LifeSupportSystem getLifeSupport(){
        return lifeSupport;
    }
}
