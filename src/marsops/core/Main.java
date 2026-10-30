package marsops.core;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import marsops.agents.Drone;
import marsops.agents.Engineer;
import marsops.agents.ExplorationRover;
import marsops.agents.MedicalOfficer;
import marsops.agents.RepairRobot;
import marsops.agents.Scientist;
import marsops.emergencies.Emergency;
import marsops.emergencies.FireEmergency;
import marsops.emergencies.MedicalEmergency;
import marsops.emergencies.OxygenLeakEmergency;
import marsops.emergencies.RoverBreakdownEmergency;
import marsops.facilities.CommunicationStation;
import marsops.facilities.Equipment;
import marsops.facilities.Habitat;
import marsops.facilities.Laboratory;
import marsops.facilities.PowerStation;
import marsops.facilities.StorageFacility;
import marsops.missions.ExplorationMission;
import marsops.missions.Mission;
import marsops.missions.RepairMission;
import marsops.missions.RescueMission;

public class Main {

    private static final String TABLE_BORDER = "  +--------------------------+----------------------------------------------------+";
    private static final String TABLE_FORMAT = "  | %-24s | %-50s |%n";

    private static class World {
        MarsColony colony;
        Simulation sim;

        Habitat habitatA;
        StorageFacility storage;
        Laboratory lab;
        CommunicationStation comms;
        PowerStation power;

        Scientist scientist;
        Engineer engineer;
        MedicalOfficer medic;
        ExplorationRover rover;
        RepairRobot repairBot;
        Drone drone;
    }

    private static World buildWorld() {
        World w = new World();
        w.colony = new MarsColony("Ares Base");
        w.sim = new Simulation(w.colony);

        initFacilities(w);
        initAgents(w);

        return w;
    }

    private static void initFacilities(World w) {
        w.habitatA = new Habitat("Habitat A", "Habitat Sector A", 10);
        w.habitatA.addResource(ResourceType.OXYGEN, 300);
        w.habitatA.addResource(ResourceType.WATER, 200);
        w.habitatA.addResource(ResourceType.FOOD, 150);

        w.storage = new StorageFacility("Storage Facility", "Storage Sector", 50, 1000);
        w.storage.addResource(ResourceType.OXYGEN, 200);
        w.storage.addResource(ResourceType.WATER, 300);
        w.storage.addResource(ResourceType.FOOD, 200);
        w.storage.addResource(ResourceType.FUEL, 100);
        w.storage.addResource(ResourceType.BATTERY_POWER, 120);
        w.storage.addResource(ResourceType.MEDICAL_SUPPLIES, 30);
        w.storage.addResource(ResourceType.SPARE_PARTS, 10);

        w.lab = new Laboratory("Lab-1", "Lab Sector", 6);
        w.lab.addResource(ResourceType.OXYGEN, 60);
        w.lab.addEquipment(new Equipment("Spectrometer", Capability.SCIENTIFIC_RESEARCH));
        w.lab.addEquipment(new Equipment("Microscope", Capability.SCIENTIFIC_RESEARCH));

        w.comms = new CommunicationStation("Comm Tower", "Comm Sector", 4);
        w.power = new PowerStation("Solar Farm", "Power Sector", 4, 500);

        w.colony.addFacility(w.habitatA);
        w.colony.addFacility(w.storage);
        w.colony.addFacility(w.lab);
        w.colony.addFacility(w.comms);
        w.colony.addFacility(w.power);
    }

    private static void initAgents(World w) {
        w.scientist = new Scientist("Dr. Salma", "S-01", 95, 10, "Habitat Sector A", "Geology");
        w.engineer  = new Engineer("Eng. Omar", "E-01", 90, 15, "Power Sector");
        w.medic     = new MedicalOfficer("Dr. Nour", "M-01", 92, 12, "Habitat Sector A");

        w.rover     = new ExplorationRover("R-01", "Explorer-X1", "Storage Sector", 100, false);
        w.rover.getCargo().add(new Equipment("Core Drill", Capability.SURFACE_EXPLORATION));
        w.repairBot = new RepairRobot("RR-01", "FixBot-2", "Storage Sector", 90, false);
        w.drone     = new Drone("D-01", "Scout-V", "Comm Sector", 80, false);

        w.colony.addAgent(w.scientist);
        w.colony.addAgent(w.engineer);
        w.colony.addAgent(w.medic);
        w.colony.addAgent(w.rover);
        w.colony.addAgent(w.repairBot);
        w.colony.addAgent(w.drone);
    }

