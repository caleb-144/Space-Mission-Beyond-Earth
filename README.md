# Space Mission: Beyond Earth

A beginner-friendly Java Swing astronaut-training game with four educational missions and local SQLite save data.

## Requirements

- Java 17 or newer
- Maven 3.8 or newer

## Run

From the project directory (Maven downloads the runtime dependencies as needed):

```sh
mvn compile exec:java
```

To build the application JAR:

```sh
mvn package
```

The game database is created automatically at `~/.space-mission-beyond-earth/game.db` (or the equivalent user home directory on your operating system).

## Missions

1. **Zero-Gravity Training:** Use the arrow keys or WASD to collect three beacons and reach the airlock while avoiding debris.
2. **Space Knowledge Assessment:** Answer eight multiple-choice questions and review explanations.
3. **Spacewalk & Satellite Repair:** Complete three repairs before oxygen runs out.
4. **Emergency Survival Simulation:** Choose procedures for three spacecraft emergencies and review the safety guidance.

Complete both Level 1 missions to unlock Level 2. Player profiles, best mission scores, and completion status are saved locally. Replaying a mission keeps the higher score.

## Architecture

The project separates Swing views, game navigation/controller logic, mission and player models, SQLite DAOs, and game progression services under `src/main/java/edu/spacemission`.
