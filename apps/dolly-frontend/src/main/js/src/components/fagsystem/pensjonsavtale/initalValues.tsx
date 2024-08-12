export const initialPensjonsavtale = [
	{
		produktBetegnelse: 'Test av pensjonsavtale',
		avtaleKategori: 'PRIVAT_TJENESTEPENSJON',
		utbetalingsperioder: [
			{
				startAlderAar: 67,
				startAlderMaaned: 1,
				sluttAlderAar: undefined,
				sluttAlderMaaned: undefined,
				aarligUtbetaling: 30000,
			},
		],
	},
]

export const initialUtbetalingsperiode = {
	startAlderAar: 62,
	startAlderMaaned: 1,
	sluttAlderAar: 72,
	sluttAlderMaaned: 12,
	aarligUtbetaling: 10000,
}
