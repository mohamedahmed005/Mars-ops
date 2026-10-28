package marsops.facilities;

// Owner: Person 1
package marsops.emergencies;

import marsops.core.ResourceType;

// كلاس مؤقت داخل باكيدج الطوارئ حتى يرسل زميلك كلاس المنشآت النهائي
public abstract class Facility {
    protected String name;
    protected String location;
    protected boolean operational = true;

    public Facility(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public boolean isOperational() {
        return operational;
    }

    public void setOperational(boolean operational) {
        this.operational = operational;
    }

    public int deductResource(ResourceType type, int amount) {
        return amount;
    }
}