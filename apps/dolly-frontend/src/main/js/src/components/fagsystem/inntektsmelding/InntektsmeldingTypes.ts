export enum Kodeverk {
	AarsakTilInnsending = 'AARSAK_TIL_INNSENDING_TYPE',
	AarsakTilUtsettelse = 'AARSAK_TIL_UTSETTELSE_TYPE',
	AarsakVedEndring = 'AARSAK_VED_ENDRING_TYPE',
	Begrunnelse = 'BEGRUNNELSE_TYPE',
	NaturalYtelse = 'NATURALYTELSE_TYPE',
	Ytelse = 'YTELSE_TYPE'
}

export enum Ytelser {
	Sykepenger = 'SYKEPENGER',
	Foreldrepenger = 'FORELDREPENGER',
	Svangerskapspenger = 'SVANGERSKAPSPENGER',
	Pleiepenger = 'PLEIEPENGER',
	PleiepengerBarn = 'PLEIEPENGER_BARN',
	PleiepengerNaerstaaende = 'PLEIEPENGER_NAERSTAAENDE',
	Omsorgspenger = 'OMSORGSPENGER',
	Opplaeringspenger = 'OPPLAERINGSPENGER'
}

export enum Tema {
	Syk = 'SYK',
	Oms = 'OMS',
	For = 'FOR'
}

export type Inntektsmelding = {
	joarkMetadata: { tema: string }
	inntekter: Array<Inntekt>
	id?: number
}

export type Inntekt = {
	aarsakTilInnsending: string
	naerRelasjon: boolean
	startdatoForeldrepengeperiode: string
	ytelse: string //enum?
	arbeidsforhold: Arbeidsforhold
	arbeidsgiver?: Arbeidsgiver
	arbeidsgiverPrivat?: ArbeidsgiverPrivat
	avsendersystem?: Avsendersystem
	gjenopptakelseNaturalytelseListe?: Array<Naturalytelse>
	opphoerAvNaturalytelseListe?: Array<Naturalytelse>
	omsorgspenger?: Omsorgspenger
	pleiepengerPerioder?: Array<Pleiepenger>
	refusjon?: Refusjon
	sykepengerIArbeidsgiverperioden?: Sykepenger
}

export type Arbeidsforhold = {
	arbeidsforholdId: string
	beregnetInntekt: BeregnetInntekt
	avtaltFerieListe?: Array<AvtaltFerie>
	foersteFravaersdag?: string
}

export type BeregnetInntekt = {
	beloep: number
	aarsakVedEndring: string //enum?
}

export type AvtaltFerie = {
	fom: string
	tom: string
}

export type Arbeidsgiver = {
	kontaktinformasjon: { kontaktinformasjonNavn: string; telefonnummer: string }
	virksomhetsnummer: string
	orgnummer?: string
}

export type ArbeidsgiverPrivat = {
	arbeidsgiverFnr: string
	kontaktinformasjon: { kontaktinformasjonNavn: string; telefonnummer: string }
}

export type Avsendersystem = {
	innsendingstidspunkt: string
	systemnavn: string
	systemversjon: string
}
export type Naturalytelse = { beloepPrMnd?: number; fom?: string; naturalytelseType?: string } //enum
export type Omsorgspenger = {
	delvisFravaersListe?: Array<DelvisFravaer>
	fravaersPerioder?: Array<Fravaer>
	harUtbetaltPliktigeDager?: boolean
}

export type DelvisFravaer = {
	dato?: string
	timer?: number
}

export type Fravaer = {
	fom?: string
	tom?: string
}
export type Pleiepenger = { fom: string; tom: string }

export type Refusjon = {
	refusjonsbeloepPrMnd?: number
	refusjonsopphoersdato?: string
	endringIRefusjonListe?: Array<EndringIRefusjon>
}
export type EndringIRefusjon = {
	refusjonsbeloepPrMnd?: number
	endringsdato?: string
}
export type Sykepenger = {
	arbeidsgiverperiodeListe?: Array<Periode>
	begrunnelseForReduksjonEllerIkkeUtbetalt?: string //enum
	bruttoUtbetalt?: number
}

export type Periode = {
	fom?: string
	tom?: string
}

export type EnkelInntektsmelding = {
	bestilling: BestillingData
	data: Journalpost[]
}

export type Dokument = {
	journalpostId: number
	dokumentInfoId: number
	dokument: string
}

export type TransaksjonId = {
	miljoe: string
	transaksjonId: {
		journalpostId: number
		dokumentInfoId: number
	}
	bestillingId: string
}

export type Journalpost = {
	bestillingId?: number
	miljoe?: string
	dokumenter: Dokument[]
}

export type BestillingData = {
	data: {
		inntektsmelding?: {
			inntekter: Array<Inntekt>
		}
	}
	erGjenopprettet: boolean
	id: number
}

export type Bestilling = {
	inntektsmelding?: Array<Inntektsmelding>
}
