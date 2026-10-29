package marsops.facilities;

//Person 1
public class Laboratory extends Facility {
   private boolean onFire;

   public Laboratory(String name ,String location, int capacity){

    super(name, location, capacity);
    this.onFire=false;
   }

   public void damageAllEquipment(){
    for (Equipment e : equipmentList){
        e.setDamaged(true);
    }
    }

   public boolean isOnFire(){
    return onFire;
   }

   public void setOnFire(boolean onFire){
    this.onFire =onFire;
   }

}
