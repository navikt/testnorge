export const initialPensjonsavtale = {
	produktBetegnelse: 'Test av pensjonsavtale',
	avtaleKategori: 'FOLKETRYGD',
	startAlderAar: 22,
	sluttAlderAar: 62,
	utbetalingsperioder: [
		{
			startAlderAar: 62,
			startAlderMaaneder: 12,
			sluttAlderAar: 72,
			sluttAlderMaaneder: 12,
			aarligUtbetaling: 200000,
			grad: 100,
		},
	],
}

export const initialUtbetalingsperiodeVerdier = {
	startAlderAar: 62,
	startAlderMaaneder: 12,
	sluttAlderAar: 72,
	sluttAlderMaaneder: 12,
	aarligUtbetaling: 200000,
	grad: 100,
}

export const initialUtbetalingsperiode = {
	startAlderAar: undefined,
	startAlderMaaneder: undefined,
	sluttAlderAar: undefined,
	sluttAlderMaaneder: undefined,
	aarligUtbetaling: undefined,
	grad: undefined,
}
