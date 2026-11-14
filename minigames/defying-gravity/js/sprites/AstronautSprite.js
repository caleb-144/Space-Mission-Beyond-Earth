/** Pixel art for the astronaut. W=white, B=blue shade, Y=yellow visor, O=orange glint, G=boots. */
class AstronautSprite {
  static SCALE = 3;

  static MAP = [
    '..WWWWWW..',
    '.WWYYYYWW.',
    '.WYYOYYYW.',
    '.WYOOYYYW.',
    '.WWYYYYWW.',
    '..WWWWWW..',
    '.WWBBBBWW.',
    'WWWWWWWWWW',
    'WW.WWWW.WW',
    '...WWWW...',
    '...WBBW...',
    '..WW..WW..',
    '..GG..GG..'
  ];

  static PALETTE = {
    W: '#f1f7ff',
    B: '#4f9fc7',
    Y: '#70e8df',
    O: '#ff9a62',
    G: '#6e86ba'
  };

  static create() {
    return SpriteFactory.fromMap(AstronautSprite.MAP, AstronautSprite.PALETTE, AstronautSprite.SCALE);
  }
}
