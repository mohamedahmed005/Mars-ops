package marsops.facilities;

// Owner: Person 1
public class LifeSupportSystem {
    

    private boolean active;
    private int filtrationEfficiency;

    public LifeSupportSystem(){
      
        this.active=true;
        this.filtrationEfficiency=100;

    }

    public void manageOxygen(){
    if (!active){
        System.out.println("Life support is inactive!");
        return;
    }
    System.out.println("Life support managing oxygen at "+ filtrationEfficiency +"% efficiency");
    }

    public boolean checkIntegrity(){
    return this.active && this.filtrationEfficiency >0;
    }

    public boolean isActive(){
    return this.active;
    }

     public void setActive(boolean active){
       this.active = active;
    }

    public int getFiltrationEfficiency(){
       return this.filtrationEfficiency;
    }

    public void setFiltrationEfficiency(int value){
    if (value < 0 || value > 100) {
        throw new IllegalArgumentException("filtrationEfficiency should be between 0 and 100");
    }
    this.filtrationEfficiency =value;
    }
}
