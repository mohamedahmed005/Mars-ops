package marsops.facilities;

//Person 1
public class PowerStation extends Facility {
  private int powerOutput;

  public PowerStation (String name ,String location, int capacity, int powerOutput){

    super(name, location, capacity);
    this.powerOutput=powerOutput;
  }

  public int generatePower(){
    if(operational){
       return powerOutput;
    }
    return 0;
  }
}
