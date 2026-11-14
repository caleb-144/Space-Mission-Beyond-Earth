/** Draws every piece of text and overlay: score, how-to-play, pause and game over. */
class UI {
  static FONT = 'system-ui, sans-serif';

  static GUIDE_LINES = [
    '1. Press SPACE, ENTER or LEFT CLICK (tap on phone) to jump.',
    '2. Gravity pulls you down, so keep jumping to stay up.',
    '3. Fly through the gaps between the asteroids to score.',
    '4. Do not touch the asteroids or the bottom of the screen.',
    '5. Press ESC (or P) to pause or resume.'
  ];

  /** Is the point (x, y) on the on-screen pause button? */
  static isPauseButton(x, y) {
    return x > CONFIG.WIDTH - 80 && y < 80;
  }

  draw(ctx, game) {
    this.drawScore(ctx, game.score);

    if (game.state === GameState.READY) this.drawGuide(ctx, game.best);
    if (game.state === GameState.PLAYING && !game.paused) this.drawPauseButton(ctx);
    if (game.paused) this.drawPauseOverlay(ctx);
    if (game.state === GameState.DEAD) this.drawGameOver(ctx, game.score, game.best);
  }

  drawScore(ctx, score) {
    ctx.textAlign = 'center';
    ctx.fillStyle = '#fff';
    ctx.font = `bold 56px ${UI.FONT}`;
    ctx.fillText(score, CONFIG.WIDTH / 2, 70);
  }

  drawGuide(ctx, best) {
    const x = 320, y = 95, w = 600, h = 360;

    ctx.fillStyle = 'rgba(20,18,42,.78)';
    ctx.fillRect(x, y, w, h);
    ctx.strokeStyle = '#ffd23f';
    ctx.lineWidth = 3;
    ctx.strokeRect(x, y, w, h);

    ctx.textAlign = 'left';
    ctx.fillStyle = '#ffd23f';
    ctx.font = `bold 32px ${UI.FONT}`;
    ctx.fillText('HOW TO PLAY', x + 30, y + 50);

    ctx.fillStyle = '#fff';
    ctx.font = `20px ${UI.FONT}`;
    UI.GUIDE_LINES.forEach((line, i) => ctx.fillText(line, x + 30, y + 95 + i * 40));

    ctx.textAlign = 'center';
    ctx.fillStyle = performance.now() % 1000 < 600 ? '#ffd23f' : '#8b93b8';
    ctx.font = `bold 22px ${UI.FONT}`;
    ctx.fillText('Press SPACE, ENTER or CLICK to start', x + w / 2, y + h - 50);

    ctx.fillStyle = '#b9c6f2';
    ctx.font = `16px ${UI.FONT}`;
    ctx.fillText('Best: ' + best, x + w / 2, y + h - 20);
  }

  drawPauseButton(ctx) {
    const W = CONFIG.WIDTH;
    ctx.fillStyle = 'rgba(255,255,255,.75)';
    ctx.fillRect(W - 52, 20, 10, 32);
    ctx.fillRect(W - 34, 20, 10, 32);
  }

  drawPauseOverlay(ctx) {
    const W = CONFIG.WIDTH, H = CONFIG.HEIGHT;
    ctx.fillStyle = 'rgba(20,18,42,.75)';
    ctx.fillRect(0, 0, W, H);
    ctx.textAlign = 'center';
    ctx.fillStyle = '#fff';
    ctx.font = `bold 56px ${UI.FONT}`;
    ctx.fillText('PAUSED', W / 2, H / 2 - 10);
    ctx.fillStyle = '#ffd23f';
    ctx.font = `22px ${UI.FONT}`;
    ctx.fillText('Press ESC, P or click to resume', W / 2, H / 2 + 40);
  }

  drawGameOver(ctx, score, best) {
    const W = CONFIG.WIDTH, H = CONFIG.HEIGHT;
    ctx.fillStyle = 'rgba(20,18,42,.7)';
    ctx.fillRect(W / 2 - 190, H / 2 - 110, 380, 220);
    ctx.textAlign = 'center';
    ctx.fillStyle = '#fff';
    ctx.font = `bold 44px ${UI.FONT}`;
    ctx.fillText('GAME OVER', W / 2, H / 2 - 45);
    ctx.font = `26px ${UI.FONT}`;
    ctx.fillText(`Score: ${score}   Best: ${best}`, W / 2, H / 2 + 5);
    ctx.fillStyle = '#ffd23f';
    ctx.font = `20px ${UI.FONT}`;
    ctx.fillText('Press Space, Enter or click to retry', W / 2, H / 2 + 60);
  }
}
