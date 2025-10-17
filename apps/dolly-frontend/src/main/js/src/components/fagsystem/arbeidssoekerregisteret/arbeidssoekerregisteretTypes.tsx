type JobbsituasjonTypes = {
	gjelderFraDato: Date
	gjelderTilDato: Date
	stillingStyrk08: string
	stillingstittel: string
	stillingsprosent: string
	sisteDagMedLoenn: Date
	sisteArbeidsdag: Date
}

export type ArbeidssoekerregisteretTypes = {
	utfoertAv: string
	kilde: string
	aarsak: string
	nuskode: string
	utdanningBestaatt: boolean
	utdanningGodkjent: boolean
	jobbsituasjonsbeskrivelse: string
	jobbsituasjonsdetaljer: JobbsituasjonTypes
	helsetilstandHindrerArbeid: boolean
	andreForholdHindrerArbeid: boolean
	registreringstidspunkt: Date
}
