package marsops.facilities;

//Person 1
public class StorageFacility extends Facility {
   
    private int maxStorageVolume;

    public StorageFacility(String name ,String location, int capacity, int maxStorageVolume){

    super(name, location, capacity);

    if (maxStorageVolume <=0){
       throw new IllegalArgumentException("Storage Volume should be positive");
    }
        this.maxStorageVolume=maxStorageVolume;   
    }

     public int getMaxStorageVolume(){
        return this.maxStorageVolume;
    }
    
}
