export type AlderspensjonTypes = {
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
	inkluderAfpPrivat?: boolean
	afpPrivatResultat?: string
	soknad: boolean
}
