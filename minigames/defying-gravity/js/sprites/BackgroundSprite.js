/** Dark purple space background with pixel stars. Drawn once, reused every frame. */
class BackgroundSprite {
  static COLOR_SPACE = '#07152f';
  static COLOR_STAR_DIM = '#174b78';
  static COLOR_STAR_BRIGHT = '#55d8e8';
  static STAR_COUNT = 260;
  static STAR_GRID = 6;

  static create(width, height) {
    const canvas = SpriteFactory.createCanvas(width, height);
    const g = canvas.getContext('2d');
    const rand = SpriteFactory.seededRandom(7);
    const grid = BackgroundSprite.STAR_GRID;

    g.fillStyle = BackgroundSprite.COLOR_SPACE;
    g.fillRect(0, 0, width, height);

    for (let i = 0; i < BackgroundSprite.STAR_COUNT; i++) {
      g.fillStyle = rand() < 0.25 ? BackgroundSprite.COLOR_STAR_BRIGHT : BackgroundSprite.COLOR_STAR_DIM;
      g.fillRect(((rand() * width / grid) | 0) * grid + 3, ((rand() * height / grid) | 0) * grid + 3, 3, 3);
    }
    return canvas;
  }
}
