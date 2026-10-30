# MARS-OPS: Autonomous Mars Colony Management & Emergency Simulator

Computer and Communications Programming II, Fall 2026, Lab 5
Alexandria University, Faculty of Engineering, Computer and Communication Engineering

| Team member | ID | Main responsibility |
|---|---|---|
| `<fill in>` | `<fill in>` | Facilities, equipment, resources (package `facilities`) |
| `<fill in>` | `<fill in>` | Personnel and robots (package `agents`) |
| `<fill in>` | `<fill in>` | Colony, missions, simulation (packages `core`, `missions`) |
| `<fill in>` | `<fill in>` | Emergencies, demonstration `Main` (package `emergencies`, `core.Main`) |

---

## Table of contents

1. [System overview](#1-system-overview)
2. [Project structure](#2-project-structure)
3. [Main components and responsibilities](#3-main-components-and-responsibilities)
4. [Object relationships (UML) and OOP concepts](#4-object-relationships-uml-and-oop-concepts)
5. [Design decisions and assumptions](#5-design-decisions-and-assumptions)
6. [Resource rules and allocation policy](#6-resource-rules-and-allocation-policy)
7. [Missions and mission states](#7-missions-and-mission-states)
8. [Emergency rules](#8-emergency-rules)
9. [Environmental effects](#9-environmental-effects)
10. [Simulation and time](#10-simulation-and-time)
11. [Compiling and running](#11-compiling-and-running)
12. [Demonstration scenarios](#12-demonstration-scenarios)
13. [UML diagrams](#13-uml-diagrams)
14. [Known limitations](#14-known-limitations)

---

## 1. System overview

MARS-OPS is a Java simulation of a small research colony on Mars. It manages:

- **Personnel**: scientists, engineers and medical officers.
- **Robotic units**: exploration rovers, repair robots and drones.
- **Facilities**: habitats, laboratories, storage facilities, power stations and communication stations, each holding resources and equipment.
- **Resources**: oxygen, water, food, fuel, battery power, medical supplies and spare parts.
- **Missions**: exploration, repair, research and rescue, with a full lifecycle.
- **Environment**: normal conditions, dust storms and communication disruption.
- **Emergencies**: oxygen leak, fire, rover breakdown and medical emergency.
- **Simulation time**: the colony advances in discrete time units.
- **Status report**: a colony-wide report with resource totals, facilities, agents and emergencies.

The central idea is that objects **collaborate**. The colony does not check what type an agent is. It asks the agent (`agent.canPerform(mission)`, `agent.canRespond(emergency)`), and each mission and emergency knows how to validate and execute itself.

---

## 2. Project structure

```
marsops/
  core/
    Agent.java                 interface implemented by every person and robot
    Capability.java            enum of capabilities
    EmergencyState.java        enum: TRIGGERED, RESOLVED, UNRESOLVED
    EnvironmentCondition.java  enum: NORMAL, DUST_STORM, COMMUNICATION_DISRUPTION
    MissionState.java          enum: ASSIGNED, IN_PROGRESS, COMPLETED, FAILED
    ResourceType.java          enum of the seven resources
    MarsColony.java            central manager (agents, facilities, missions, resources)
    Simulation.java            time management and environment changes
    Main.java                  demonstration / testing driver
  agents/
    CrewMember.java            abstract base for humans
    Scientist.java  Engineer.java  MedicalOfficer.java
    RoboticUnit.java           abstract base for robots
    ExplorationRover.java  RepairRobot.java  Drone.java
  facilities/
    Facility.java              abstract base for facilities
    Habitat.java  Laboratory.java  StorageFacility.java
    PowerStation.java  CommunicationStation.java
    LifeSupportSystem.java     owned by each Habitat
    Equipment.java
  missions/
    Mission.java               abstract base, lifecycle and state control
    ExplorationMission.java  RepairMission.java
    ResearchMission.java  RescueMission.java
  emergencies/
    Emergency.java             abstract base
    OxygenLeakEmergency.java  FireEmergency.java
    RoverBreakdownEmergency.java  MedicalEmergency.java
```

---

## 3. Main components and responsibilities

### Core

| Class | Responsibility |
|---|---|
| `MarsColony` | Owns the lists of agents, facilities, emergencies and mission history. Holds the single active mission and the current environment. Computes colony-wide resource totals, consumes resources atomically, accepts or rejects mission submissions, handles emergencies and produces the status report. |
| `Simulation` | Owns the clock. `advanceTime(n)` advances the colony `n` units, one tick at a time. `changeEnvironment(...)` changes the environment and applies it to the active mission. `triggerEmergency(...)` hands an emergency to the colony. |
| `Agent` | Common contract for people and robots: id, name, location, availability, `hasCapability`, `canPerform(Mission)`, `canRespond(Emergency)`. |

### Agents

| Class | Capability | Can perform missions | Can respond to emergencies |
|---|---|---|---|
| `Scientist` | `SCIENTIFIC_RESEARCH` | Research, Exploration | none |
| `Engineer` | `ENGINEERING_REPAIR` | Repair | Oxygen leak, Fire, Rover breakdown |
| `MedicalOfficer` | `MEDICAL_CARE` | Rescue | Medical emergency, Fire |
| `ExplorationRover` | `SURFACE_EXPLORATION`, `HEAVY_TRANSPORT` | Exploration, Rescue | none |
| `RepairRobot` | `ENGINEERING_REPAIR` | Repair | Oxygen leak, Fire, Rover breakdown |
| `Drone` | `AERIAL_RECON` | Exploration | none |

Humans have health (1 to 100), fatigue (0 to 100), location, skills and availability. Robots have battery level (0 to 100), damaged flag, location, capabilities and availability.

An agent is **available** when:
- Human: its availability flag is set and its health is above 0.
- Robot: its availability flag is set, it is not damaged, and its battery is above 0.

### Facilities

| Class | Notes |
|---|---|
| `Facility` (abstract) | Name, location, capacity, operational status, a map of stored resources, and a list of equipment. |
| `Habitat` | Stores oxygen and other resources. Owns a `LifeSupportSystem`. `reduceOxygen(amount)` never goes below zero. |
| `Laboratory` | Adds an `onFire` flag and `damageAllEquipment()`. |
| `StorageFacility` | Adds a maximum storage volume. |
| `PowerStation` | Adds a power output (0 when not operational). |
| `CommunicationStation` | Adds a signal-active flag. |
| `Equipment` | Name, provided capability, damaged flag. Usable when not damaged. |

### Missions and emergencies

See [section 7](#7-missions-and-mission-states) and [section 8](#8-emergency-rules).

---

## 4. Object relationships (UML) and OOP concepts

### Relationship types used, and why

| Relationship | Where it is used | Why this type |
|---|---|---|
| **Inheritance** | `CrewMember` <- `Scientist`, `Engineer`, `MedicalOfficer`. `RoboticUnit` <- `ExplorationRover`, `RepairRobot`, `Drone`. `Facility` <- `Habitat`, `Laboratory`, `StorageFacility`, `PowerStation`, `CommunicationStation`. `Mission` <- four mission types. `Emergency` <- four emergency types. | A true "is-a" relationship. Subclasses share state and behaviour and override what differs. |
| **Interface realization** | `CrewMember` and `RoboticUnit` implement `Agent`. | Humans and robots are different hierarchies, but the colony treats them uniformly as agents. |
| **Composition** | `Habitat` ◆ `LifeSupportSystem` | The system is created in the habitat's constructor, stored in a `final` field and has no meaning outside its habitat. It lives and dies with it. |
| **Aggregation** | `MarsColony` ◇ `Agent`, `MarsColony` ◇ `Facility`, `Facility` ◇ `Equipment`, `ExplorationRover` ◇ `Equipment` (cargo) | The parts are created outside the whole and passed in (`addAgent`, `addFacility`, `addEquipment`). They can exist, be reassigned or be moved independently. |
| **Association** | `Mission` – `Agent` (assigned agents), `Mission` – `Facility` (target), `Mission` – `Equipment` (repair target), `RescueMission` – `Agent` (stranded entity), `Emergency` – `Agent` (responders), `Emergency` – `Habitat` / `Laboratory` / `ExplorationRover` / `CrewMember`, `MarsColony` – `Mission`, `Simulation` – `MarsColony` | Objects that use or refer to one another without owning each other. |

### The four OOP pillars

- **Encapsulation**: all state is `private` or `protected` and is changed through methods that validate input (for example, `Facility.deductResource` clamps at zero, constructors reject invalid values). Getters for collections return copies, so outside code cannot modify the colony's internal lists.
- **Abstraction**: `Agent`, `CrewMember`, `RoboticUnit`, `Facility`, `Mission` and `Emergency` define contracts. The colony works against these abstractions.
- **Inheritance**: see the table above.
- **Polymorphism**: `canPerform`, `canRespond`, `validateRequirements`, `start`, `advanceOneTimeUnit`, `complete`, `onEnvironmentChange`, `handle` and `performDuty` are overridden per subclass and called through base types.

### Capability-based design (spec section 6.1)

A mission never checks the concrete class of an agent to decide whether it can take part. The colony does:

```java
if (agent.canPerform(mission)) { ... }
```

Each agent answers for itself. Emergencies do the same with `agent.canRespond(emergency)`. Adding a new kind of agent means writing one class and answering these two questions. Nothing in the colony has to change.

---

## 5. Design decisions and assumptions

1. **Missions belong to the colony, not to facilities.** Facilities are only a *target* or *location* of a mission (the facility to repair, the laboratory for research). Exploration missions have no facility.
2. **Resources are stored in facilities and managed by the colony.** Quantities live in each facility's resource map. Totals, availability checks and consumption are colony operations.
3. **One active mission at a time.** Emergencies are not subject to this limit, but responders must be available (not part of the active mission).
4. **A rejected mission is recorded but never active.** Every submission, accepted or not, is stored in the mission history. A rejected mission becomes `FAILED` with a reason. Failed missions are never retried; a new `Mission` object must be created.
5. **All checks happen at submission, in a fixed order** (see [section 7](#7-missions-and-mission-states)). Mission resources are consumed once, at acceptance, and are **not refunded** if the mission later fails.
6. **Locations are plain strings.** A location is either a facility's location string (for example `"Habitat Sector A"`) or the constant string `"Martian Surface"`.
7. **Facility capacity** is stored and validated (must be positive) but is not enforced against stored resources or personnel.
8. **Response resources are kept outside the facility under emergency.** A non-operational facility does not count towards colony totals (spec section 5.1). A leaking habitat or burning laboratory is switched off during the response, so spare parts, battery power and medical supplies must be stored in other facilities. The demonstration puts them in the Storage Facility.
9. **Emergencies are triggered manually** (`simulation.triggerEmergency(...)`), as required by spec section 9.2. The environment is also changed manually (`simulation.changeEnvironment(...)`).
10. **Human vs. robot responder for an oxygen leak.** Whether a responder is *appropriate* is decided by `canRespond`. The oxygen-leak rule "at least one human and one robot" splits candidates by their abstract category (`CrewMember` / `RoboticUnit`).
11. **Java version**: JDK 11 or newer (the code uses `String.isBlank()`). Tested with JDK 21.

---

## 6. Resource rules and allocation policy

Resource types: `OXYGEN`, `WATER`, `FOOD`, `FUEL`, `BATTERY_POWER`, `MEDICAL_SUPPLIES`, `SPARE_PARTS`.

| Rule | Implementation |
|---|---|
| **Colony-wide totals** | `MarsColony.getTotalResource(type)` sums the amount held by every **operational** facility. Resources in a non-operational facility are not counted. |
| **Multiple sources** | `consumeResource` draws from several facilities in turn until the requested quantity is met. |
| **All-or-nothing** | `consumeResourcesAtomically(map)` first checks every requested resource against the colony total. If any is short, it returns `false` and consumes nothing. Only then does it consume. |
| **Never negative** | `Facility.deductResource` clamps at zero, `addResource` accepts only positive amounts, and the colony refuses to consume more than the total. |
| **Local emergencies** | An oxygen leak reduces oxygen only in the affected habitat, which lowers the colony total. |
| **Status report** | `generateStatusReport()` prints colony-wide totals for every resource type. |

### Allocation policy (which facility supplies first)

**Resources are drawn from facilities in the order in which they were added to the colony (`addFacility`), draining each facility before moving to the next.** Non-operational facilities are skipped.

Example from the demonstration colony (facilities added in the order Habitat A, Storage, Lab-1, Comm Tower, Solar Farm):

| Facility | Oxygen before | After consuming 450 |
|---|---|---|
| Habitat A | 300 | 0 |
| Storage Facility | 200 | 50 |
| Lab-1 | 60 | 60 |
| Colony total | 560 | 110 |

A request for 600 oxygen is rejected with nothing consumed, because only 560 are available.

---

## 7. Missions and mission states

### Mission types

| Type | Purpose | Requirements checked |
|---|---|---|
| `ExplorationMission` | Explore a region. | No dust storm. At least one agent with `SURFACE_EXPLORATION` (a rover) and one with `SCIENTIFIC_RESEARCH`. Every agent available and able to perform. Required resources available. |
| `RepairMission` | Repair a facility or a piece of equipment. | A target facility or equipment is set. At least one robotic unit **and** one crew member with `ENGINEERING_REPAIR`. Required spare parts available. |
| `ResearchMission` | A scientist performs an experiment. | Target is an **operational** `Laboratory` that is not on fire. Usable equipment providing `SCIENTIFIC_RESEARCH`. A scientist assigned. Required resources available. |
| `RescueMission` | Locate and recover a stranded agent. | No communication disruption. An operational communication station. A vehicle (`SURFACE_EXPLORATION` or `HEAVY_TRANSPORT`) and at least one crew member. The stranded agent is not among the rescuers. No dust storm if a surface vehicle is used. |

Each mission also has a duration (in time units), a target facility (optional), a map of required resources, a list of assigned agents, an elapsed time and a failure reason.

Side effects when a mission **starts**: exploration moves the rover to `"Martian Surface"`; repair moves the repairing agents to the target facility; research moves the scientist to the laboratory; rescue moves the rescuers to the stranded agent's location. When a repair mission **completes**, its target facility becomes operational and its equipment is repaired. When a rescue **completes**, the stranded agent and rescuers are moved to the rescue base (the first rescuer's location when the mission started).

### Submission pipeline (`MarsColony.submitMission`)

The mission is first added to the history, then these checks run in order. The first failure rejects the mission immediately with a reason.

1. Another mission is already active: `"Another mission is already active"`.
2. No agents supplied: `"No agents assigned to the mission"`.
3. An agent is null or unavailable: `"Agent unavailable"`.
4. An agent cannot perform this mission (`canPerform`): `"Agent does not possess the required capability"`.
5. Mission-specific validation (`validateRequirements`): environment, equipment, vehicle, personnel, resources. The mission sets its own failure reason.
6. Atomic resource consumption (all-or-nothing).
7. Agents are reserved (marked unavailable), the mission becomes the active mission and its state is `ASSIGNED`.

### State machine

```
        (submit accepted)
 [*] -----------------------> ASSIGNED
                                  |
                                  | first time unit (Simulation tick)
                                  v
                             IN_PROGRESS ---- elapsed >= duration ----> COMPLETED ---> [*]
                                  |
                                  | failure condition
                                  v
 (submit rejected) ---------->  FAILED -------------------------------------------> [*]
```

| From | To | Trigger / guard |
|---|---|---|
| (initial) | `ASSIGNED` | Submission accepted. |
| (initial) | `FAILED` | Submission rejected (reason recorded). The mission never becomes active. |
| `ASSIGNED` | `IN_PROGRESS` | The next time unit. `Mission.start()` throws if called from any other state. |
| `IN_PROGRESS` | `COMPLETED` | `elapsedTime >= duration`. |
| `IN_PROGRESS` | `FAILED` | Dust storm (exploration, surface rescue), communication disruption (rescue), laboratory non-operational or no usable equipment (research), or a rover breakdown emergency. |
| `COMPLETED`, `FAILED` | (final) | Terminal. `fail()` is ignored once a mission is terminal. The colony clears the active mission. |

Agents are released (made available again) when a mission completes, or when a reserved mission fails.

Duration counting: a mission starts counting when it becomes `IN_PROGRESS`. The first tick both starts the mission and advances it by one unit, so a mission with duration 3 completes on the third tick after acceptance.

---

## 8. Emergency rules

Common behaviour (class `Emergency`):

- States: `TRIGGERED` → `RESOLVED` or `UNRESOLVED`.
- Emergencies do **not** interrupt the active mission.
- A responder must be **available** (not in the active mission, not damaged, health/battery above 0) and must report `canRespond(emergency) == true`.
- Dispatching marks responders unavailable and **moves them to the emergency's location**. Location does not restrict who can respond. Responders are released when the response ends.
- Response resources are consumed atomically, and only after responders and resources have both been confirmed.
- If required responders or resources are missing, the emergency is `UNRESOLVED`, a clear reason is recorded, **no response resources are consumed**, and it is **not retried automatically**.

### Requirements per emergency type

| Emergency | Location | Required responders | Required resources (set by the scenario) | Effect when resolved | Effect when unresolved |
|---|---|---|---|---|---|
| **Oxygen leak** | Affected habitat | At least one human and one robot that can handle it (for example an Engineer and a Repair Robot). | For example 2 spare parts and 5 battery power. | Habitat is operational again. | Habitat stays non-operational. |
| **Fire** | Affected laboratory | One fire responder (`canRespond` and `ENGINEERING_REPAIR`). If crew are inside the lab, also one medical responder (`canRespond` and `MEDICAL_CARE`). | For example 1 spare part and 2 medical supplies. | Fire is put out, lab is operational again, treated crew recover. Lab equipment stays damaged and needs a `RepairMission`. | No consequences are applied; state and reason are recorded. |
| **Rover breakdown** | `"Martian Surface"` | One available agent (other than the rover) that can handle it. In practice an Engineer or a Repair Robot. | For example 2 spare parts and 5 battery power. | Rover is repaired and available again. | Rover stays damaged where it broke down. |
| **Medical emergency** | The medical facility | One available agent that can handle it (a Medical Officer). The facility must be operational. | For example 2 medical supplies and 5 oxygen. | Patient moves to the facility and its health increases by the configured amount. | No change to the patient. |

Immediate consequences (applied before the response is attempted):

- **Oxygen leak**: the specified oxygen amount is deducted from the habitat and the habitat becomes non-operational.
- **Rover breakdown**: the rover becomes damaged, unavailable and stays on the surface. The affected mission is failed with a reason and cleared as the active mission.

### Oxygen loss rule (spec section 9.2)

| Outcome | Oxygen lost from the affected habitat |
|---|---|
| Leak repaired | `X` (one deduction) |
| Leak unresolved (no responders, or not enough response resources) | `2X` (same amount deducted again) |

The loss is a one-time deduction, not continuous. A habitat's oxygen never falls below zero.

### Fire consequences (spec section 9.3)

Applied when the fire is handled with the required personnel and resources present:

| Consequence | Representation |
|---|---|
| Laboratory becomes unavailable | `operational = false` and `onFire = true` during the response. |
| Equipment becomes damaged | `Laboratory.damageAllEquipment()` marks all equipment damaged. |
| Oxygen consumption increases | A one-time oxygen deduction from the laboratory. |
| Fire-response personnel required | One responder with `ENGINEERING_REPAIR` that can respond to fire. |
| Medical assistance for personnel present | Crew located in the lab take damage, then a medical responder heals them. |

---

## 9. Environmental effects

The environment is one of `NORMAL`, `DUST_STORM`, `COMMUNICATION_DISRUPTION`. It is changed with `simulation.changeEnvironment(condition)`.

| Operation | Dust storm | Communication disruption |
|---|---|---|
| Start an **exploration** mission | Rejected: *"Surface operations are not allowed during a dust storm."* | Allowed |
| Start a **rescue** mission | Rejected if a surface vehicle is used | Rejected: *"rescue operations are not allowed during communication disturbance"* |
| Start a **repair** or **research** mission | Allowed | Allowed |
| Exploration mission **in progress** | Fails: *"Dust storm: active rover missions fail."* | Continues |
| Rescue mission **in progress** | Fails if it uses a surface vehicle | Fails: *"communication disruption: communication for rescue failed"* |
| Repair or research mission **in progress** | Continues | Continues |
| `hasActiveCommunicationStation()` | Unchanged | Returns `false` |

### What happens to missions already in progress when the environment changes

When `changeEnvironment` is called, the active mission (if it is `IN_PROGRESS`) is told about the new condition through `onEnvironmentChange(condition)`:

- If the new condition forbids the mission, it **fails immediately**, without consuming a time unit, with a clear reason.
- Its agents are released and become available again. A rover that was on the surface **stays where it is**.
- The colony clears the active mission, so a new mission can be submitted.
- Resources that were consumed when the mission was accepted are **not refunded**.
- If the condition does not affect the mission (for example a communication disruption during exploration), the mission simply continues.
- Each mission also re-checks the environment on every tick, so a condition set directly on the colony is caught at the next time unit.
- Returning to `NORMAL` does not revive failed missions. A new mission must be created.

---

## 10. Simulation and time

`Simulation.advanceTime(units)` runs `units` ticks. Each tick:

1. The simulation clock increases by one.
2. If the active mission is `ASSIGNED`, it is started.
3. If it is `IN_PROGRESS`, it advances by one unit and may complete or fail.
4. If it ended (`COMPLETED` or `FAILED`), it is cleared as the active mission and its agents are released.

Time units must be positive.

---

## 11. Compiling and running

Requirements: JDK 11 or newer. Run all commands from the project root, the folder that contains the `marsops` folder.

**Linux / macOS / Git Bash**

```bash
mkdir -p out
javac -d out $(find marsops -name "*.java")
java -cp out marsops.core.Main
```

**Windows (Command Prompt)**

```bat
mkdir out
dir /s /b marsops\*.java > sources.txt
javac -d out @sources.txt
java -cp out marsops.core.Main
```

The main class is `marsops.core.Main`.

---

## 12. Demonstration scenarios

`Main` is the testing class. Every scenario creates its own fresh colony, so scenarios never affect one another.

```bash
java -cp out marsops.core.Main          # all scenarios
java -cp out marsops.core.Main 1        # only scenario 1
java -cp out marsops.core.Main 2 3 5    # scenarios 2, 3 and 5
```

### Required scenarios

| # | Scenario | What it shows | Key expected output |
|---|---|---|---|
| 1 | Successful exploration | Scientist + rover + equipment + exploration mission (duration 3). Full status report before and after. | `ASSIGNED` → `IN_PROGRESS (1/3)` → … → `COMPLETED (3/3)`. Fuel 100 → 90, battery power 120 → 105. Rover location becomes `Martian Surface`. All agents available again. |
| 2 | Mission rejection | (a) An Engineer is submitted for an exploration mission. (b) A second mission is submitted while one is active. | `Reason: Agent does not possess the required capability` and `Reason: Another mission is already active`. Mission state `FAILED`, never active. The first mission stays active. |
| 3 | Resource shortage | (a) A repair mission needs 99 spare parts, the colony has 10. (b) 600 oxygen requested, then 450. | Reason `Insufficient SPARE_PARTS: required 99, available 10.` Totals unchanged. 600 oxygen rejected with nothing consumed. 450 accepted: Habitat A → 0, Storage → 50, total 560 → 110. |
| 4 | Emergency handling (oxygen leak) | (a) Leak during an active mission, resolved. (b) No responders, unresolved. (c) Responders but not enough spare parts. (d) Oxygen never below zero. | (a) Habitat 300 → 250, spare parts 10 → 8, battery power 105 → 100, mission is not interrupted. (b) Habitat 300 → 200 (loss applied twice), nothing consumed, state `UNRESOLVED` with reason. (d) Oxygen `0`. |
| 5 | Environmental effect (dust storm) | (a) Exploration submitted during a storm. (b) Storm starts while an exploration is in progress. | (a) `Reason: Surface operations are not allowed during a dust storm.`, fuel and battery untouched. (b) Mission `FAILED`, reason `Dust storm: active rover missions fail.`, agents released. |

### Extra scenarios

| # | Scenario | What it shows |
|---|---|---|
| 6 | Rover breakdown | Rover damaged on the surface, mission failed and cleared, a repair responder is dispatched to the surface, rover repaired. |
| 7 | Laboratory fire | A scientist inside the lab is injured, treated by a medical officer, the lab's equipment ends up damaged, lab operational again. |
| 8 | Medical emergency | (a) Patient health rises by 30 and resources are consumed. (b) Medical officer busy: `UNRESOLVED`, nothing consumed. |
| 9 | Communication disruption | (a) Rescue rejected during a disruption. (b) After communication returns, a rover and a medical officer complete a rescue and the stranded agent is brought to the rescue base. |

---

## 13. UML diagrams

All diagrams are created with `<tool, for example draw.io>`. Editable sources and exported images are in the `uml/` folder.

| Diagram | File | What it covers |
|---|---|---|
| Class diagram | `uml/class-diagram.<ext>` | All classes, interfaces, abstract classes and enums with attributes, methods, visibility and multiplicities. Shows inheritance, association, aggregation and composition. |
| Object diagram | `uml/object-diagram.<ext>` | A snapshot of the colony just before an exploration mission: scientist, rover, facilities with resource quantities, and the mission in `ASSIGNED`. |
| State machine diagram | `uml/state-machine.<ext>` | Mission lifecycle: `ASSIGNED`, `IN_PROGRESS`, `COMPLETED`, `FAILED`, with guards and failure causes ([section 7](#7-missions-and-mission-states)). |
| Sequence diagrams | `uml/sequence-mission.<ext>`, `uml/sequence-oxygen-leak.<ext>`, `uml/sequence-dust-storm.<ext>` | Mission submission and execution (with rejection flow), oxygen leak emergency (with unavailable responders and insufficient resources flows), environmental restriction. |
| Use case diagram | `uml/use-case.<ext>` | Colony Operator and Emergency Operator, with the use cases listed in spec section 11.6. |

---

## 14. Known limitations

These are deliberate simplifications. They do not affect the required scenarios.

- **Simulated time only progresses missions.** Battery decay, fatigue growth and random emergencies or environment changes are not applied automatically on each tick. Emergencies and environment changes are triggered explicitly.
- **Robot battery level is not consumed by missions.** Missions consume the colony resource `BATTERY_POWER`. A robot's own `batteryLevel` only matters for availability (it must be above 0) and can be changed with `consumeBattery` and `recharge`.
- **Communication disruption** affects rescue missions and the communication-station check. Drone control and other communication-dependent operations are not restricted.
- **Facility capacity** is validated but not enforced.
- **An unresolved fire** records its state and reason without applying its consequences.
- **Resources of a mission are not refunded** if the mission fails after it was accepted.
