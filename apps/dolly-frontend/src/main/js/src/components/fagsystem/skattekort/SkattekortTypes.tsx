export type Forskuddstrekk = {
	trekktabell?: {
		trekkode?: string
		tabellnummer?: string
		prosentsats?: number
		antallMaanederForTrekk?: number
	}
	trekkprosent?: {
		trekkode?: string
		prosentsats?: number
		antallMaanederForTrekk?: number
	}
	frikort?: {
		trekkode?: string
		frikortbeloep?: number
	}
}

type Arbeidstaker = {
	arbeidstakeridentifikator?: string
	resultatPaaForespoersel: string
	skattekort: {
		utstedtDato: string
		skattekortidentifikator: number
		forskuddstrekk: Array<Forskuddstrekk>
	}
	tilleggsopplysning: Array<string>
	inntektsaar: number
}

export type ArbeidsgiverSkatt = {
	arbeidsgiveridentifikator: {
		organisasjonsnummer?: string
		personidentifikator?: string
	}
	arbeidstaker: Array<Arbeidstaker>
}

export type SkattekortTypes = {
	skattekort: {
		arbeidsgiverSkatt: Array<ArbeidsgiverSkatt>
	}
}