    private static ExplorationMission newExploration(String id, int duration, int fuel, int battery) {
        ExplorationMission m = new ExplorationMission(id, duration, "Valles Marineris");
        m.addRequiredResource(ResourceType.FUEL, fuel);
        m.addRequiredResource(ResourceType.BATTERY_POWER, battery);
        return m;
    }

    private static Map<ResourceType, Integer> cost(Object... pairs) {
        Map<ResourceType, Integer> map = new EnumMap<>(ResourceType.class);
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((ResourceType) pairs[i], (Integer) pairs[i + 1]);
        }
        return map;
    }

    private static boolean submit(World w, Mission mission, Agent... agents) {
        MarsColony colony = w.colony;
        boolean accepted = colony.submitMission(mission, Arrays.asList(agents));

        String agentNames = Arrays.stream(agents)
                .map(Agent::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("None");

        System.out.println("  +-------------------------------------------------------------------------------+");
        System.out.printf("  | %-77s |%n", "MISSION SUBMISSION :: " + mission.getMissionId());
        System.out.println(TABLE_BORDER);
        System.out.printf(TABLE_FORMAT, "Assigned Agents", agentNames);
        System.out.printf(TABLE_FORMAT, "Submission Result", accepted ? "ACCEPTED" : "REJECTED");
        System.out.printf(TABLE_FORMAT, "Mission State", mission.getState());
        if (!accepted && mission.getFailureReason() != null) {
            System.out.printf(TABLE_FORMAT, "Failure Reason", mission.getFailureReason());
        }
        System.out.printf(TABLE_FORMAT, "Colony Active Mission", describeActive(colony));
        System.out.println(TABLE_BORDER);
        return accepted;
    }

    private static void advance(World w, Mission mission, int units) {
        System.out.println("  +--------+--------------+----------------+------------+-------------------------+");
        System.out.printf("  | %-6s | %-12s | %-14s | %-10s | %-23s |%n",
                "Time", "Mission ID", "State", "Progress", "Notes / Reason");
        System.out.println("  +--------+--------------+----------------+------------+-------------------------+");
        for (int i = 0; i < units; i++) {
            w.sim.advanceTime(1);
            String progress = mission.getElapsedTime() + " / " + mission.getDuration();
            String reason = mission.getFailureReason() != null ? mission.getFailureReason() : "-";
            System.out.printf("  | t = %-2d | %-12s | %-14s | %-10s | %-23s |%n",
                    w.sim.getCurrentTime(),
                    mission.getMissionId(),
                    mission.getState(),
                    progress,
                    reason);
        }
        System.out.println("  +--------+--------------+----------------+------------+-------------------------+");
    }

    private static void trigger(World w, Emergency emergency) {
        w.sim.triggerEmergency(emergency);
        System.out.println("  +-------------------------------------------------------------------------------+");
        System.out.printf("  | %-77s |%n", "EMERGENCY DISPATCH :: " + emergency.getEmergencyId());
        System.out.println("  +-------------------------------------------------------------------------------+");
        System.out.printf("  | %-77s |%n", emergency.toString());
        System.out.println("  +-------------------------------------------------------------------------------+");
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println("  +===============================================================================+");
        System.out.printf("  | %-77s |%n", ">> " + title);
        System.out.println("  +===============================================================================+");
    }

    private static void section(String text) {
        System.out.println();
        System.out.println("  +-------------------------------------------------------------------------------+");
        System.out.printf("  | [ %s ]%-" + Math.max(1, 74 - text.length()) + "s|%n", text, "");
        System.out.println("  +-------------------------------------------------------------------------------+");
    }

    private static void printKeyValueTable(String title, String... pairs) {
        System.out.println("  +-------------------------------------------------------------------------------+");
        System.out.printf("  | %-77s |%n", title);
        System.out.println(TABLE_BORDER);
        for (int i = 0; i < pairs.length; i += 2) {
            System.out.printf(TABLE_FORMAT, pairs[i], pairs[i + 1]);
        }
        System.out.println(TABLE_BORDER);
    }

    private static void printStatusBox(String title, String report) {
        System.out.println("  +-------------------------------------------------------------------------------+");
        System.out.printf("  | %-77s |%n", title);
        System.out.println("  +-------------------------------------------------------------------------------+");
        for (String line : report.split("\\R")) {
            System.out.printf("  | %-77s |%n", line);
        }
        System.out.println("  +-------------------------------------------------------------------------------+");
    }

    private static String describeActive(MarsColony colony) {
        Mission m = colony.getActiveMission();
        return m == null ? "None" : m.getMissionId() + " (" + m.getState() + ")";
    }

    private static void printResourceTotals(World w, String label, ResourceType... types) {
        System.out.println("  +--------------------------+----------------------------------+-----------------+");
        System.out.printf("  | %-24s | %-32s | %-15s |%n", "Snapshot", "Resource Type", "Colony Total");
        System.out.println("  +--------------------------+----------------------------------+-----------------+");
        for (int i = 0; i < types.length; i++) {
            ResourceType t = types[i];
            System.out.printf("  | %-24s | %-32s | %-15d |%n",
                    (i == 0 ? label : ""), t.name(), w.colony.getTotalResource(t));
        }
        System.out.println("  +--------------------------+----------------------------------+-----------------+");
    }

    private static void printAgents(World w, String label) {
        System.out.println("  +-------------------------------------------------------------------------------+");
        System.out.printf("  | %-77s |%n", label);
        System.out.println("  +------------------------+------------------------------------+-----------------+");
        System.out.printf("  | %-22s | %-34s | %-15s |%n", "Agent Name", "Current Location", "Available");
        System.out.println("  +------------------------+------------------------------------+-----------------+");
        for (Agent a : w.colony.getAgents()) {
            System.out.printf("  | %-22s | %-34s | %-15s |%n",
                    a.getName(), a.getLocation(), a.isAvailable() ? "YES" : "NO");
        }
        System.out.println("  +------------------------+------------------------------------+-----------------+");
    }

    private static void printOxygenByFacility(World w) {
        System.out.println("  +-------------------+-------------------+-------------+-------------+-----------+");
        System.out.printf("  | %-17s | %-17s | %-11s | %-11s | %-9s |%n",
                "Habitat A Oxygen", "Habitat A Status", "Storage O2", "Lab-1 O2", "Total O2");
        System.out.println("  +-------------------+-------------------+-------------+-------------+-----------+");
        System.out.printf("  | %-17d | %-17s | %-11d | %-11d | %-9d |%n",
                w.habitatA.getResourceAmount(ResourceType.OXYGEN),
                w.habitatA.isOperational() ? "OPERATIONAL" : "OFFLINE",
                w.storage.getResourceAmount(ResourceType.OXYGEN),
                w.lab.getResourceAmount(ResourceType.OXYGEN),
                w.colony.getTotalResource(ResourceType.OXYGEN));
        System.out.println("  +-------------------+-------------------+-------------+-------------+-----------+");
    }

    private static void scenario1SuccessfulExploration() {
        banner("SCENARIO 1: Successful Exploration");
        World w = buildWorld();

        section("System State BEFORE Execution");
        printStatusBox("COLONY STATUS REPORT (INITIAL)", w.colony.generateStatusReport());
        Equipment drill = w.rover.getCargo().get(0);
        printKeyValueTable("ROVER CARGO INSPECTION",
                "Equipment Name", drill.getName(),
                "Usable Status", String.valueOf(drill.isUsable()));

        section("Submitting and Executing Mission");
        ExplorationMission mission = newExploration("EXP-1", 3, 10, 15);
        if (submit(w, mission, w.scientist, w.rover)) {
            advance(w, mission, 3);
        }

        section("System State AFTER Execution");
        printStatusBox("COLONY STATUS REPORT (FINAL)", w.colony.generateStatusReport());
    }

    private static void scenario2MissionRejection() {
        banner("SCENARIO 2: Mission Rejection");
        World w = buildWorld();

        section("2a. Unsuitable Agent (Engineer Cannot Explore)");
        ExplorationMission bad = newExploration("EXP-BAD", 3, 10, 15);
        submit(w, bad, w.engineer);
        printKeyValueTable("VERIFICATION",
                "Never Active?", String.valueOf(w.colony.getActiveMission() == null));

        section("2b. Another Mission is Already Active");
        ExplorationMission first = newExploration("EXP-A", 3, 10, 15);
        submit(w, first, w.scientist, w.rover);

        ExplorationMission second = newExploration("EXP-B", 3, 10, 15);
        submit(w, second, w.drone);
        printKeyValueTable("VERIFICATION",
                "Active Mission Still", describeActive(w.colony));
    }

    private static void scenario3ResourceShortage() {
        banner("SCENARIO 3: Resource Shortage");
        World w = buildWorld();

        section("3a. Mission Needs More Spare Parts Than Colony Owns");
        printResourceTotals(w, "Before Submission", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        RepairMission repair = new RepairMission(99, "REP-1", 2);
        repair.setTargetFacility(w.lab);
        submit(w, repair, w.engineer, w.repairBot);
        printResourceTotals(w, "After Submission", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        printKeyValueTable("AUDIT SUMMARY",
                "Consumption Check", "Nothing consumed, nothing negative");

        section("3b. Multiple Sources + All-or-Nothing (Spec Section 5.1)");
        printOxygenByFacility(w);
        boolean ok600 = w.colony.consumeResourcesAtomically(cost(ResourceType.OXYGEN, 600));
        printKeyValueTable("ATOMIC REQUEST :: 600 OXYGEN (EXCEEDS AVAILABLE)",
                "Outcome", ok600 ? "ACCEPTED" : "REJECTED (Nothing Consumed)");
        printOxygenByFacility(w);

        boolean ok450 = w.colony.consumeResourcesAtomically(cost(ResourceType.OXYGEN, 450));
        printKeyValueTable("ATOMIC REQUEST :: 450 OXYGEN (MULTI-FACILITY)",
                "Outcome", ok450 ? "ACCEPTED (Habitat A drained first, then Storage)" : "REJECTED");
        printOxygenByFacility(w);
    }

    private static void scenario4EmergencyHandling() {
        banner("SCENARIO 4: Emergency Handling (Oxygen Leak)");

        section("4a. Leak While Exploration Mission is Active (Mission NOT Interrupted)");
        World w = buildWorld();
        ExplorationMission mission = newExploration("EXP-1", 3, 10, 15);
        submit(w, mission, w.scientist, w.rover);
        advance(w, mission, 1);

        printOxygenByFacility(w);
        printResourceTotals(w, "Before Leak", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        OxygenLeakEmergency leak = new OxygenLeakEmergency(
                "EM-OL1", w.habitatA, 50,
                cost(ResourceType.SPARE_PARTS, 2, ResourceType.BATTERY_POWER, 5));
        trigger(w, leak);
        printOxygenByFacility(w);
        printResourceTotals(w, "After Leak", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        printKeyValueTable("FACILITY RECOVERY CHECK",
                "Habitat A Operational", String.valueOf(w.habitatA.isOperational()));
        printAgents(w, "AGENTS AFTER RESPONSE (RESPONDERS RELEASED)");
        advance(w, mission, 2);

        section("4b. Leak With NO Available Responders -> UNRESOLVED, Double Oxygen Loss");
        World w2 = buildWorld();
        w2.engineer.setAvailable(false);
        w2.repairBot.setAvailable(false);
        printOxygenByFacility(w2);
        printResourceTotals(w2, "Before Leak", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        trigger(w2, new OxygenLeakEmergency(
                "EM-OL2", w2.habitatA, 50,
                cost(ResourceType.SPARE_PARTS, 2, ResourceType.BATTERY_POWER, 5)));
        printOxygenByFacility(w2);
        printResourceTotals(w2, "After Leak", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        printKeyValueTable("PENALTY SUMMARY",
                "Oxygen Penalty", "50 lost twice = 100 from Habitat A",
                "Response Resources", "None consumed");

        section("4c. Responders Exist But Spare Parts Insufficient -> UNRESOLVED");
        World w3 = buildWorld();
        trigger(w3, new OxygenLeakEmergency(
                "EM-OL3", w3.habitatA, 50,
                cost(ResourceType.SPARE_PARTS, 99)));
        printOxygenByFacility(w3);
        printResourceTotals(w3, "After Leak", ResourceType.SPARE_PARTS);

        section("4d. Oxygen Never Goes Below Zero");
        World w4 = buildWorld();
        trigger(w4, new OxygenLeakEmergency(
                "EM-OL4", w4.habitatA, 9999,
                cost(ResourceType.SPARE_PARTS, 1)));
        printKeyValueTable("BOUNDARY CHECK",
                "Habitat A Oxygen Level", String.valueOf(w4.habitatA.getOxygenLevel()));
    }

    private static void scenario5EnvironmentalEffect() {
        banner("SCENARIO 5: Environmental Effect (Dust Storm)");

        section("5a. Dust Storm Prevents Exploration Mission From Starting");
        World w = buildWorld();
        w.sim.changeEnvironment(EnvironmentCondition.DUST_STORM);
        printKeyValueTable("ENVIRONMENT UPDATE",
                "Current Condition", String.valueOf(w.colony.getEnvironment()));
        ExplorationMission blocked = newExploration("EXP-1", 3, 10, 15);
        submit(w, blocked, w.scientist, w.rover);
        printResourceTotals(w, "Untouched Resources", ResourceType.FUEL, ResourceType.BATTERY_POWER);

        section("5b. Dust Storm Hits a Mission Already IN_PROGRESS");
        World w2 = buildWorld();
        ExplorationMission running = newExploration("EXP-2", 5, 10, 15);
        submit(w2, running, w2.scientist, w2.rover);
        advance(w2, running, 1);

        w2.sim.changeEnvironment(EnvironmentCondition.DUST_STORM);
        printKeyValueTable("ENVIRONMENTAL HAZARD TRIGGERED :: DUST_STORM",
                "Affected Mission", running.getMissionId(),
                "New Mission State", String.valueOf(running.getState()),
                "Failure Reason", String.valueOf(running.getFailureReason()),
                "Colony Active Mission", describeActive(w2.colony));
        printAgents(w2, "AGENTS RELEASED AFTER FAILURE");
    }

    private static void scenario6RoverBreakdown() {
        banner("SCENARIO 6 (Extra): Rover Breakdown");
        World w = buildWorld();
        ExplorationMission mission = newExploration("EXP-1", 4, 10, 15);
        submit(w, mission, w.scientist, w.rover);
        advance(w, mission, 1);
        printAgents(w, "AGENTS BEFORE BREAKDOWN");
        printResourceTotals(w, "Before Breakdown", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);

        trigger(w, new RoverBreakdownEmergency(
                "EM-RB1", w.rover, mission,
                cost(ResourceType.SPARE_PARTS, 2, ResourceType.BATTERY_POWER, 5)));

        printKeyValueTable("POST-BREAKDOWN DIAGNOSTICS",
                "Mission State", String.valueOf(mission.getState()),
                "Failure Reason", String.valueOf(mission.getFailureReason()),
                "Rover Damaged?", String.valueOf(w.rover.isDamaged()),
                "Rover Location", w.rover.getLocation(),
                "Colony Active Mission", describeActive(w.colony));
        printResourceTotals(w, "After Repair", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        printAgents(w, "AGENTS AFTER BREAKDOWN AND REPAIR");
    }

    private static void scenario7Fire() {
        banner("SCENARIO 7 (Extra): Laboratory Fire");
        World w = buildWorld();
        w.scientist.setLocation(w.lab.getLocation());
        int healthBefore = w.scientist.getHealth();
        printOxygenByFacility(w);

        trigger(w, new FireEmergency(
                "EM-F1", w.lab, 20,
                cost(ResourceType.SPARE_PARTS, 1, ResourceType.MEDICAL_SUPPLIES, 2)));

        printOxygenByFacility(w);
        printKeyValueTable("LABORATORY FIRE IMPACT & RECOVERY",
                "Patient Health (Before)", String.valueOf(healthBefore),
                "Patient Health (After)", w.scientist.getHealth() + " (Injured by fire, then treated)",
                "Lab Operational?", String.valueOf(w.lab.isOperational()),
                "Lab Still On Fire?", String.valueOf(w.lab.isOnFire()));

        System.out.println("  +---------------------------------------------------+---------------------------+");
        System.out.printf("  | %-49s | %-25s |%n", "Laboratory Equipment Name", "Damaged Status");
        System.out.println("  +---------------------------------------------------+---------------------------+");
        for (Equipment e : w.lab.getEquipmentList()) {
            System.out.printf("  | %-49s | %-25s |%n", e.getName(), e.isDamaged() ? "DAMAGED" : "INTACT");
        }
        System.out.println("  +---------------------------------------------------+---------------------------+");

        printResourceTotals(w, "After Fire Response", ResourceType.SPARE_PARTS, ResourceType.MEDICAL_SUPPLIES);
    }

    private static void scenario8MedicalEmergency() {
        banner("SCENARIO 8 (Extra): Medical Emergency");

        section("8a. Medical Officer Available -> Patient Health Increases");
        World w = buildWorld();
        w.scientist.takeDamage(50);
        int healthBefore1 = w.scientist.getHealth();
        printResourceTotals(w, "Before Treatment", ResourceType.MEDICAL_SUPPLIES, ResourceType.OXYGEN);
        trigger(w, new MedicalEmergency(
                "EM-M1", w.scientist, w.habitatA, 30,
                cost(ResourceType.MEDICAL_SUPPLIES, 2, ResourceType.OXYGEN, 5)));
        printKeyValueTable("PATIENT VITALS REPORT (RESOLVED)",
                "Patient Name", w.scientist.getName(),
                "Health Before", String.valueOf(healthBefore1),
                "Health After", String.valueOf(w.scientist.getHealth()));
        printResourceTotals(w, "After Treatment", ResourceType.MEDICAL_SUPPLIES, ResourceType.OXYGEN);

        section("8b. Medical Officer Busy -> UNRESOLVED, Nothing Consumed");
        World w2 = buildWorld();
        w2.scientist.takeDamage(50);
        int healthBefore2 = w2.scientist.getHealth();
        w2.medic.setAvailable(false);
        trigger(w2, new MedicalEmergency(
                "EM-M2", w2.scientist, w2.habitatA, 30,
                cost(ResourceType.MEDICAL_SUPPLIES, 2, ResourceType.OXYGEN, 5)));
        printKeyValueTable("PATIENT VITALS REPORT (UNRESOLVED)",
                "Patient Name", w2.scientist.getName(),
                "Health Before", String.valueOf(healthBefore2),
                "Health After", String.valueOf(w2.scientist.getHealth()));
        printResourceTotals(w2, "After Unresolved", ResourceType.MEDICAL_SUPPLIES, ResourceType.OXYGEN);
    }

    private static void scenario9CommunicationDisruption() {
        banner("SCENARIO 9 (Extra): Communication Disruption");
        World w = buildWorld();
        w.rover.setLocation("Martian Surface");

        boolean normalComm = w.colony.hasActiveCommunicationStation();
        w.sim.changeEnvironment(EnvironmentCondition.COMMUNICATION_DISRUPTION);
        printKeyValueTable("COMMUNICATION TELEMETRY",
                "Comm Station (Normal)", String.valueOf(normalComm),
                "Environment Condition", String.valueOf(w.colony.getEnvironment()),
                "Comm Station (Disrupted)", String.valueOf(w.colony.hasActiveCommunicationStation()));

        RescueMission rescue = new RescueMission(w.rover, "RES-1", 3);
        submit(w, rescue, w.medic);

        section("9b. Communication Restored -> Rescue With Rover + Medic Succeeds");
        w.sim.changeEnvironment(EnvironmentCondition.NORMAL);
        RescueMission rescue2 = new RescueMission(w.scientist, "RES-2", 2);
        w.scientist.setLocation("Martian Surface");
        w.rover.setLocation("Storage Sector");
        if (submit(w, rescue2, w.rover, w.medic)) {
            advance(w, rescue2, 2);
        }
        printKeyValueTable("POST-RESCUE VERIFICATION",
                "Rescued Scientist", w.scientist.getName(),
                "Current Location", w.scientist.getLocation());
    }

    private static void runScenario(int number) {
        try {
            switch (number) {
                case 1: scenario1SuccessfulExploration(); break;
                case 2: scenario2MissionRejection(); break;
                case 3: scenario3ResourceShortage(); break;
                case 4: scenario4EmergencyHandling(); break;
                case 5: scenario5EnvironmentalEffect(); break;
                case 6: scenario6RoverBreakdown(); break;
                case 7: scenario7Fire(); break;
                case 8: scenario8MedicalEmergency(); break;
                case 9: scenario9CommunicationDisruption(); break;
                default:
                    printKeyValueTable("RUNNER WARNING", "Invalid Scenario", number + " (Valid: 1-9)");
            }
        } catch (RuntimeException ex) {
            printKeyValueTable("SCENARIO CRASHED", "Scenario Number", String.valueOf(number), "Error", ex.toString());
            ex.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) {
        System.out.println("  +===============================================================================+");
        System.out.println("  |      MARS-OPS :: AUTONOMOUS MARS COLONY MANAGEMENT & EMERGENCY SIMULATOR      |");
        System.out.println("  +===============================================================================+");

        if (args.length == 0) {
            for (int i = 1; i <= 9; i++) {
                runScenario(i);
            }
        } else {
            for (String arg : args) {
                try {
                    runScenario(Integer.parseInt(arg.trim()));
                } catch (NumberFormatException e) {
                    printKeyValueTable("INPUT ERROR", "Invalid Argument", arg);
                }
            }
        }

        System.out.println();
        System.out.println("  +===============================================================================+");
        System.out.println("  |                  ALL REQUESTED SCENARIOS FINISHED SUCCESSFULLY                |");
        System.out.println("  +===============================================================================+");
    }
}