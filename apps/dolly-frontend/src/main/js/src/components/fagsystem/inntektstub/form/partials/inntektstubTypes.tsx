export type Inntektsinformasjon = {
	perioder: Array<string>
	rapporteringsdato?: Date | null
	virksomhet: string
	opplysningspliktig: string
	inntektsliste: Array<Inntekt>
	fradragsliste: Array<Fradrag>
	forskuddstrekksliste: Array<Forskudd>
}

export type Inntekt = {
	beloep: string
	startOpptjeningsperiode?: string
	sluttOpptjeningsperiode?: string
	inntektstype: string
	tilleggsinformasjonstype?: string
}

export type Fradrag = {}
export type Forskudd = {}
