/** Procedurally generated pixel asteroids (3 variations). */
class AsteroidSprite {
  static SCALE = 3;
  static COLS = 24;
  static ROWS = 26;
  static SEEDS = [11, 29, 47];

  static COLOR_BASE = '#426b91';
  static COLOR_LIGHT = '#75a6c7';
  static COLOR_CRACK = '#203c63';

  /** Build one asteroid from a seed. */
  static create(seed) {
    const { COLS, ROWS, SCALE, COLOR_BASE, COLOR_LIGHT, COLOR_CRACK } = AsteroidSprite;
    const rand = SpriteFactory.seededRandom(seed);
    const canvas = SpriteFactory.createCanvas(COLS * SCALE, ROWS * SCALE);
    const g = canvas.getContext('2d');

    // 1. Rough round body
    const cells = [];
    for (let y = 0; y < ROWS; y++) {
      cells[y] = [];
      for (let x = 0; x < COLS; x++) {
        const dx = (x - 11.5) / 12;
        const dy = (y - 12.5) / 13;
        cells[y][x] = dx * dx + dy * dy < 0.8 + rand() * 0.2 ? COLOR_BASE : null;
      }
    }

    // 2. Random cracks
    for (let k = 0; k < 4; k++) {
      let x = (3 + rand() * 18) | 0;
      let y = (3 + rand() * 20) | 0;
      for (let i = 0; i < 9; i++) {
        if (cells[y] && cells[y][x]) cells[y][x] = COLOR_CRACK;
        x += rand() < 0.5 ? 1 : 0;
        y += rand() < 0.6 ? 1 : 0;
        x -= rand() < 0.2 ? 1 : 0;
      }
    }

    // 3. Paint, with a lighter top-left edge
    for (let y = 0; y < ROWS; y++) {
      for (let x = 0; x < COLS; x++) {
        if (!cells[y][x]) continue;
        g.fillStyle = cells[y][x] === COLOR_BASE && x + y < 14 ? COLOR_LIGHT : cells[y][x];
        g.fillRect(x * SCALE, y * SCALE, SCALE, SCALE);
      }
    }
    return canvas;
  }

  /** All variations as an array. */
  static createSet() {
    return AsteroidSprite.SEEDS.map(seed => AsteroidSprite.create(seed));
  }
}
