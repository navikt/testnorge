export type AlderspensjonTypes = {
	kravFremsattDato: string
	iverksettelsesdato: string
	saksbehandler: string
	attesterer: string
	uttaksgrad: number
	navEnhetId: string
	relasjoner?: Array<{
		sumAvForvArbKapPenInntekt: string
	}>
	soknad: boolean
}
