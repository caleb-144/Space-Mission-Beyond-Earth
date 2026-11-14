"use strict";

const STORAGE_KEY = "space-mission-beyond-earth-v1";
const missions = [
  { id: 1, name: "Zero-Gravity Training", detail: "Collect three beacons and reach the airlock." },
  { id: 2, name: "Space Knowledge Assessment", detail: "Test your knowledge of planets, gravity, and exploration." },
  { id: 3, name: "Spacewalk & Satellite Repair", detail: "Complete three repairs before oxygen runs low." },
  { id: 4, name: "Emergency Survival Simulation", detail: "Choose the safest procedure in each scenario." }
];
const questions = [
  ["Which planet is known as the Red Planet?", ["Venus", "Mars", "Jupiter", "Mercury"], 1, "Iron-rich dust gives Mars its reddish appearance."],
  ["What keeps the planets in orbit around the Sun?", ["Magnetism", "Air resistance", "Gravity", "Solar wind"], 2, "The Sun’s gravity continually bends each planet’s path into an orbit."],
  ["What does an astronaut experience in orbit?", ["Microgravity", "No mass", "No motion", "Stronger gravity than on Earth"], 0, "Astronauts and their spacecraft are in continuous free fall, creating a microgravity experience."],
  ["What is the International Space Station?", ["A telescope on Mars", "An orbiting research laboratory", "A rocket engine", "A lunar lander"], 1, "The ISS is a crewed laboratory orbiting Earth."],
  ["Which equipment helps an astronaut breathe during a spacewalk?", ["A parachute", "A spacesuit life-support system", "A solar panel", "A radio dish"], 1, "A spacesuit provides oxygen, pressure, and temperature control."],
  ["Which is the largest planet in our solar system?", ["Saturn", "Neptune", "Earth", "Jupiter"], 3, "Jupiter is the largest planet in our solar system."],
  ["Why do astronauts appear to float inside an orbiting spacecraft?", ["There is no gravity in space", "They and the spacecraft are falling together around Earth", "The cabin is full of helium", "The spacecraft is outside the solar system"], 1, "Gravity is still present in low Earth orbit; the crew and spacecraft are in free fall together."],
  ["What is a satellite?", ["An object that orbits another object in space", "A type of spacesuit", "A star that has gone out", "A rocket launch site"], 0, "Natural and human-made satellites orbit planets and other celestial bodies."]
];
const emergencies = [
  {
    title: "Oxygen system warning",
    prompt: "The cabin oxygen reading is falling. What should you do first?",
    choices: ["Ignore it; the sensor is probably wrong", "Notify the crew, check the backup supply, and follow the checklist", "Open the hatch to vent the cabin", "Turn off all communications"],
    correct: 1,
    explanation: "Alerting the crew and following the emergency checklist helps verify the issue and safely switch to backup life support."
  },
  {
    title: "Electrical power fault",
    prompt: "A critical power circuit has failed. Which action is safest?",
    choices: ["Isolate the affected circuit and use the approved power-down procedure", "Reconnect wires while the circuit is live", "Disable every warning alarm", "Leave the spacecraft immediately"],
    correct: 0,
    explanation: "Isolate the fault using the spacecraft checklist, then preserve power for essential life-support and communications."
  },
  {
    title: "Equipment malfunction",
    prompt: "A life-support fan stops working. What is the best response?",
    choices: ["Strike the unit until it starts", "Report the fault and use the documented backup procedure", "Remove the filter and discard it", "Wait until the next day to mention it"],
    correct: 1,
    explanation: "Report equipment problems promptly and use documented backup procedures; improvised repairs can make a hazard worse."
  }
];
const repairTasks = [
  "Inspect the damaged solar array",
  "Secure the loose power connector",
  "Deploy the replacement antenna"
];
const debris = new Set(["3,1", "3,2", "6,3"]);
const beacons = ["2,0", "5,2", "1,4"];
const airlock = "7,4";

const app = document.querySelector("#game");
const notice = document.querySelector("#notice");
let profile = readProfile();
let screen = "home";
let draftName = "";
let round = null;
let noticeTimer;

