export type UforetrygdTypes = {
	uforetidspunkt: string
	kravFremsattDato: string
	onsketVirkningsDato: string
	inntektForUforhet: number
	inntektEtterUforhet: number
	uforegrad: number
	minimumInntektForUforhetType: string
	saksbehandler: string
	attesterer: string
	navEnhetId: string
	barnetilleggDetaljer: BarnetilleggDetaljer
}

export type BarnetilleggDetaljer = {
	barnetilleggType: string
	forventedeInntekterSoker: Array<ForventedeInntekterSokerOgEP>
	forventedeInntekterEP: Array<ForventedeInntekterSokerOgEP>
}

export type ForventedeInntekterSokerOgEP = {
	datoFom: string
	datoTom: string
	inntektType: string
	belop: number
}
