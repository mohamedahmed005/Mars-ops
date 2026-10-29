package marsops.facilities;

//Person 1
public class CommunicationStation extends Facility {

    private boolean signalActive;
   
    public CommunicationStation (String name ,String location, int capacity){

        super(name, location, capacity);
        this.signalActive=true;
    }
    
   public void setSignalActive(boolean signalActive){
     this.signalActive =signalActive;
     }

    public boolean isCommAvailable(){
       return operational && signalActive;
    }
}
