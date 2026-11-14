/** The player. Handles its own physics, hitbox and drawing. */
class Astronaut {
  constructor(sprite) {
    this.sprite = sprite;
    this.x = CONFIG.PLAYER_X;
    this.reset();
  }

  reset() {
    this.y = CONFIG.HEIGHT / 2;
    this.vy = 0;
  }

  /** Jump upwards. */
  flap() {
    this.vy = CONFIG.FLAP_POWER;
  }

  /** Gentle floating animation before the game starts. */
  hover(timeMs) {
    this.y = CONFIG.HEIGHT / 2 + Math.sin(timeMs / 300) * 8;
  }

  /** Apply gravity. `dt` is 1 at 60 FPS. */
  update(dt) {
    this.vy = Math.min(this.vy + CONFIG.GRAVITY * dt, CONFIG.MAX_FALL_SPEED);
    this.y += this.vy * dt;
    if (this.y < CONFIG.CEILING_Y) {
      this.y = CONFIG.CEILING_Y;
      this.vy = 0;
    }
  }

  get hitsFloor() {
    return this.y > CONFIG.HEIGHT - CONFIG.FLOOR_MARGIN;
  }

  /** Tilt up when rising, nose down when falling. */
  get rotation() {
    return Math.max(-0.5, Math.min(1, this.vy * 0.07));
  }

  draw(ctx, tilt = true) {
    ctx.save();
    ctx.translate(this.x, this.y);
    ctx.rotate(tilt ? this.rotation : 0);
    ctx.drawImage(this.sprite, -this.sprite.width / 2, -this.sprite.height / 2);
    ctx.restore();
  }
}
