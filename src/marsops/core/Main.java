package marsops.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import marsops.agents.Drone;
import marsops.agents.Engineer;
import marsops.agents.ExplorationRover;
import marsops.agents.MedicalOfficer;
import marsops.agents.RepairRobot;
import marsops.agents.Scientist;
import marsops.emergencies.Emergency;
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

/**
 * MARS-OPS demo runner.
 * Usage: no arguments = run all scenarios, or pass scenario numbers (e.g. 1 4 6).
 */
public class Main {

    // ------------------------------------------------------------------
    // Layout helpers (every box is the same width)
    // ------------------------------------------------------------------
    private static final int WIDTH = 78;
    private static final int KEY_WIDTH = 26;

    private static String line(char c) {
        return "  +" + String.valueOf(c).repeat(WIDTH) + "+";
    }

    private static void row(String text) {
        int max = WIDTH - 2;
        String rest = text == null ? "" : text;
        do {
            String part = rest.length() > max ? rest.substring(0, max) : rest;
            rest = rest.substring(part.length());
            System.out.printf("  | %-" + max + "s |%n", part);
        } while (!rest.isEmpty());
    }

    private static void kv(String key, Object value) {
        row(String.format("%-" + KEY_WIDTH + "s : %s", key, value));
    }

    private static void banner(String title) {
        System.out.println();
        System.out.println(line('='));
        row(">> " + title);
        System.out.println(line('='));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println(line('-'));
        row("[ " + title + " ]");
        System.out.println(line('-'));
    }

