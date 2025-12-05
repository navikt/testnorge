export type AlderspensjonTypes = {
	kravFremsattDato?: string
	iverksettelsesdato?: string
	fom?: string
	saksbehandler: string
	attesterer: string
	uttaksgrad?: number
	nyUttaksgrad?: number
	navEnhetId: string
	relasjoner?: Array<{
		sumAvForvArbKapPenInntekt: string
	}>
	soknad: boolean
}