function readProfile() {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (!saved) return null;
    const parsed = JSON.parse(saved);
    if (!parsed || typeof parsed.name !== "string" || typeof parsed.character !== "string"
      || !parsed.bestScores || typeof parsed.bestScores !== "object" || Array.isArray(parsed.bestScores)
      || !Array.isArray(parsed.completed)
      || Object.values(parsed.bestScores).some(score => !Number.isInteger(score) || score < 0 || score > 100)
      || parsed.completed.some(id => ![1, 2, 3, 4].includes(id))) {
      throw new Error("The saved profile has an unexpected format.");
    }
    return parsed;
  } catch (error) {
    if (error instanceof SyntaxError || error instanceof TypeError) {
      showNotice("Your saved game could not be read. Start a new game to create a fresh profile.");
    } else if (error.name === "SecurityError") {
      showNotice("Browser storage is unavailable. The game can be played, but progress will not be saved.");
    } else if (error.message !== "The saved profile has an unexpected format.") {
      showNotice("Your saved game could not be loaded: " + error.message);
    } else {
      showNotice(error.message);
    }
    return null;
  }
}

function saveProfile() {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(profile));
  } catch (error) {
    showNotice("Progress could not be saved in this browser: " + error.message);
  }
}

function showNotice(message) {
  notice.textContent = message;
  notice.classList.add("visible");
  clearTimeout(noticeTimer);
  noticeTimer = setTimeout(() => notice.classList.remove("visible"), 6000);
}

function escapeHTML(value) {
  return String(value).replace(/[&<>"']/g, character => ({
    "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
  })[character]);
}

function rankInfo() {
  const ranks = [
    { title: "Flight Trainee", points: 0 },
    { title: "Cadet", points: 100 },
    { title: "Specialist", points: 200 },
    { title: "Commander", points: 300 },
    { title: "Mission Legend", points: 400 }
  ];
  const score = totalScore();
  const index = ranks.findLastIndex(rank => score >= rank.points);
  const current = ranks[index];
  const next = ranks[index + 1];
  return {
    title: current.title,
    progress: next ? Math.round((score - current.points) / (next.points - current.points) * 100) : 100,
    nextPoints: next ? next.points - score : 0
  };
}

function renderChrome() {
  const rank = profile ? rankInfo() : null;
  return `<header class="game-chrome">
    <div class="game-brand"><span class="brand-mark" aria-hidden="true">✦</span>
      <span><strong>SPACE MISSION</strong><small>BEYOND EARTH · TRAINING SIMULATOR</small></span></div>
    <div class="chrome-status"><span class="signal-dot"></span>FLIGHT SYSTEMS ONLINE</div>
    ${profile ? `<div class="player-chip"><span class="player-avatar">${escapeHTML(profile.character.charAt(0))}</span>
      <span class="player-name">${escapeHTML(profile.name)}<small>${rank.title}</small></span>
      <span class="player-score">${totalScore()}<small>PTS</small></span></div>`
      : `<div class="academy-tag">ASTRONAUT ACADEMY</div>`}
  </header>
  ${profile ? `<section class="career-progress" aria-label="Career rank progress">
    <span class="career-label">CAREER RANK</span><strong>${rank.title}</strong>
    <div class="career-bar"><span style="width:${rank.progress}%"></span></div>
    <span class="career-next">${rank.nextPoints ? `${rank.nextPoints} pts to next rank` : "Top rank achieved"}</span>
  </section>` : ""}`;
}

function setPage(content, pageClass = "") {
  app.className = "app";
  app.innerHTML = `${renderChrome()}<div class="page-content ${pageClass}">${content}</div>
    <footer class="game-footer"><span>SPACE MISSION: BEYOND EARTH</span><span>MISSION CONTROL · TRAINING SIM 01</span></footer>`;
}

function button(label, action, className = "") {
  return `<button class="button ${className}" data-action="${action}">${label}</button>`;
}

