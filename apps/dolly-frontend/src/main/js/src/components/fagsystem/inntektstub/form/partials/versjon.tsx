import * as _ from 'lodash-es'
// @ts-ignore
import { Inntektsinformasjon } from './inntektinformasjonTypes'
import { UseFormReturn } from 'react-hook-form/dist/types'

type Versjonsoversikt = {
	formIdx?: number
	underversjonerIdx?: Array<number>
}

type SpesifikkVersjon = {
	versjonAv: number
	gjeldendeIdx: number
	underversjonerIdx: Array<number>
	gjeldendeInntektMedHistorikk?: boolean
}

export default function versjonsinformasjon(
	formMethods: UseFormReturn,
	inntektstubPath: string,
	inntektValues: Array<Inntektsinformasjon>,
	idx: number,
): SpesifikkVersjon {
	// Skaffer oversikt over hvilke av inntektene i formet som er gjeldende og historikk
	const versjonsliste: Array<Versjonsoversikt> = mapVersjonsliste(
		formMethods,
		inntektstubPath,
		inntektValues,
	)

	//Samler spesifikk versjoninformasjon for den inntekten (idx) som skal vise
	const spesifikkVersjonsinfo: SpesifikkVersjon = versjonsliste.reduce(
		(acc: SpesifikkVersjon, curr: Versjonsoversikt, kdx: number) => {
			if (curr.formIdx === idx) {
				return { ...acc, underversjonerIdx: curr.underversjonerIdx, gjeldendeIdx: kdx }
			} else if (curr.underversjonerIdx.some((versjon) => versjon === idx)) {
				return { ...acc, versjonAv: kdx, underVersjonerIdx: curr.underversjonerIdx }
			} else {
				return acc
			}
		},
		{ versjonAv: -1, underversjonerIdx: [], gjeldendeIdx: -1 },
	)

	const gjeldendeInntektMedHistorikk =
		spesifikkVersjonsinfo.versjonAv < 0 && spesifikkVersjonsinfo.underversjonerIdx.length > 0

	return { ...spesifikkVersjonsinfo, gjeldendeInntektMedHistorikk }
}

const mapVersjonsliste = (
	formMethods: UseFormReturn,
	inntektstubPath: string,
	inntektValues: Array<Inntektsinformasjon>,
): Array<Versjonsoversikt> => {
	const versjonsoversikt: Array<Versjonsoversikt> = []
	inntektValues.forEach((inntektinfo: Inntektsinformasjon, idx: number) => {
		if (_.isNil(inntektinfo.versjon)) {
			versjonsoversikt.push({ formIdx: idx, underversjonerIdx: [] })
		} else {
			versjonsoversikt.forEach((inntekt) => {
				if (sjekkKombinasjon(inntektValues[inntekt.formIdx], inntektinfo)) {
					inntekt.underversjonerIdx.push(idx)
					if (inntektinfo.versjon !== inntekt.underversjonerIdx.length) {
						//Endrer versjonsnr ved sletting av versjonsnr foran
						formMethods.setValue(
							`${inntektstubPath}[${idx}].versjon`,
							inntekt.underversjonerIdx.length,
						)
					}
				}
			})
		}
	})

	return versjonsoversikt
}

const sjekkKombinasjon = (
	startversjon: Inntektsinformasjon,
	testinntekt: Inntektsinformasjon,
): boolean =>
	// De tre verdiene som må være like mellom gjeldende inntekt og historikk
	startversjon.antallMaaneder === testinntekt.antallMaaneder &&
	startversjon.sisteAarMaaned === testinntekt.sisteAarMaaned &&
	startversjon.virksomhet === testinntekt.virksomhet
