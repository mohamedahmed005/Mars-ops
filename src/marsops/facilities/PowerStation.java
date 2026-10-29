package marsops.facilities;

//Person 1
public class PowerStation extends Facility {
  private int powerOutput;

  public PowerStation (String name ,String location, int capacity, int powerOutput){

    super(name, location, capacity);
    if (powerOutput < 0)
       throw new IllegalArgumentException("powerOutput must not be negative");
    this.powerOutput=powerOutput;
  }

  public int generatePower(){
    if(operational){
       return this.powerOutput;
    }
    return 0;
  }
}
