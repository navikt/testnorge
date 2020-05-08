export type Versjonering = {
	underversjoner: Array<Number>
	path: string
	harAvhengigheter: boolean
}

export type Inntektsinformasjon = {
	sisteAarMaaned: string
	antallMaaneder: string
	virksomhet: string
	opplysningspliktig: string
	versjon?: number | null
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
