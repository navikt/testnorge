export const initialPensjonsavtale = [
	{
		produktBetegnelse: 'Test av pensjonsavtale',
		avtaleKategori: 'FOLKETRYGD',
		utbetalingsperioder: [
			{
				startAlderAar: 67,
				startAlderMaaned: 1,
				sluttAlderAar: undefined,
				sluttAlderMaaned: undefined,
				aarligUtbetaling: 300000,
			},
		],
	},
]

export const initialUtbetalingsperiode = {
	startAlderAar: 62,
	startAlderMaaned: 1,
	sluttAlderAar: 72,
	sluttAlderMaaned: 12,
	aarligUtbetaling: 100000,
}
