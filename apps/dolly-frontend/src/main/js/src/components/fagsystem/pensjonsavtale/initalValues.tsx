export const initialPensjonsavtale = [
	{
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
	},
]

export const initialNyPensjonsavtaleVerdier = {
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

export const initialNyPensjonsavtale = {
	produktBetegnelse: '',
	avtaleKategori: '',
	startAlderAar: undefined,
	sluttAlderAar: undefined,
	utbetalingsperioder: [
		{
			startAlderAar: undefined,
			startAlderMaaneder: undefined,
			sluttAlderAar: undefined,
			sluttAlderMaaneder: undefined,
			aarligUtbetaling: undefined,
			grad: undefined,
		},
	],
}

export const initialUtbetalingsperiodeVerdier = {
	startAlderAar: 67,
	startAlderMaaneder: 1,
	sluttAlderAar: undefined,
	sluttAlderMaaneder: undefined,
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
