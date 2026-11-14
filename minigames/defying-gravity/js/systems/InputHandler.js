/**
 * Listens to keyboard, mouse and touch and calls back into the game.
 * Callbacks: onJump(), onTogglePause(), onPointer(x, y), onHidden()
 */
class InputHandler {
  static JUMP_KEYS = ['Space', 'Enter', 'NumpadEnter', 'ArrowUp'];
  static PAUSE_KEYS = ['Escape', 'KeyP'];

  constructor(canvas, callbacks) {
    this.canvas = canvas;
    this.cb = callbacks;

    canvas.tabIndex = 0;
    try { window.focus(); canvas.focus(); } catch (e) { /* ignore */ }

    addEventListener('keydown', e => this.onKeyDown(e));
    canvas.addEventListener('pointerdown', e => this.onPointerDown(e));
    canvas.addEventListener('contextmenu', e => e.preventDefault());
    document.addEventListener('visibilitychange', () => {
      if (document.hidden) this.cb.onHidden();
    });
  }

  onKeyDown(e) {
    if (InputHandler.PAUSE_KEYS.includes(e.code)) {
      e.preventDefault();
      if (!e.repeat) this.cb.onTogglePause();
    } else if (InputHandler.JUMP_KEYS.includes(e.code)) {
      e.preventDefault();
      if (!e.repeat) this.cb.onJump();
    }
  }

  onPointerDown(e) {
    e.preventDefault();
    this.canvas.focus();
    if (e.button !== 0) return; // left click / touch only

    // Convert screen position to canvas (logical) position
    const r = this.canvas.getBoundingClientRect();
    const x = (e.clientX - r.left) * CONFIG.WIDTH / r.width;
    const y = (e.clientY - r.top) * CONFIG.HEIGHT / r.height;
    this.cb.onPointer(x, y);
  }
}