function renderHome() {
  const completed = profile ? profile.completed.length : 0;
  setPage(`
    <section class="home-hero">
      <div class="hero-copy">
        <div class="eyebrow"><span class="eyebrow-line"></span>ASTRONAUT TRAINING PROGRAM</div>
        <h1>YOUR NEXT<br>FRONTIER IS<br><span>OUT THERE.</span></h1>
        <p class="lead">Train for the unknown. Master microgravity, space science, and the split-second decisions that bring a crew home.</p>
        ${profile ? `<div class="welcome-chip"><span class="signal-dot"></span>WELCOME BACK, ${escapeHTML(profile.name.toUpperCase())}</div>` : ""}
        <div class="menu-actions">
          ${button(profile ? "Start New Career" : "Begin Training", "start", "primary")}
          <button class="button secondary-action" data-action="continue" ${profile ? "" : "disabled"}>Continue Mission</button>
          ${button("Training Guide", "instructions", "subtle")}
        </div>
      </div>
      <div class="hero-illustration" aria-hidden="true">
        <div class="orbit orbit-wide"></div><div class="orbit orbit-tight"></div>
        <div class="hero-planet"><span></span></div><div class="hero-moon"></div>
        <div class="hero-ship">✦</div><div class="hero-coordinates">SECTOR 01<br>EARTH ORBIT</div>
      </div>
    </section>
    <section class="home-briefing">
      <div class="briefing-item"><span class="briefing-icon">◈</span><span><strong>04</strong><small>MISSION CHALLENGES</small></span></div>
      <div class="briefing-item"><span class="briefing-icon">⌁</span><span><strong>02</strong><small>TRAINING LEVELS</small></span></div>
      <div class="briefing-item"><span class="briefing-icon">✧</span><span><strong>${completed} / 4</strong><small>MISSIONS CLEARED</small></span></div>
      <div class="briefing-tag">YOUR JOURNEY STARTS HERE <span>↗</span></div>
    </section>`, "home-page");
}

function renderNameEntry() {
  setPage(`
    <header class="section-heading"><div><div class="eyebrow">NEW MISSION PROFILE</div><h1>Welcome aboard</h1></div>
      <div class="top-actions">${button("Main menu", "home", "subtle")}</div></header>
    <section class="card form-stage">
      <div class="stage-emblem">✦</div>
      <form id="name-form">
        <label for="player-name"><h2>What should we call you, astronaut?</h2></label>
        <p class="prompt">Your callsign will appear in mission control. Your progress is saved in this browser.</p>
        <input id="player-name" name="name" maxlength="24" autocomplete="given-name" required
          placeholder="Enter your name" value="${escapeHTML(draftName)}"
          style="width:100%;padding:13px 15px;border:1px solid var(--line);border-radius:11px;background:#091329;color:var(--text);margin-bottom:18px">
        <button class="button primary" type="submit">Set callsign &amp; continue <span>→</span></button>
      </form>
    </section>`, "form-page");
}

function renderCharacterSelection() {
  setPage(`
    <header class="section-heading"><div><div class="eyebrow">CREW SELECTION · ${escapeHTML(draftName.toUpperCase())}</div><h1>Choose your astronaut</h1>
      <p>Select the crew member who will join you on this mission.</p></div>
      <div class="top-actions">${button("Back to menu", "home", "subtle")}</div></header>
    <section class="character-grid">
      ${characterCard("Ali", "#55c7e8", "A calm, curious trainee who loves spacecraft engineering.")}
      ${characterCard("Alicia", "#ff9f70", "A bold problem-solver ready to explore beyond Earth.")}
    </section>`, "character-page");
}

function characterCard(name, color, description) {
  return `<article class="card character-card">
    <div class="astronaut" style="--suit:${color}" aria-hidden="true"><div class="helmet"></div><div class="body-suit"></div></div>
    <div class="crew-role">FLIGHT CREW · ${name === "Ali" ? "ENGINEERING" : "EXPLORATION"}</div>
    <h2>${name}</h2><p>${description}</p>${button(`Select ${name}`, `character-${name}`, "primary")}
  </article>`;
}

