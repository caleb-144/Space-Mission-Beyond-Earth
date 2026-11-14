# Space Mission: Beyond Earth

A browser-based astronaut-training game with four educational missions. The game runs in modern browsers using HTML, CSS, and JavaScript; no installation or build step is required.

## Run

Open `index.html` in a modern web browser, or serve the project directory with any static web server. For example:

```sh
python3 -m http.server 8000
```

Then visit `http://localhost:8000`. Mission progress and the best score for each mission are saved in the browser's local storage.

## Missions

1. **Zero-Gravity Training:** Use the arrow keys or WASD to collect three beacons and reach the airlock while avoiding debris.
2. **Space Knowledge Assessment:** Answer eight multiple-choice questions and review explanations.
3. **Spacewalk & Satellite Repair:** Complete three repairs before oxygen runs out.
4. **Emergency Survival Simulation:** Choose procedures for three spacecraft emergencies and review the safety guidance.

Complete both Level 1 missions to unlock Level 2. Replaying a mission keeps the higher score.

## Browser controls

- Zero-Gravity Training: arrow keys, WASD, or the on-screen directional buttons.
- All other missions: use the on-screen choices and buttons.

The original Java Swing implementation remains under `src/main/java` and can still be run with `mvn compile exec:java`.


## Updated minigame lineup

The campaign now contains only two playable activities, in this order:

1. **Defying Gravity** — the astronaut asteroid-dodging arcade game, embedded from `minigames/defying-gravity/`.
2. **Space Knowledge Quiz** — the original eight-question quiz.

The previous grid movement, repair, and emergency activities are no longer shown in mission control. Defying Gravity sends its run score to the parent campaign when the player reaches game over; the campaign converts each asteroid passed into 10 mission points (maximum 100) and saves the best score with the player profile. The arcade sprites use a deep-space blue and teal palette to better fit the supplied space-pixel-art reference.

Run the project using the same local web server setup as before and open the project `index.html`. The embedded game uses relative paths, so avoid opening the HTML directly with a `file://` URL.
