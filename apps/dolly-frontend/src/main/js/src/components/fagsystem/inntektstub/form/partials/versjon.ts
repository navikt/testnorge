import { FormikProps } from 'formik'
import _isNil from 'lodash/isNil'
// @ts-ignore
import { Inntektsinformasjon } from './inntektinformasjonTypes'

type Versjonsoversikt = {
	formikIdx?: number
	underversjonerIdx?: Array<number>
}

type SpesifikkVersjon = {
	versjonAv: number
	gjeldendeIdx: number
	underversjonerIdx: Array<number>
	gjeldendeInntektMedHistorikk?: boolean
}

export default function versjonsinformasjon(
	formikBag: FormikProps<{}>,
	inntektstubPath: string,
	inntektValues: Array<Inntektsinformasjon>,
	idx: number
): SpesifikkVersjon {
	// Skaffer oversikt over hvilke av inntektene i formikBag som er gjeldende og historikk
	const versjonsliste: Array<Versjonsoversikt> = mapVersjonsliste(
		formikBag,
		inntektstubPath,
		inntektValues
	)

	//Samler spesifikk versjoninformasjon for den inntekten (idx) som skal vise
	const spesifikkVersjonsinfo: SpesifikkVersjon = versjonsliste.reduce(
		(acc: SpesifikkVersjon, curr: Versjonsoversikt, kdx: number) => {
			if (curr.formikIdx === idx)
				return { ...acc, underversjonerIdx: curr.underversjonerIdx, gjeldendeIdx: kdx }
			else if (curr.underversjonerIdx.some((versjon) => versjon === idx))
				return { ...acc, versjonAv: kdx, underVersjonerIdx: curr.underversjonerIdx }
			else return acc
		},
		{ versjonAv: -1, underversjonerIdx: [], gjeldendeIdx: -1 }
	)

	const gjeldendeInntektMedHistorikk =
		spesifikkVersjonsinfo.versjonAv < 0 && spesifikkVersjonsinfo.underversjonerIdx.length > 0

	return { ...spesifikkVersjonsinfo, gjeldendeInntektMedHistorikk }
}

const mapVersjonsliste = (
	formikBag: FormikProps<{}>,
	inntektstubPath: string,
	inntektValues: Array<Inntektsinformasjon>
): Array<Versjonsoversikt> => {
	const versjonsoversikt: Array<Versjonsoversikt> = []
	inntektValues.forEach((inntektinfo: Inntektsinformasjon, idx: number) => {
		if (_isNil(inntektinfo.versjon))
			versjonsoversikt.push({ formikIdx: idx, underversjonerIdx: [] })
		else {
			versjonsoversikt.forEach((inntekt) => {
				if (sjekkKombinasjon(inntektValues[inntekt.formikIdx], inntektinfo)) {
					inntekt.underversjonerIdx.push(idx)
					if (inntektinfo.versjon !== inntekt.underversjonerIdx.length)
						//Endrer versjonsnr ved sletting av versjonsnr foran
						formikBag.setFieldValue(
							`${inntektstubPath}[${idx}].versjon`,
							inntekt.underversjonerIdx.length
						)
				}
			})
		}
	})

	return versjonsoversikt
}

const sjekkKombinasjon = (
	startversjon: Inntektsinformasjon,
	testinntekt: Inntektsinformasjon
): boolean =>
	// De tre verdiene som må være like mellom gjeldende inntekt og historikk
	startversjon.antallMaaneder === testinntekt.antallMaaneder &&
	startversjon.sisteAarMaaned === testinntekt.sisteAarMaaned &&
	startversjon.virksomhet === testinntekt.virksomhet
