package marsops.missions;



import java.util.Map;
import marsops.agents.CrewMember;
import marsops.agents.RoboticUnit;
import marsops.core.Agent;
import marsops.core.Capability;
import marsops.core.MarsColony;
import marsops.core.ResourceType;
import marsops.facilities.Equipment;



public class RepairMission extends Mission {
        private final int spare_parts;
        private Equipment targetEquipment;
        
        public RepairMission(int spare_parts, String missionId, int duration) {
        super(missionId, duration);
        this.spare_parts = spare_parts;
        addRequiredResource(ResourceType.SPARE_PARTS, spare_parts);
    }
    

    public int getSpare_parts() {
        return spare_parts;
    }


    public Equipment getTargetEquipment() {
        return targetEquipment;
    }

    public void setTargetEquipment(Equipment targetEquipment) {
        this.targetEquipment = targetEquipment;
    }


    
    @Override
    public boolean validateRequirements(MarsColony colony) {

        if (targetFacility == null && targetEquipment == null) {
            fail("Nothing was set to repair: a target facility or target equipment is required.");
            return false;
        }
      
        if (assignedAgents.isEmpty()) {
            fail("No agents were assigned to the mission.");
            return false;
        }
        
        boolean hasrepairobot = false;
        boolean hasengineer = false;
        
        
        for (Agent agent : assignedAgents) {
            if (!agent.isAvailable()) {
                fail("Agent " + agent.getName() + " is not available.");
                return false;
            }
            if (!agent.canPerform(this)) {
                fail("Agent " + agent.getName() + " does not possess the required capability.");
                return false;
            }
            if (agent.hasCapability(Capability.ENGINEERING_REPAIR) && agent instanceof RoboticUnit) {
                hasrepairobot = true;
            }
            if (agent.hasCapability(Capability.ENGINEERING_REPAIR) && agent instanceof CrewMember) {
                hasengineer = true;
            }
        }
        if (!hasrepairobot) {
            fail("No repair robot was assigned: a robotic unit with repair capability is required.");
            return false;
        }
        if (!hasengineer) {
            fail("No engineer was assigned: a crew member with engineering skill is required.");
            return false;
        }

       
        for (Map.Entry<ResourceType, Integer> entry : requiredResources.entrySet()) {
            int available = colony.getTotalResource(entry.getKey());
            if (available < entry.getValue()) {
                fail("Insufficient " + entry.getKey() + ": required " + entry.getValue()
                        + ", available " + available + ".");
                return false;
            }
        }
        return true;
    }

    @Override
    public void start() {
        super.start();
        for (Agent agent : assignedAgents) {
            if (agent.hasCapability(Capability.ENGINEERING_REPAIR) && targetFacility != null) {
                agent.setLocation(targetFacility.getLocation());
            }
        }
    }

    @Override
    public void complete() {
        super.complete();
        if (targetFacility != null) {
            targetFacility.setOperational(true);
            for (Equipment e : targetFacility.getEquipmentList()) {
                e.repair();
            }
        }
        if (targetEquipment != null) {
            targetEquipment.repair();
        }
    }
}
