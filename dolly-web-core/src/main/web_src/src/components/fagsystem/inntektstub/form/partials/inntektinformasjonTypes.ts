export type VersjonInfo = {
	underversjoner: Array<number>
	path: string
	gjeldendeInntektMedHistorikk: boolean
}

export type Inntektsinformasjon = {
	sisteAarMaaned: string
	antallMaaneder: string
	virksomhet: string
	opplysningspliktig: string
	versjon?: number
	inntektsliste: Array<Inntekt>
	fradragsliste: Array<Fradrag>
	forskuddstrekksliste: Array<Forskudd>
	arbeidsforholdsliste: Array<Arbeidsforhold>
}

type Inntekt = {
	beloep: string
	startOpptjeningsperiode: string
	sluttOpptjeningsperiode: string
	inntektstype: string
}

type Fradrag = {}
type Forskudd = {}
type Arbeidsforhold = {}
