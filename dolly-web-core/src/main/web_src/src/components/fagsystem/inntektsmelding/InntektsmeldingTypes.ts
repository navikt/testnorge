//TODO Ikke tatt i bruk enda. Denne kan brukes av både form og visning

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
	Omsorgspenger = 'OMSORGSPENGER',
	Opplaeringspenger = 'OPPLAERINGSPENGER'
}

export enum Tema {
	Syk = 'SYK',
	Oms = 'OMS',
	For = 'FOR'
}

export type Inntekter = {
	aarsakTilInnsending: string
	naerRelasjon: boolean
	startdatoForeldrepengeperiode: string
	ytelse: string //enum?
	arbeidsforhold: Arbeidsforhold
	arbeidsgiver: Arbeidsgiver
	avsendersystem?: Avsendersystem
	gjenopptakelseNaturalytelseListe?: Array<Naturalytelse>
	opphoerAvNaturalytelseListe?: Array<Naturalytelse>
	omsorgspenger?: Omsorgspenger
	pleiepengerPerioder?: Array<Pleiepenger>
	refusjon?: Refusjon
	sykepengerIArbeidsgiverperioden?: SykepengerIArbeidsgiverperioden
}

export type Arbeidsforhold = {
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

type DelvisFravaer = {
	dato?: string
	timer?: number
}

type Fravaer = {
	fom?: string
	tom?: string
}
export type Pleiepenger = { fom: string; tom: string }
export type Refusjon = {
	refusjonsbeloepPrMnd?: number
	refusjonsopphoersdato?: string
	endringIRefusjonListe?: Array<EndringIRefusjon>
}
type EndringIRefusjon = {
	refusjonsbeloepPrMnd?: number
	endringsdato?: string
}
export type SykepengerIArbeidsgiverperioden = {}
