export type Utbetalingsperiode = {
	startAlderAar: number
	startAlderMaaned: number
	sluttAlderAar: number
	sluttAlderMaaned: number
	aarligUtbetaling: number
}

export type PensjonsavtaleTypes = {
	produktBetegnelse: string
	avtaleKategori: string
	utbetalingsperioder: Array<Utbetalingsperiode>
}
