/** A column of stacked asteroids with a gap in the middle (the "pipe"). */
class AsteroidColumn {
  /**
   * @param {HTMLCanvasElement[]} sprites  asteroid sprite variations
   * @param {number} variant               which variation to start the stack with
   */
  constructor(sprites, variant) {
    this.sprites = sprites;
    this.variant = variant;
    this.x = CONFIG.WIDTH + CONFIG.ASTEROID_W;
    this.gapY = CONFIG.GAP / 2 + 50 + Math.random() * (CONFIG.HEIGHT - CONFIG.GAP - 100);
    this.passed = false;
  }

  update(dt) {
    this.x -= CONFIG.SCROLL_SPEED * dt;
  }

  get isOffscreen() {
    return this.x <= -CONFIG.ASTEROID_W;
  }

  /** True once the astronaut has flown past this column. */
  hasPassed(astronautX) {
    return !this.passed && this.x < astronautX;
  }

  /** Does the astronaut touch the asteroids? */
  collidesWith(astronaut) {
    const half = CONFIG.ASTEROID_W / 2;
    const overlapsX =
      astronaut.x + CONFIG.HITBOX_HALF_W > this.x - half + CONFIG.COLLISION_PAD_X &&
      astronaut.x - CONFIG.HITBOX_HALF_W < this.x + half - CONFIG.COLLISION_PAD_X;
    if (!overlapsX) return false;

    const gapTop = this.gapY - CONFIG.GAP / 2 + CONFIG.COLLISION_PAD_Y;
    const gapBottom = this.gapY + CONFIG.GAP / 2 - CONFIG.COLLISION_PAD_Y;
    return astronaut.y - CONFIG.HITBOX_HALF_H < gapTop || astronaut.y + CONFIG.HITBOX_HALF_H > gapBottom;
  }

  draw(ctx) {
    const { ASTEROID_W, ASTEROID_H, ASTEROID_STEP, GAP, HEIGHT } = CONFIG;
    const drawX = Math.round(this.x - ASTEROID_W / 2);
    const count = this.sprites.length;

    // Upper stack grows upwards from the gap
    let i = 0;
    for (let y = this.gapY - GAP / 2 - ASTEROID_H; y > -ASTEROID_H; y -= ASTEROID_STEP, i++) {
      ctx.drawImage(this.sprites[(i + this.variant) % count], drawX, Math.round(y));
    }

    // Lower stack grows downwards from the gap
    i = 0;
    for (let y = this.gapY + GAP / 2; y < HEIGHT; y += ASTEROID_STEP, i++) {
      ctx.drawImage(this.sprites[(i + this.variant + 1) % count], drawX, Math.round(y));
    }
  }
}
