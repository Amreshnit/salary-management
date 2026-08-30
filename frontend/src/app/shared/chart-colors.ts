const CATEGORY_PALETTE = [
  '#6366f1',
  '#ec4899',
  '#14b8a6',
  '#f59e0b',
  '#8b5cf6',
  '#06b6d4',
  '#ef4444',
  '#22c55e',
  '#f97316',
  '#3b82f6',
  '#a855f7',
  '#eab308',
];

const BAND_GRADIENT = ['#c7d2fe', '#a5b4fc', '#818cf8', '#6366f1', '#4338ca'];

export function colorForCategory(categoryName: string): string {
  let hash = 0;
  for (let index = 0; index < categoryName.length; index++) {
    hash = (hash << 5) - hash + categoryName.charCodeAt(index);
    hash |= 0;
  }
  const paletteIndex = Math.abs(hash) % CATEGORY_PALETTE.length;
  return CATEGORY_PALETTE[paletteIndex];
}

export function colorForBand(band: number, totalBands: number): string {
  const index = Math.min(band - 1, BAND_GRADIENT.length - 1);
  return totalBands <= BAND_GRADIENT.length ? BAND_GRADIENT[index] : BAND_GRADIENT[BAND_GRADIENT.length - 1];
}
