import * as _ from 'lodash-es'
import subYears from 'date-fns/subYears'

export const hentAaregEksisterendeData = (personFoerLeggTil: any) => {
	if (_.isEmpty(personFoerLeggTil?.aareg)) {
		return null
	}

	let stoersteAaregdata: any = []
	personFoerLeggTil?.aareg?.forEach((aareg: any) => {
		if (aareg.data?.length > stoersteAaregdata.length) {
			stoersteAaregdata = aareg?.data
		}
	})
	stoersteAaregdata.sort((a: any, b: any) => a.arbeidsforholdId.localeCompare(b.arbeidsforholdId))

	const aaregdataClean: any = []

	stoersteAaregdata.forEach((aareg: any) => {
		const arbeidsforhold = {
			arbeidsforholdstype: aareg.type || 'ordinaertArbeidsforhold',
			arbeidsgiver: {
				aktoertype: aareg.arbeidsgiver?.type === 'Person' ? 'PERS' : 'ORG',
				orgnummer: aareg.arbeidsgiver?.organisasjonsnummer || undefined,
				ident: aareg.arbeidsgiver?.offentligIdent || undefined,
			},
			arbeidsforholdId: aareg.arbeidsforholdId || '',
			ansettelsesPeriode: {
				fom: aareg.ansettelsesperiode?.periode?.fom || subYears(new Date(), 20),
				tom: aareg.ansettelsesperiode?.periode?.tom || null,
				sluttaarsak: aareg.ansettelsesperiode?.sluttaarsak || null,
			},
			arbeidsavtale: {
				yrke: aareg.arbeidsavtaler?.[0]?.yrke || '',
				ansettelsesform: aareg.arbeidsavtaler?.[0]?.ansettelsesform || 'fast',
				stillingsprosent: aareg.arbeidsavtaler?.[0]?.stillingsprosent || 100,
				endringsdatoStillingsprosent: aareg.arbeidsavtaler?.[0]?.sistStillingsendring || null,
				sisteLoennsendringsdato: aareg.arbeidsavtaler?.[0]?.sistLoennsendring || null,
				arbeidstidsordning: aareg.arbeidsavtaler?.[0]?.arbeidstidsordning || 'ikkeSkift',
				avtaltArbeidstimerPerUke: aareg.arbeidsavtaler?.[0]?.antallTimerPrUke || 37.5,
			},
			antallTimerForTimeloennet:
				aareg.antallTimerForTimeloennet?.map((timeloennet: any) => {
					return {
						periode: {
							fom: timeloennet.periode?.fom || null,
							tom: timeloennet.periode?.tom || null,
						},
						antallTimer: timeloennet.antallTimer || 0,
					}
				}) || [],
			utenlandsopphold:
				aareg.utenlandsopphold?.map((opphold: any) => {
					return {
						periode: {
							fom: opphold.periode?.fom || null,
							tom: opphold.periode?.tom || null,
						},
						land: opphold.landkode || '',
					}
				}) || [],
			permisjon:
				aareg.permisjonPermitteringer
					?.filter((item: any) => item.type !== 'permittering')
					?.map((permisjon: any) => {
						return {
							permisjonsPeriode: {
								fom: permisjon.periode?.fom || null,
								tom: permisjon.periode?.tom || null,
							},
							permisjonsprosent: permisjon.prosent || 100,
							permisjon: permisjon.type || '',
						}
					}) || [],
			permittering:
				aareg.permisjonPermitteringer
					?.filter((item: any) => item.type === 'permittering')
					?.map((permittering: any) => {
						return {
							permitteringsPeriode: {
								fom: permittering.periode?.fom || null,
								tom: permittering.periode?.tom || null,
							},
							permitteringsprosent: permittering.prosent || 100,
						}
					}) || [],
			fartoy:
				aareg.type === 'maritimtArbeidsforhold'
					? [
							{
								skipsregister: aareg.arbeidsavtaler?.[0]?.skipsregister || '',
								skipstype: aareg.arbeidsavtaler?.[0]?.skipstype || '',
								fartsomraade: aareg.arbeidsavtaler?.[0]?.fartsomraade || '',
							},
						]
					: undefined,
			isOppdatering: true,
		}
		aaregdataClean.push(arbeidsforhold)
	})
	return aaregdataClean
}
