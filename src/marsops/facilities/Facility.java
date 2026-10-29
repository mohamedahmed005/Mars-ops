package marsops.facilities;


import java.util.Map;
import marsops.core.ResourceType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;


//Person 1

public abstract class Facility {
  protected String name;
  protected String location;
  protected int capacity;
  protected boolean operational;
  protected Map<ResourceType, Integer> resources;
  protected List<Equipment> equipmentList;

        protected Facility(String name ,String location, int capacity){

            if(name ==null || name.trim().isEmpty()){
                throw new IllegalArgumentException("not valid!");
            }
            

            if(location ==null || location.trim().isEmpty()){
                throw new IllegalArgumentException("not valid!");
            }

            if(capacity <= 0){
                throw new IllegalArgumentException("not valid!");
            }
            
            this.name=name;
            this.location=location;
            this.capacity=capacity;
            this.operational=true;
            this.resources =new EnumMap<>(ResourceType.class);
            this.equipmentList =new ArrayList<>();
        }

        public String getName(){
             return this.name;
        }

        public String getLocation(){
             return this.location;
        }

        public int getCapacity(){
             return this.capacity;
        }

        public List<Equipment> getEquipmentList(){
              return this.equipmentList;
        }

        public int getResourceAmount(ResourceType type){
            //resources.getOrDefault(key,defaultValue)
           return this.resources.getOrDefault(type,0);
        }

        public int deductResource(ResourceType type,int amount){
            if(amount <=0){
                return 0;
            }
         
            int available =this.getResourceAmount(type);

            if (amount >available){
              this.resources.put(type,0);
              return available;
            }
             else {
                 this.resources.put(type,available-amount);
                 return amount;
            }
        }

        public void addResource(ResourceType type, int amount){

            int available =this.getResourceAmount(type);
          if(amount >0){
            this.resources.put(type,available+amount);
          }
          else{
            return ;
          }
           
       }      
       public boolean isOperational(){
            return this.operational;
       }
       public void setOperational(boolean status){
         this.operational = status;
       }

       public void addEquipment(Equipment e){

      if (e ==null){
         throw new IllegalArgumentException("ERROR");
       }
       this.equipmentList.add(e);
      }
}