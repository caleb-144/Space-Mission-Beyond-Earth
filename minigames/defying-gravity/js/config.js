/** All tweakable game numbers live here. */
const CONFIG = Object.freeze({
  // Canvas (logical size, CSS scales it to the window)
  WIDTH: 960,
  HEIGHT: 540,

  // Physics
  GRAVITY: 0.45,
  FLAP_POWER: -7.2,
  MAX_FALL_SPEED: 10,
  SCROLL_SPEED: 2.6,

  // Astronaut
  PLAYER_X: 200,
  HITBOX_HALF_W: 11,
  HITBOX_HALF_H: 17,
  CEILING_Y: 14,
  FLOOR_MARGIN: 14,

  // Asteroid columns
  GAP: 190,               // opening the astronaut flies through
  COLUMN_SPACING: 330,    // distance between columns
  ASTEROID_W: 72,
  ASTEROID_H: 78,
  ASTEROID_STEP: 70,      // vertical distance between stacked asteroids
  COLLISION_PAD_X: 6,     // forgiving horizontal hitbox
  COLLISION_PAD_Y: 4,     // forgiving vertical hitbox

  // Game flow
  RETRY_DELAY_MS: 450,
  STORAGE_KEY: 'astroBest'
});

const GameState = Object.freeze({
  READY: 'ready',
  PLAYING: 'play',
  DEAD: 'dead'
});
