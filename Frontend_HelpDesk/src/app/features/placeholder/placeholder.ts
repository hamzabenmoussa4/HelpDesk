import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/** Page temporaire affichée pour les modules qui seront codés ensuite. */
@Component({
  selector: 'app-placeholder',
  template: `<section class="placeholder">
    <p class="eyebrow">PROCHAINE ÉTAPE</p>
    <h1>{{ titre }}</h1>
    <p>{{ message }}</p>
  </section>`,
  styles: [
    `
      .placeholder {
        padding: 28px;
        border: 1px solid #e5eaf0;
        border-radius: 10px;
        background: #fff;
      }
      .eyebrow {
        color: #648276;
        font-size: 10px;
        font-weight: 800;
        letter-spacing: 1px;
      }
      h1 {
        margin: 8px 0;
        color: #17283e;
        font-size: 27px;
      }
      .placeholder p:last-child {
        color: #728096;
        font-size: 14px;
      }
    `,
  ],
})
export class Placeholder {
  private readonly route = inject(ActivatedRoute);
  protected readonly titre = this.route.snapshot.data['title'] as string;
  protected readonly message = this.route.snapshot.data['message'] as string;
}
