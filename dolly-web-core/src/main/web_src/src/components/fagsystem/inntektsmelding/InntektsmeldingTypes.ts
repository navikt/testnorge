//TODO Ikke tatt i bruk enda. Denne kan brukes av både form og visning

type Inntekter = {
	aarsakTilInnsending: string
	naerRelasjon: boolean
	startdatoForeldrepengeperiode: string
	ytelse: string //enum?
	arbeidsforhold: Arbeidsforhold
	arbeidsgiver: Arbeidsgiver
	avsendersystem?: Avsendersystem
	gjenopptakelseNaturalytelseListe?: Array<GjenopptakelseNaturalytelse>
	opphoerAvNaturalytelseListe?: Array<OpphoerAvNaturalytelse>
	omsorgspenger?: Omsorgspenger
	pleiepengerPerioder?: Array<Pleiepenger>
	refusjon?: Refusjon
	sykepengerIArbeidsgiverperioden?: SykepengerIArbeidsgiverperioden
}

type Arbeidsforhold = {
	beregnetInntekt: BeregnetInntekt
	avtaltFerieListe?: Array<AvtaltFerie>
	foersteFravaersdag?: string
}

type BeregnetInntekt = {
	beloep: number
	aarsakVedEndring: string //enum?
}

type AvtaltFerie = {
	fom: string
	tom: string
}

type Arbeidsgiver = {}

type Avsendersystem = {}
type GjenopptakelseNaturalytelse = {}
type OpphoerAvNaturalytelse = {}
type Omsorgspenger = {}
type Pleiepenger = {}
type Refusjon = {}
type SykepengerIArbeidsgiverperioden = {}
