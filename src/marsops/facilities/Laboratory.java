package marsops.facilities;

// Owner: Person 1
public class Laboratory extends Facility {
    private boolean onFire = false;

public void setOnFire(boolean onFire) {
    this.onFire = onFire;
}

public boolean isOnFire() {
    return onFire;
}

public void damageAllEquipment() {
    // TODO: Implement equipment damage when the Equipment class is ready.
}


}