function renderMissionControl() {
  const completedBasic = profile.completed.includes(1) && profile.completed.includes(2);
  const cleared = profile.completed.length;
  setPage(`
    <header class="section-heading"><div><div class="eyebrow">MISSION CONTROL · FLIGHT DECK 01</div><h1>Campaign overview</h1>
        <p>Welcome back, ${escapeHTML(profile.name)}. Choose your next objective.</p></div>
      <div class="top-actions">${button("Change astronaut", "change-character")}
        ${button("Main menu", "home", "subtle")}</div>
    </header>
    <section class="campaign-progress">
      <div class="campaign-copy"><span class="campaign-icon">⌖</span><span><strong>CAMPAIGN PROGRESS</strong><small>${cleared} OF 4 OBJECTIVES CLEARED</small></span></div>
      <div class="campaign-track" aria-label="${cleared} of 4 missions complete">${missions.map((mission, index) =>
        `<span class="${profile.completed.includes(mission.id) ? "cleared" : ""} ${index === 2 && !completedBasic ? "track-locked" : ""}"></span>`).join("")}</div>
      <span class="campaign-points">${totalScore()} <small>PTS</small></span>
    </section>
    <section class="level-grid">
      ${levelCard("Basic Training", "Master movement in microgravity and pass your knowledge check.", [missions[0], missions[1]], true, 1)}
      ${levelCard("Spacecraft Operations",
        completedBasic ? "Unlocked — complete a spacewalk repair and respond to emergencies." : "Locked — complete both Level 1 missions to qualify.",
        [missions[2], missions[3]], completedBasic, 2)}
    </section>`, "control-page");
}

function levelCard(title, description, list, unlocked, level) {
  return `<article class="card level-card">
    <div class="level-heading"><span class="level-number ${unlocked ? "" : "dimmed"}">0${level}</span>
      <div><div class="level-label">LEVEL 0${level} ${level === 1 ? "· FOUNDATION" : "· ADVANCED"}</div><h2>${title}</h2></div>
      <span class="status-tag ${unlocked ? "status-open" : "status-locked"}">${unlocked ? "AVAILABLE" : "LOCKED"}</span></div>
    <p class="${unlocked ? "" : "locked"}">${description}</p>
    <div class="mission-list">${list.map(mission => {
      const score = profile.bestScores[mission.id];
      const completed = profile.completed.includes(mission.id);
      return `<div class="mission-item ${completed ? "mission-cleared" : ""}">
        <span class="mission-token ${completed ? "token-cleared" : ""}">${completed ? "✓" : `0${mission.id}`}</span>
        <div class="mission-info"><span class="mission-type">MISSION 0${mission.id}</span><h3>${mission.name}</h3>
          <p>${score === undefined ? mission.detail : `Best score · ${score} / 100`}</p></div>
        <span class="mission-state ${completed ? "state-cleared" : ""}">${completed ? "CLEARED" : "READY"}</span>
        <button class="button ${completed ? "replay-button" : "mission-launch"}" data-action="mission-${mission.id}" ${unlocked ? "" : "disabled"}>${completed ? "Replay ↻" : "Launch →"}</button></div>`;
    }).join("")}</div></article>`;
}

function renderInstructions() {
  setPage(`
    <header class="section-heading"><div><div class="eyebrow">FLIGHT MANUAL · TRAINEE BRIEFING</div><h1>How to Play</h1></div>
      <div class="top-actions">${button("Main menu", "home", "subtle")}</div></header>
    <section class="card instructions"><div class="manual-stamp">FIELD GUIDE <span>SMB · 01</span></div>
      <h2>Your training path</h2>
      <ul><li>Choose Ali or Alicia. Your astronaut and mission progress are saved in this browser.</li>
      <li>Complete both Basic Training missions to unlock spacecraft operations.</li>
      <li>Replay any unlocked mission to practice. Your best score for each mission is kept.</li></ul>
      <h2>Zero-Gravity Training</h2><p>Use arrow keys, WASD, or the on-screen controls. Collect all three training beacons, then reach the green airlock. Avoid floating debris.</p>
      <h2>Space Knowledge Assessment</h2><p>Select one of four answers. You will see an explanation after every answer.</p>
      <h2>Spacewalk &amp; Satellite Repair</h2><p>Complete each repair before oxygen runs out. Each repair and the oxygen remaining contribute to your score.</p>
      <h2>Emergency Survival</h2><p>Read each spacecraft scenario and select the safest procedure. Explanations reinforce the correct response.</p>
      <p>Your total score and mission completion status are saved automatically in local browser storage.</p>
    </section>`, "manual-page");
}

