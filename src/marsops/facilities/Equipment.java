package marsops.facilities;

import marsops.core.Capability;

// Owner: Person 1
public class Equipment {
private String name;
private Capability providedCapability;
private boolean damaged;
public Equipment(String name, Capability providedCapability){
    if(name == null || name.trim().isEmpty()){
        throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if(providedCapability == null){
        throw new IllegalArgumentException("Provided capability cannot be null");
    }
    this.name = name;
    this.providedCapability = providedCapability;
    this.damaged = false;


}
public String getName(){
    return this.name;
}
public Capability getProvidedCapability(){
    return this.providedCapability;
}
public boolean isDamaged(){
    return this.damaged;
}
public void setDamaged(boolean damaged){
    this.damaged = damaged;
}
public boolean isUsable(){
    return !this.damaged;
}
public void repair(){
this.damaged = false;
}
}    
