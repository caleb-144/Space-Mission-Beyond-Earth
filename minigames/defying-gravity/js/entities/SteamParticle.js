/** One puff of steam. */
class SteamParticle {
  constructor(x, y) {
    this.x = x;
    this.y = y;
    this.vx = -CONFIG.SCROLL_SPEED - 0.3 - Math.random() * 1.3;
    this.vy = 0.8 + Math.random() * 2;
    this.life = 1;
    this.size = 5 + ((Math.random() * 5) | 0);
  }

  get alive() {
    return this.life > 0;
  }

  update(dt) {
    this.x += this.vx * dt;
    this.y += this.vy * dt;
    this.vy *= 0.96;
    this.life -= 0.035 * dt;
    this.size += 0.12 * dt;
  }

  draw(ctx) {
    ctx.globalAlpha = Math.max(0, this.life) * 0.85;
    ctx.fillStyle = this.life > 0.6 ? '#ffffff' : '#c8d2f0';
    const s = Math.round(this.size);
    ctx.fillRect(Math.round(this.x - this.size / 2), Math.round(this.y - this.size / 2), s, s);
    ctx.globalAlpha = 1;
  }
}

/** Owns every steam particle: spawns, updates and draws them. */
class SteamSystem {
  constructor() {
    this.particles = [];
  }

  /** Release a burst of steam under the astronaut. */
  burst(astronaut) {
    for (let i = 0; i < 9; i++) {
      this.particles.push(new SteamParticle(astronaut.x - 6 + Math.random() * 10, astronaut.y + 16));
    }
  }

  update(dt) {
    this.particles.forEach(p => p.update(dt));
    this.particles = this.particles.filter(p => p.alive);
  }

  draw(ctx) {
    this.particles.forEach(p => p.draw(ctx));
  }

  clear() {
    this.particles = [];
  }
}
