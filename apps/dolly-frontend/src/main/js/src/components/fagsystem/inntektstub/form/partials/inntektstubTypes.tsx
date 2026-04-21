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
	rapporteringsdato: Date
	versjon?: number
	inntektsliste: Array<Inntekt>
	fradragsliste: Array<Fradrag>
	forskuddstrekksliste: Array<Forskudd>
}

export type Inntekt = {
	beloep: string
	startOpptjeningsperiode: string
	sluttOpptjeningsperiode: string
	inntektstype: string
	tilleggsinformasjonstype?: string
}

export type Fradrag = {}
export type Forskudd = {}