function renderMissionHeader(id, title, subtitle) {
  const category = id <= 2 ? "BASIC TRAINING" : "SPACECRAFT OPERATIONS";
  return `<header class="mission-banner">
    <span class="mission-emblem">0${id}</span>
    <div class="mission-banner-copy"><div class="eyebrow">MISSION 0${id} <span>·</span> ${category}</div><h1>${title}</h1><p>${subtitle}</p></div>
    <span class="mission-live"><i></i>IN PROGRESS</span>
    <div class="mission-exit">${button("Leave mission", "missions", "subtle")}</div>
  </header>`;
}

function objectiveProgress(current, total, label) {
  const percent = Math.round(current / total * 100);
  return `<div class="objective-progress"><div class="objective-label"><span>${label}</span><strong>${current} <small>/ ${total}</small></strong></div>
    <div class="objective-track" role="progressbar" aria-label="${label}" aria-valuenow="${current}" aria-valuemin="0" aria-valuemax="${total}">
      <span style="width:${percent}%"></span></div></div>`;
}

function renderTraining() {
  const cells = [];
  for (let y = 0; y < 5; y++) {
    for (let x = 0; x < 8; x++) {
      const key = `${x},${y}`;
      let icon = "", kind = "";
      if (round.x === x && round.y === y) { icon = "🧑‍🚀"; kind = "player"; }
      else if (debris.has(key)) { icon = "✹"; kind = "debris"; }
      else if (beacons.includes(key) && !round.collected.includes(key)) { icon = "✦"; kind = "beacon"; }
      else if (key === airlock) { icon = "▣"; kind = "airlock"; }
      cells.push(`<div class="cell ${kind}" aria-label="${kind || "empty"}">${icon}</div>`);
    }
  }
  setPage(`${renderMissionHeader(1, "Zero-Gravity Training", "Collect all 3 glowing beacons, avoid debris, then reach the green airlock.")}
    <div class="mission-body"><section class="training-layout">
      <div class="board" role="img" aria-label="Training grid: astronaut, beacons, debris, and airlock">${cells.join("")}</div>
      <div class="training-hud"><div>${objectiveProgress(round.collected.length, 3, "TRAINING BEACONS")}</div>
        <span class="moves-counter"><small>FLIGHT MOVES</small><strong>${String(round.moves).padStart(2, "0")}</strong></span></div>
      <div class="training-status"><span>${round.collected.length === 3 ? "AIRLOCK UNLOCKED · REACH THE EXIT" : "COLLECT ALL BEACONS TO UNLOCK AIRLOCK"}</span><span>ARROWS / WASD</span></div>
      <div class="dpad" aria-label="Movement controls">
        <button class="button up" data-action="move-up" aria-label="Move up">↑</button>
        <button class="button left" data-action="move-left" aria-label="Move left">←</button>
        <button class="button down" data-action="move-down" aria-label="Move down">↓</button>
        <button class="button right" data-action="move-right" aria-label="Move right">→</button>
      </div>
    </section></div>`, "mission-page");
}

