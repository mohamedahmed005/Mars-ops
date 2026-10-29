package marsops.facilities;

// Owner: Person 1
public class LifeSupportSystem {
    // TODO

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
    return active && filtrationEfficiency >0;
    }

    public boolean isActive(){
    return active;
    }

     public void setActive(boolean active){
       this.active = active;
    }

    public int getFiltrationEfficiency(){
       return filtrationEfficiency;
    }

    public void setFiltrationEfficiency(int value){
    if (value < 0 || value > 100) {
        throw new IllegalArgumentException("filtrationEfficiency should be between 0 and 100");
    }
    this.filtrationEfficiency =value;
    }
}
