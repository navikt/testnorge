export const oppfoelgingsvedtak14aPath = 'oppfoelgingsvedtak14a'

export const initialOppfoelgingsvedtak14a = {
	innsatsgruppe: 'GODE_MULIGHETER',
	hovedmal: 'SKAFFE_ARBEID',
	vedtakFattet: new Date(),
	oppfolgingsEnhet: null,
	begrunnelse: null,
	veilederIdent: null,
}

export type Oppfoelgingsvedtak14aTypes = {
	innsatsgruppe: string
	hovedmal: string
	vedtakFattet?: Date
	oppfolgingsEnhet?: string
	begrunnelse?: string
	veilederIdent?: string
}