function renderQuiz() {
  const [text, options] = questions[round.index];
  setPage(`${renderMissionHeader(2, "Space Knowledge Assessment", "Choose the best answer. Explanations follow every response.")}
    <div class="mission-body"><section class="card mission-card quiz-card">
      <div class="quiz-topline"><div class="progress-label">KNOWLEDGE CHECK <span>·</span> QUESTION ${String(round.index + 1).padStart(2, "0")}/${questions.length}</div>
        ${objectiveProgress(round.index + (round.selected !== null ? 1 : 0), questions.length, "ASSESSMENT PROGRESS")}</div>
      <h2>${text}</h2>
      <div class="options">${options.map((option, i) => `<button class="button option ${round.selected === i ? "selected" : ""}" data-action="answer-${i}" ${round.selected !== null ? "disabled" : ""}>${String.fromCharCode(65 + i)}　${option}</button>`).join("")}</div>
      <p class="feedback ${round.feedbackClass || ""}">${round.feedback || "Choose the best answer."}</p>
      ${round.selected !== null ? `<div class="quiz-footer"><span></span>${button(round.index === questions.length - 1 ? "See results" : "Next question", "advance", "primary")}</div>` : ""}
    </section></div>`, "mission-page");
}

function renderEmergency() {
  const scenario = emergencies[round.index];
  setPage(`${renderMissionHeader(4, "Emergency Survival Simulation", "Select the safest procedure for each situation.")}
    <div class="mission-body"><section class="card mission-card quiz-card">
      <div class="quiz-topline"><div class="progress-label">EMERGENCY SIMULATION <span>·</span> SCENARIO 0${round.index + 1}/${emergencies.length}</div>
        ${objectiveProgress(round.index + (round.selected !== null ? 1 : 0), emergencies.length, "CREW SAFETY CHECK")}</div>
      <div class="scenario-heading"><span class="alert-icon">!</span><div><h2>${scenario.title}</h2><p class="prompt">${scenario.prompt}</p></div></div>
      <div class="options">${scenario.choices.map((choice, i) => `<button class="button option ${round.selected === i ? "selected" : ""}" data-action="answer-${i}" ${round.selected !== null ? "disabled" : ""}>${String.fromCharCode(65 + i)}　${choice}</button>`).join("")}</div>
      <p class="feedback ${round.feedbackClass || ""}">${round.feedback || "Select the safest procedure."}</p>
      ${round.selected !== null ? `<div class="quiz-footer"><span></span>${button(round.index === emergencies.length - 1 ? "See results" : "Next scenario", "advance", "primary")}</div>` : ""}
    </section></div>`, "mission-page");
}

function renderRepair() {
  const task = round.task;
  const failed = round.oxygen <= 0;
  setPage(`${renderMissionHeader(3, "Spacewalk & Satellite Repair", "Work carefully outside the spacecraft. Oxygen is limited.")}
    <div class="mission-body"><section class="card mission-card">
      <div class="repair-scene" aria-hidden="true"><div class="satellite-illustration">
        <div class="solar-panel panel-left"><i></i><i></i><i></i></div><div class="satellite-core"><span>◈</span></div>
        <div class="solar-panel panel-right"><i></i><i></i><i></i></div><div class="repair-astronaut">🧑‍🚀</div>
      </div><span class="scene-label">EXTERNAL PAYLOAD · EVA LINK ACTIVE</span></div>
      <div class="oxygen-row"><div class="oxygen-reading"><span>SUIT OXYGEN</span><strong>${String(round.oxygen).padStart(2, "0")}<small>%</small></strong></div><div class="oxygen-bar" role="progressbar" aria-label="Oxygen" aria-valuenow="${round.oxygen}" aria-valuemin="0" aria-valuemax="100">
        <div class="oxygen-fill ${round.oxygen < 25 ? "low" : ""}" style="width:${round.oxygen}%"></div></div></div>
      <div class="repair-task-header"><div><div class="eyebrow">CURRENT EVA OBJECTIVE</div>
        <h2>${failed ? "Oxygen depleted" : `Repair ${String(task + 1).padStart(2, "0")} · ${repairTasks[task]}`}</h2>
        <p class="prompt">${failed ? "Return to mission control and try again." : "Complete the procedure before your suit reserve runs out."}</p></div>
        ${objectiveProgress(task, repairTasks.length, "REPAIRS COMPLETE")}</div>
      <div class="repair-actions">${failed
        ? button("Return to mission control", "missions", "primary")
        : button("Complete repair task", "repair", "primary")}</div>
    </section></div>`, "mission-page");
}

