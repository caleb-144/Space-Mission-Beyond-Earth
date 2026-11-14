/** Ties everything together: state, game loop, update and draw. */
class Game {
  constructor(canvas) {
    canvas.width = CONFIG.WIDTH;
    canvas.height = CONFIG.HEIGHT;
    this.ctx = canvas.getContext('2d');
    this.ctx.imageSmoothingEnabled = false;

    // Sprites
    this.background = BackgroundSprite.create(CONFIG.WIDTH, CONFIG.HEIGHT);
    this.asteroidSprites = AsteroidSprite.createSet();

    // Objects
    this.astronaut = new Astronaut(AstronautSprite.create());
    this.columns = [];
    this.steam = new SteamSystem();
    this.ui = new UI();

    // State
    this.state = GameState.READY;
    this.paused = false;
    this.score = 0;
    this.best = this.loadBest();
    this.deadAt = 0;
    this.bgOffset = 0;
    this.columnsSpawned = 0;
    this.lastTime = performance.now();

    this.input = new InputHandler(canvas, {
      onJump: () => this.jump(),
      onTogglePause: () => this.togglePause(),
      onPointer: (x, y) => this.handlePointer(x, y),
      onHidden: () => this.pauseIfPlaying()
    });

    this.loop = this.loop.bind(this);
  }

  start() {
    requestAnimationFrame(this.loop);
  }

  // ---------- input actions ----------

  jump() {
    if (this.paused) { this.paused = false; return; }

    if (this.state === GameState.READY) {
      this.state = GameState.PLAYING;
      this.astronaut.flap();
      this.steam.burst(this.astronaut);
    } else if (this.state === GameState.PLAYING) {
      this.astronaut.flap();
      this.steam.burst(this.astronaut);
    } else if (this.state === GameState.DEAD && performance.now() - this.deadAt > CONFIG.RETRY_DELAY_MS) {
      this.reset();
    }
  }

  togglePause() {
    if (this.paused) this.paused = false;
    else if (this.state === GameState.PLAYING) this.paused = true;
  }

  pauseIfPlaying() {
    if (this.state === GameState.PLAYING) this.paused = true;
  }

  handlePointer(x, y) {
    if (this.state === GameState.PLAYING && !this.paused && UI.isPauseButton(x, y)) {
      this.paused = true;
      return;
    }
    this.jump();
  }

  // ---------- game flow ----------

  reset() {
    this.astronaut.reset();
    this.columns = [];
    this.steam.clear();
    this.score = 0;
    this.bgOffset = 0;
    this.columnsSpawned = 0;
    this.state = GameState.READY;
  }

  gameOver() {
    this.state = GameState.DEAD;
    this.deadAt = performance.now();
    if (window.parent !== window) {
      window.parent.postMessage({ type: 'defying-gravity-result', score: this.score }, window.location.origin);
    }
    if (this.score > this.best) {
      this.best = this.score;
      this.saveBest();
    }
  }

  loadBest() {
    try { return +localStorage.getItem(CONFIG.STORAGE_KEY) || 0; } catch (e) { return 0; }
  }

  saveBest() {
    try { localStorage.setItem(CONFIG.STORAGE_KEY, this.best); } catch (e) { /* ignore */ }
  }

  spawnColumnIfNeeded() {
    const last = this.columns[this.columns.length - 1];
    if (!last || last.x < CONFIG.WIDTH - CONFIG.COLUMN_SPACING) {
      this.columns.push(new AsteroidColumn(this.asteroidSprites, this.columnsSpawned++ % this.asteroidSprites.length));
    }
  }

  // ---------- update / draw ----------

  update(dt) {
    if (this.paused) return;

    this.bgOffset = (this.bgOffset + dt * 0.4) % 6;
    this.steam.update(dt);

    if (this.state === GameState.READY) {
      this.astronaut.hover(performance.now());
      return;
    }
    if (this.state !== GameState.PLAYING) return;

    this.astronaut.update(dt);
    this.spawnColumnIfNeeded();

    for (const column of this.columns) {
      column.update(dt);
      if (column.hasPassed(this.astronaut.x)) {
        column.passed = true;
        this.score++;
      }
      if (column.collidesWith(this.astronaut)) return this.gameOver();
    }

    this.columns = this.columns.filter(c => !c.isOffscreen);
    if (this.astronaut.hitsFloor) this.gameOver();
  }

  draw() {
    const ctx = this.ctx;
    ctx.drawImage(this.background, -this.bgOffset, 0);
    ctx.drawImage(this.background, CONFIG.WIDTH - this.bgOffset, 0);

    this.columns.forEach(c => c.draw(ctx));
    this.steam.draw(ctx);
    this.astronaut.draw(ctx, this.state !== GameState.READY);
    this.ui.draw(ctx, this);
  }

  loop(time) {
    const dt = Math.min((time - this.lastTime) / 16.67, 2.5);
    this.lastTime = time;
    this.update(dt);
    this.draw();
    requestAnimationFrame(this.loop);
  }
}
