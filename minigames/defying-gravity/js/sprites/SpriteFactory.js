/** Small helpers shared by every sprite generator. */
class SpriteFactory {
  /** Create an offscreen canvas. */
  static createCanvas(width, height) {
    const canvas = document.createElement('canvas');
    canvas.width = width;
    canvas.height = height;
    return canvas;
  }

  /**
   * Turn a pixel map (array of strings) into a canvas.
   * Each character is looked up in `palette`; unknown characters stay transparent.
   */
  static fromMap(map, palette, scale) {
    const canvas = SpriteFactory.createCanvas(map[0].length * scale, map.length * scale);
    const g = canvas.getContext('2d');
    map.forEach((row, y) => {
      [...row].forEach((ch, x) => {
        if (palette[ch]) {
          g.fillStyle = palette[ch];
          g.fillRect(x * scale, y * scale, scale, scale);
        }
      });
    });
    return canvas;
  }

  /** Tiny seeded random generator so generated sprites look the same every time. */
  static seededRandom(seed) {
    return () => (seed = (seed * 16807) % 2147483647) / 2147483647;
  }
}
