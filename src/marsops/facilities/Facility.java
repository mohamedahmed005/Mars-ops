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
                throw new RuntimeException("not valid!");
            }
            else{
                this.name=name;
            }
            

            if(location ==null || location.trim().isEmpty()){
                throw new RuntimeException("not valid!");
            }
            else{
                this.location=location;
            }


            if(capacity <= 0){
                throw new RuntimeException("not valid!");
            }
            else{
                this.capacity=capacity;
            }

            this.operational=true;
            this.resources =new EnumMap<>(ResourceType.class);
            this.equipmentList =new ArrayList<>();
        }

        public String getName(){
             return name;
        }

        public String getLocation(){
             return location;
        }

        public int getCapacity(){
             return capacity;
        }

        public List<Equipment> getEquipmentList(){
              return equipmentList;
        }

        public int getResourceAmount(ResourceType type){
            //resources.getOrDefault(key,defaultValue)
           return resources.getOrDefault(type,0);
        }

        public int deductResource(ResourceType type,int amount){
            if(amount <=0){
                return 0;
            }
         
            int available =getResourceAmount(type);

            if (amount >available){
              resources.put(type,0);
              return available;
            }
             else {
                 resources.put(type,available-amount);
                 return amount;
            }
        }

        public void addResource(ResourceType type, int amount){

            int available =getResourceAmount(type);
          if(amount >0){
            resources.put(type,available+amount);
          }
          else{
            return ;
          }
           
       }      
       public boolean isOperational(){
            return operational;
       }
       public void setOperational(boolean status){
         this.operational = status;
       }

       public void addEquipment(Equipment e){

      if (e ==null){
         throw new IllegalArgumentException("ERROR");
       }
       equipmentList.add(e);
      }
}