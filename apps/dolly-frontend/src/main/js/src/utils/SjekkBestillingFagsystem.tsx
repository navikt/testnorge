type Fagsystem = {
	aareg?: boolean
	skattekort?: boolean
	medl?: boolean
	udistub?: boolean
	sigrunstubPensjonsgivende?: boolean
	sigrunstubSummertSkattegrunnlag?: boolean
	pensjonforvalter?: {
		tp?: Array<{
			ordning?: string
			ytelser?: Array<any>
		}>
		inntekt?: any
		generertInntekt?: any
		alderspensjon?: boolean
		pensjonsavtale?: boolean
		uforetrygd?: boolean
		afpOffentlig?: boolean
	}
	instdata?: boolean
	dokarkiv?: boolean
	histark?: boolean
	arbeidssoekerregisteret?: boolean
	arbeidsplassenCV?: boolean
	arenaforvalter?: boolean
	sykemelding?: boolean
	yrkesskader?: boolean
	inntektsmelding?: boolean
	inntektstub?: boolean
}

const harFagsystem = (
	bestillingerFagsystemer: Fagsystem[],
	validationFn: (item: Fagsystem) => boolean,
): boolean => {
	let exists = false
	bestillingerFagsystemer?.forEach((item) => {
		if (validationFn(item)) {
			exists = true
		}
	})
	return exists
}

export const harAaregBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.aareg)

export const harSkattekortBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.skattekort)

export const harMedlBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.medl)

export const harUdistubBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.udistub)

export const harSigrunstubPensjonsgivendeInntekt = (
	bestillingerFagsystemer: Fagsystem[],
): boolean => harFagsystem(bestillingerFagsystemer, (i) => !!i?.sigrunstubPensjonsgivende)

export const harSigrunstubSummertSkattegrunnlag = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.sigrunstubSummertSkattegrunnlag)

export const harTpBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => (i?.pensjonforvalter?.tp?.length || 0) > 0)

export const harPoppBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(
		bestillingerFagsystemer,
		(i) => !!i?.pensjonforvalter?.inntekt || !!i?.pensjonforvalter?.generertInntekt,
	)

export const harApBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.pensjonforvalter?.alderspensjon)

export const harPensjonavtaleBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.pensjonforvalter?.pensjonsavtale)

export const harUforetrygdBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.pensjonforvalter?.uforetrygd)

export const harAfpOffentligBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.pensjonforvalter?.afpOffentlig)

export const harInstBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.instdata)

export const harDokarkivBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.dokarkiv)

export const harHistarkBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.histark)

export const harArbeidssoekerregisteretBestilling = (
	bestillingerFagsystemer: Fagsystem[],
): boolean => harFagsystem(bestillingerFagsystemer, (i) => !!i?.arbeidssoekerregisteret)

export const harArbeidsplassenBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.arbeidsplassenCV)

export const harArenaBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.arenaforvalter)

export const harSykemeldingBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.sykemelding)

export const harYrkesskaderBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.yrkesskader)

export const harInntektsmeldingBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.inntektsmelding)

export const harInntektstubBestilling = (bestillingerFagsystemer: Fagsystem[]): boolean =>
	harFagsystem(bestillingerFagsystemer, (i) => !!i?.inntektstub)
