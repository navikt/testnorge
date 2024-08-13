export const initialUtbetalingsperiode = {
	startAlderAar: 62,
	startAlderMaaned: 1,
	sluttAlderAar: 72,
	sluttAlderMaaned: 12,
	aarligUtbetaling: 30000,
}

export const initialPensjonsavtale = [
	{
		produktBetegnelse: 'Test av pensjonsavtale',
		avtaleKategori: 'PRIVAT_TJENESTEPENSJON',
		utbetalingsperioder: [initialUtbetalingsperiode],
	},
]
