# MARS-OPS

Empty class skeleton (class names and inheritance only, no code).
Each person fills in only their own classes.

## Ownership
| Person | Package / classes |
|---|---|
| 1 | `facilities/`: Facility, Habitat, LifeSupportSystem, Laboratory, StorageFacility, PowerStation, CommunicationStation, Equipment |
| 2 | `agents/`: CrewMember, Scientist, Engineer, MedicalOfficer, RoboticUnit, ExplorationRover, RepairRobot, Drone |
| 3 | `missions/`: Mission + 4 subtypes, and in `core/`: MarsColony, Simulation |
| 4 | `emergencies/`: Emergency + 4 subtypes, and in `core/`: Main |

Shared and frozen after day 1: the enums and the `Agent` interface in `core/`.

## Git workflow
    git checkout -b person1-facilities     # one branch per person
    git add src/marsops/facilities && git commit -m "Implement facilities"
    git push -u origin person1-facilities  # then open a pull request into main

Only edit the files you own. If you need a signature changed in someone else's class, ask them first.

## Build
    mkdir -p out && javac -d out $(find src -name "*.java")