    private static void box(String title, String... pairs) {
        System.out.println(line('-'));
        row(title);
        System.out.println(line('-'));
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            kv(pairs[i], pairs[i + 1]);
        }
        System.out.println(line('-'));
    }

    private static void reportBox(String title, String report) {
        System.out.println(line('-'));
        row(title);
        System.out.println(line('-'));
        for (String l : report.split("\\R")) {
            row(l);
        }
        System.out.println(line('-'));
    }

    // ------------------------------------------------------------------
    // World setup
    // ------------------------------------------------------------------
    private static class World {
        MarsColony colony;
        Simulation sim;

        Habitat habitat;
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
        w.colony = new MarsColony("Ares Base Alpha");
        w.sim = new Simulation(w.colony);

        w.habitat = new Habitat("Habitat A", "Habitat Sector", 10);
        w.habitat.addResource(ResourceType.OXYGEN, 300);
        w.habitat.addResource(ResourceType.WATER, 200);
        w.habitat.addResource(ResourceType.FOOD, 150);

        w.storage = new StorageFacility("Main Storage", "Storage Sector", 100, 1000);
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

        w.colony.addFacility(w.habitat);
        w.colony.addFacility(w.storage);
        w.colony.addFacility(w.lab);
        w.colony.addFacility(w.comms);
        w.colony.addFacility(w.power);

        w.scientist = new Scientist("Dr. Salma", "S-01", 95, 10, "Habitat Sector", "Geology");
        w.engineer = new Engineer("Eng. Omar", "E-01", 90, 15, "Power Sector");
        w.medic = new MedicalOfficer("Dr. Nour", "M-01", 92, 12, "Habitat Sector");
        w.rover = new ExplorationRover("R-01", "Explorer-X1", "Storage Sector", 100, false);
        w.repairBot = new RepairRobot("RR-01", "FixBot-2", "Storage Sector", 90, false);
        w.drone = new Drone("D-01", "Scout-V", "Comm Sector", 80, false);

        w.colony.addAgent(w.scientist);
        w.colony.addAgent(w.engineer);
        w.colony.addAgent(w.medic);
        w.colony.addAgent(w.rover);
        w.colony.addAgent(w.repairBot);
        w.colony.addAgent(w.drone);
        return w;
    }

    // ------------------------------------------------------------------
    // Scenario helpers
    // ------------------------------------------------------------------
    private static Map<ResourceType, Integer> cost(Object... pairs) {
        Map<ResourceType, Integer> map = new EnumMap<>(ResourceType.class);
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            map.put((ResourceType) pairs[i], (Integer) pairs[i + 1]);
        }
        return map;
    }

    private static ExplorationMission exploration(String id, int duration) {
        ExplorationMission m = new ExplorationMission(id, duration, "Valles Marineris");
        m.addRequiredResource(ResourceType.FUEL, 10);
        m.addRequiredResource(ResourceType.BATTERY_POWER, 15);
        return m;
    }

    private static String activeMission(MarsColony colony) {
        Mission m = colony.getActiveMission();
        return m == null ? "None" : m.getMissionId() + " (" + m.getState() + ")";
    }

    private static boolean submit(World w, Mission mission, Agent... agents) {
        boolean accepted = w.colony.submitMission(mission, Arrays.asList(agents));
        List<String> names = new ArrayList<>();
        for (Agent a : agents) {
            names.add(a.getName());
        }
        box("SUBMIT " + mission.getMissionId(),
                "Agents", String.join(", ", names),
                "Result", accepted ? "ACCEPTED" : "REJECTED",
                "Mission state", String.valueOf(mission.getState()),
                "Failure reason", String.valueOf(mission.getFailureReason()),
                "Colony active mission", activeMission(w.colony));
        return accepted;
    }

    private static void tick(World w, Mission mission, int units) {
        System.out.println(line('-'));
        row(String.format("%-6s | %-10s | %-12s | %-8s | %s", "Time", "Mission", "State", "Progress", "Reason"));
        System.out.println(line('-'));
        for (int i = 0; i < units; i++) {
            w.sim.advanceTime(1);
            row(String.format("t=%-4d | %-10s | %-12s | %-8s | %s",
                    w.sim.getCurrentTime(),
                    mission.getMissionId(),
                    mission.getState(),
                    mission.getElapsedTime() + "/" + mission.getDuration(),
                    mission.getFailureReason() == null ? "-" : mission.getFailureReason()));
        }
        System.out.println(line('-'));
    }

    private static void emergency(World w, Emergency e) {
        w.sim.triggerEmergency(e);
        box("EMERGENCY " + e.getEmergencyId(),
                "State", String.valueOf(e.getState()),
                "Reason", String.valueOf(e.getUnresolvedReason()),
                "Details", e.toString());
    }

    private static void resources(World w, String label, ResourceType... types) {
        System.out.println(line('-'));
        row(label);
        System.out.println(line('-'));
        for (ResourceType t : types) {
            kv(t.name(), w.colony.getTotalResource(t));
        }
        System.out.println(line('-'));
    }

    private static void agents(World w, String label) {
        System.out.println(line('-'));
        row(label);
        System.out.println(line('-'));
        for (Agent a : w.colony.getAgents()) {
            row(String.format("%-14s | %-18s | available: %s",
                    a.getName(), a.getLocation(), a.isAvailable() ? "YES" : "NO"));
        }
        System.out.println(line('-'));
    }

    // ------------------------------------------------------------------
    // Scenarios
    // ------------------------------------------------------------------
    private static void scenario1() {
        banner("SCENARIO 1: Successful Exploration Mission");
        World w = buildWorld();

        section("Create scientist, rover, equipment and mission");
        Equipment sampler = new Equipment("Soil Sampler", Capability.SCIENTIFIC_RESEARCH);
        w.rover.getCargo().add(sampler);
        ExplorationMission m = exploration("EXP-1", 3);
        box("CREATED OBJECTS",
                "Scientist", w.scientist.getName(),
                "Exploration rover", w.rover.getName(),
                "Equipment (rover cargo)", sampler.getName(),
                "Mission", m.getMissionId() + " -> " + m.getTargetRegion() + " (duration " + m.getDuration() + ")");

        section("Before");
        reportBox("COLONY STATUS (INITIAL)", w.colony.generateStatusReport());
        resources(w, "RESOURCES BEFORE", ResourceType.FUEL, ResourceType.BATTERY_POWER);

        section("Submit and run");
        if (submit(w, m, w.scientist, w.rover)) {
            agents(w, "AGENTS DURING MISSION");
            tick(w, m, 3);
        }

        section("After");
        reportBox("COLONY STATUS (FINAL)", w.colony.generateStatusReport());
        resources(w, "RESOURCES AFTER (mission cost consumed)", ResourceType.FUEL, ResourceType.BATTERY_POWER);
        agents(w, "AGENTS RELEASED");
        box("VERIFICATION",
                "Mission state", String.valueOf(m.getState()),
                "Progress", m.getElapsedTime() + "/" + m.getDuration(),
                "Colony active mission", activeMission(w.colony),
                "Equipment damaged", String.valueOf(sampler.isDamaged()));
    }

    private static void scenario2() {
        banner("SCENARIO 2: Mission Rejection");

        section("2a. Unsuitable agent (engineer cannot explore)");
        World w = buildWorld();
        ExplorationMission bad = exploration("EXP-BAD", 3);
        resources(w, "BEFORE", ResourceType.FUEL, ResourceType.BATTERY_POWER);
        submit(w, bad, w.engineer);
        System.out.println(line('-'));
        row("Mission cannot be assigned. Reason: " + bad.getFailureReason());
        System.out.println(line('-'));
        box("VERIFICATION (never became active)",
                "Mission state", String.valueOf(bad.getState()),
                "Elapsed time", String.valueOf(bad.getElapsedTime()),
                "Colony active mission", activeMission(w.colony));
        resources(w, "AFTER (nothing consumed)", ResourceType.FUEL, ResourceType.BATTERY_POWER);

        section("2b. Another mission already active");
        World w2 = buildWorld();
        submit(w2, exploration("EXP-A", 3), w2.scientist, w2.rover);
        submit(w2, exploration("EXP-B", 3), w2.drone);
        box("VERIFICATION", "Active mission still", activeMission(w2.colony));
    }

    private static void scenario3() {
        banner("SCENARIO 3: Resource Shortage");

        section("3a. Repair mission needs more spare parts than the colony has");
        World w = buildWorld();
        resources(w, "BEFORE", ResourceType.SPARE_PARTS);
        RepairMission repair = new RepairMission(99, "REP-1", 2);
        repair.setTargetFacility(w.lab);
        submit(w, repair, w.engineer, w.repairBot);
        System.out.println(line('-'));
        row("Mission cannot be executed. Reason: " + repair.getFailureReason());
        System.out.println(line('-'));
        resources(w, "AFTER (nothing consumed, never negative)", ResourceType.SPARE_PARTS);

        section("3b. All-or-nothing consumption across facilities");
        resources(w, "OXYGEN BEFORE", ResourceType.OXYGEN);
        boolean ok600 = w.colony.consumeResourcesAtomically(cost(ResourceType.OXYGEN, 600));
        box("REQUEST 600 OXYGEN", "Outcome", ok600 ? "ACCEPTED" : "REJECTED (nothing consumed)");
        resources(w, "OXYGEN AFTER 600 REQUEST", ResourceType.OXYGEN);
        boolean ok450 = w.colony.consumeResourcesAtomically(cost(ResourceType.OXYGEN, 450));
        box("REQUEST 450 OXYGEN", "Outcome", ok450 ? "ACCEPTED (several facilities)" : "REJECTED");
        resources(w, "OXYGEN AFTER 450 REQUEST", ResourceType.OXYGEN);
    }

    private static void scenario4() {
        banner("SCENARIO 4: Oxygen Leak Emergency");

        section("4a. Responders available -> RESOLVED (single oxygen loss)");
        World w = buildWorld();
        resources(w, "BEFORE", ResourceType.OXYGEN, ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        OxygenLeakEmergency leak = new OxygenLeakEmergency("EM-OL1", w.habitat, 50,
                cost(ResourceType.SPARE_PARTS, 2, ResourceType.BATTERY_POWER, 5));

        box("STEP 1 - DETECT THE EMERGENCY",
                "Emergency", leak.getEmergencyId(),
                "Type", leak.getClass().getSimpleName(),
                "State", String.valueOf(leak.getState()),
                "Oxygen loss amount", String.valueOf(leak.getOxygenLossAmount()));

        box("STEP 2 - LOCATE THE AFFECTED FACILITY",
                "Facility", leak.getAffectedHabitat().getName(),
                "Location", leak.getLocation(),
                "Oxygen level", String.valueOf(w.habitat.getOxygenLevel()),
                "Operational", String.valueOf(w.habitat.isOperational()));

        System.out.println(line('-'));
        row("STEP 3 - IDENTIFY SUITABLE RESPONDERS");
        System.out.println(line('-'));
        for (Agent a : w.colony.getAgents()) {
            if (a.isAvailable() && a.canRespond(leak)) {
                row(a.getName() + " (" + a.getClass().getSimpleName() + ")");
            }
        }
        System.out.println(line('-'));

        emergency(w, leak);
        box("STEP 4 - PERFORM THE RESPONSE", "Outcome", String.valueOf(leak.getState()));
        resources(w, "STEP 5 - RESOURCES CONSUMED (after)",
                ResourceType.OXYGEN, ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        box("STEP 6 - FACILITY STATE UPDATED",
                "Habitat operational", String.valueOf(w.habitat.isOperational()),
                "Habitat oxygen", String.valueOf(w.habitat.getOxygenLevel()));
        agents(w, "RESPONDERS RELEASED");

        section("4b. No responders -> UNRESOLVED, double oxygen loss");
        World w2 = buildWorld();
        w2.engineer.setAvailable(false);
        w2.repairBot.setAvailable(false);
        resources(w2, "BEFORE", ResourceType.OXYGEN);
        emergency(w2, new OxygenLeakEmergency("EM-OL2", w2.habitat, 50,
                cost(ResourceType.SPARE_PARTS, 2, ResourceType.BATTERY_POWER, 5)));
        box("PENALTY", "Habitat A oxygen", w2.habitat.getOxygenLevel() + " (lost 100 in total)",
                "Habitat operational", String.valueOf(w2.habitat.isOperational()));

        section("4c. Responders exist but spare parts insufficient");
        World w3 = buildWorld();
        emergency(w3, new OxygenLeakEmergency("EM-OL3", w3.habitat, 50,
                cost(ResourceType.SPARE_PARTS, 99)));

        section("4d. Oxygen never goes below zero");
        World w4 = buildWorld();
        emergency(w4, new OxygenLeakEmergency("EM-OL4", w4.habitat, 9999,
                cost(ResourceType.SPARE_PARTS, 1)));
        box("BOUNDARY CHECK", "Habitat A oxygen", String.valueOf(w4.habitat.getOxygenLevel()));
    }

    private static void scenario5() {
        banner("SCENARIO 5: Environmental Effect (Dust Storm)");

        section("5a. Dust storm blocks a new exploration mission");
        World w = buildWorld();
        w.sim.changeEnvironment(EnvironmentCondition.DUST_STORM);
        box("ENVIRONMENT", "Condition", String.valueOf(w.colony.getEnvironment()));
        ExplorationMission blocked = exploration("EXP-1", 3);
        submit(w, blocked, w.scientist, w.rover);
        System.out.println(line('-'));
        row("Exploration Mission cannot start. Reason: " + blocked.getFailureReason());
        System.out.println(line('-'));
        resources(w, "RESOURCES UNTOUCHED", ResourceType.FUEL, ResourceType.BATTERY_POWER);

        section("5b. Dust storm hits a mission already IN_PROGRESS");
        World w2 = buildWorld();
        ExplorationMission running = exploration("EXP-2", 5);
        submit(w2, running, w2.scientist, w2.rover);
        tick(w2, running, 1);
        w2.sim.changeEnvironment(EnvironmentCondition.DUST_STORM);
        box("DUST STORM HIT",
                "Mission state", String.valueOf(running.getState()),
                "Failure reason", String.valueOf(running.getFailureReason()),
                "Colony active mission", activeMission(w2.colony));
        agents(w2, "AGENTS RELEASED AFTER FAILURE");
    }

    private static void scenario6() {
        banner("SCENARIO 6: Rover Breakdown");
        World w = buildWorld();
        ExplorationMission m = exploration("EXP-1", 4);
        submit(w, m, w.scientist, w.rover);
        tick(w, m, 1);
        resources(w, "BEFORE", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);

        emergency(w, new RoverBreakdownEmergency("EM-RB1", w.rover, m,
                cost(ResourceType.SPARE_PARTS, 2, ResourceType.BATTERY_POWER, 5)));

        box("DIAGNOSTICS",
                "Mission state", String.valueOf(m.getState()),
                "Failure reason", String.valueOf(m.getFailureReason()),
                "Rover damaged", String.valueOf(w.rover.isDamaged()),
                "Colony active mission", activeMission(w.colony));
        resources(w, "AFTER REPAIR", ResourceType.SPARE_PARTS, ResourceType.BATTERY_POWER);
        agents(w, "AGENTS AFTER REPAIR");
    }

    // ------------------------------------------------------------------
    // Runner
    // ------------------------------------------------------------------
    private static void run(int number) {
        try {
            switch (number) {
                case 1: scenario1(); break;
                case 2: scenario2(); break;
                case 3: scenario3(); break;
                case 4: scenario4(); break;
                case 5: scenario5(); break;
                case 6: scenario6(); break;
                default: box("RUNNER WARNING", "Invalid scenario", number + " (valid: 1-6)");
            }
        } catch (RuntimeException ex) {
            box("SCENARIO CRASHED", "Scenario", String.valueOf(number), "Error", ex.toString());
            ex.printStackTrace(System.out);
        }
    }

    public static void main(String[] args) {
        System.out.println(line('='));
        row("MARS-OPS :: Mars Colony Management and Emergency Simulator");
        System.out.println(line('='));

        if (args.length == 0) {
            for (int i = 1; i <= 6; i++) {
                run(i);
            }
        } else {
            for (String arg : args) {
                try {
                    run(Integer.parseInt(arg.trim()));
                } catch (NumberFormatException e) {
                    box("INPUT ERROR", "Invalid argument", arg);
                }
            }
        }

        System.out.println();
        System.out.println(line('='));
        row("ALL REQUESTED SCENARIOS FINISHED");
        System.out.println(line('='));
    }
}