function renderResult() {
  const mission = missions.find(item => item.id === round.mission);
  const score = round.score;
  const best = round.previousBest;
  const message = score >= 85 ? "Excellent work, mission specialist!"
    : score >= 60 ? "Good progress. Keep building your skills."
      : "Training complete. Replay to improve your score.";
  const stars = score >= 85 ? "★★★" : score >= 60 ? "★★☆" : "★☆☆";
  setPage(`<section class="card result-card">
    <div class="result-orbit"><div class="result-medal">✦</div></div>
    <div class="eyebrow">MISSION 0${mission.id} · DEBRIEF COMPLETE</div><h1>Mission complete</h1><p class="lead">${mission.name}</p>
    <div class="result-stars" aria-label="${score >= 85 ? "Three" : score >= 60 ? "Two" : "One"} star rating">${stars}</div>
    <div class="score ${score < 70 ? "low" : ""}">${score} <span>/ 100</span></div>
    <p class="result-message">${message}</p>
    <div class="result-stat-row"><div><small>PREVIOUS BEST</small><strong>${best} <span>PTS</span></strong></div>
      <div><small>CAREER TOTAL</small><strong>${totalScore()} <span>PTS</span></strong></div>
      <div><small>RANK</small><strong>${rankInfo().title}</strong></div></div>
    <div class="result-actions">${button("Replay mission", `mission-${mission.id}`, "primary")}${button("Back to mission control", "missions")}</div>
  </section>`, "result-page");
}

function totalScore() {
  return Object.values(profile.bestScores).reduce((sum, score) => sum + score, 0);
}

function completeMission(id, score) {
  const previousBest = profile.bestScores[id] || 0;
  profile.bestScores[id] = Math.max(profile.bestScores[id] || 0, score);
  if (!profile.completed.includes(id)) profile.completed.push(id);
  saveProfile();
  round = { mission: id, score, previousBest };
  screen = "result";
  render();
}

function startMission(id) {
  if (id > 2 && !(profile.completed.includes(1) && profile.completed.includes(2))) {
    showNotice("Complete both Basic Training missions to unlock Level 2.");
    return;
  }
  if (id === 1) {
    round = { mission: id, x: 0, y: 0, moves: 0, collected: [] };
    screen = "training";
  } else if (id === 2) {
    round = { mission: id, index: 0, correct: 0, selected: null, feedback: "", feedbackClass: "" };
    screen = "quiz";
  } else if (id === 3) {
    round = { mission: id, task: 0, oxygen: 100, timer: null };
    screen = "repair";
    round.timer = setInterval(() => {
      if (screen !== "repair") return;
      round.oxygen = Math.max(0, round.oxygen - 1);
      if (round.oxygen === 0) clearInterval(round.timer);
      renderRepair();
    }, 1000);
  } else {
    round = { mission: id, index: 0, correct: 0, selected: null, feedback: "", feedbackClass: "" };
    screen = "emergency";
  }
  render();
}

function move(dx, dy) {
  if (screen !== "training") return;
  const x = round.x + dx, y = round.y + dy, key = `${x},${y}`;
  if (x < 0 || x > 7 || y < 0 || y > 4 || debris.has(key)) return;
  round.x = x;
  round.y = y;
  round.moves++;
  if (beacons.includes(key) && !round.collected.includes(key)) round.collected.push(key);
  if (round.collected.length === 3 && key === airlock) {
    const score = Math.max(50, 100 - Math.max(0, round.moves - 12) * 2);
    completeMission(1, score);
  } else renderTraining();
}

function answer(choice) {
  if (round.selected !== null) return;
  const isEmergency = screen === "emergency";
  const item = isEmergency ? emergencies[round.index] : questions[round.index];
  const correct = isEmergency ? item.correct : item[2];
  round.selected = choice;
  if (choice === correct) {
    round.correct++;
    round.feedback = isEmergency ? `Correct response. ${item.explanation}` : `Correct! ${item[3]}`;
    round.feedbackClass = "correct";
  } else {
    round.feedback = isEmergency ? `Review this response. ${item.explanation}` : `Not quite. ${item[3]}`;
    round.feedbackClass = "incorrect";
  }
  isEmergency ? renderEmergency() : renderQuiz();
}

function advanceQuestion() {
  const isEmergency = screen === "emergency";
  const total = isEmergency ? emergencies.length : questions.length;
  if (round.index + 1 < total) {
    round.index++;
    round.selected = null;
    round.feedback = "";
    round.feedbackClass = "";
    isEmergency ? renderEmergency() : renderQuiz();
  } else {
    completeMission(round.mission, Math.round(round.correct * 100 / total));
  }
}

function completeRepair() {
  if (round.oxygen <= 0) return;
  round.oxygen = Math.max(0, round.oxygen - 5);
  round.task++;
  if (round.task === repairTasks.length) {
    clearInterval(round.timer);
    completeMission(3, Math.min(100, 60 + Math.floor(round.oxygen / 3)));
  } else if (round.oxygen === 0) {
    clearInterval(round.timer);
    renderRepair();
  } else renderRepair();
}

function render() {
  switch (screen) {
    case "home": renderHome(); break;
    case "name": renderNameEntry(); break;
    case "character": renderCharacterSelection(); break;
    case "missions": renderMissionControl(); break;
    case "instructions": renderInstructions(); break;
    case "training": renderTraining(); break;
    case "quiz": renderQuiz(); break;
    case "repair": renderRepair(); break;
    case "emergency": renderEmergency(); break;
    case "result": renderResult(); break;
    default: screen = "home"; renderHome();
  }
}

app.addEventListener("click", event => {
  const target = event.target.closest("[data-action]");
  if (!target || target.disabled) return;
  const action = target.dataset.action;
  if (action === "home") {
    if (round?.timer) clearInterval(round.timer);
    screen = "home";
  } else if (action === "start") {
    draftName = "";
    screen = "name";
  } else if (action === "continue") {
    if (profile) { draftName = profile.name; screen = profile.character ? "missions" : "character"; }
  } else if (action === "instructions") screen = "instructions";
  else if (action === "change-character") { draftName = profile.name; screen = "character"; }
  else if (action.startsWith("character-")) {
    const character = action.slice("character-".length);
    if (!profile) profile = { name: draftName, character, bestScores: {}, completed: [] };
    else profile.character = character;
    saveProfile();
    screen = "missions";
  } else if (action === "missions") {
    if (round?.timer) clearInterval(round.timer);
    screen = "missions";
  } else if (action.startsWith("mission-")) startMission(Number(action.slice("mission-".length)));
  else if (action.startsWith("move-")) {
    const directions = { "move-up": [0, -1], "move-down": [0, 1], "move-left": [-1, 0], "move-right": [1, 0] };
    move(...directions[action]);
  } else if (action.startsWith("answer-")) answer(Number(action.slice("answer-".length)));
  else if (action === "advance") advanceQuestion();
  else if (action === "repair") completeRepair();
  render();
});

app.addEventListener("submit", event => {
  if (event.target.id !== "name-form") return;
  event.preventDefault();
  const name = new FormData(event.target).get("name").trim();
  if (!name) {
    showNotice("Please enter a name to begin your training.");
    return;
  }
  draftName = name;
  profile = null;
  screen = "character";
  render();
});

document.addEventListener("keydown", event => {
  if (screen !== "training" || event.altKey || event.ctrlKey || event.metaKey) return;
  const moves = {
    ArrowUp: [0, -1], w: [0, -1], W: [0, -1],
    ArrowDown: [0, 1], s: [0, 1], S: [0, 1],
    ArrowLeft: [-1, 0], a: [-1, 0], A: [-1, 0],
    ArrowRight: [1, 0], d: [1, 0], D: [1, 0]
  };
  if (moves[event.key]) {
    event.preventDefault();
    move(...moves[event.key]);
  }
});

render();
