import * as _ from 'lodash-es'
import { tilNestedInntekt } from '@/components/fagsystem/inntektstub/validerInntekt/tilleggsinformasjon'

export const GYLDIGE_VERDIER = 'gyldigeVerdier'

export type FeltOptions = Array<string | boolean>

export type GyldigeVerdier = Record<string, FeltOptions>

export const optionsUtfylt = (options: FeltOptions) =>
	(options.length === 2 && options.includes('<TOM>') && options.includes('<UTFYLT>')) ||
	(options.length === 1 && options[0] === '<UTFYLT>')

export const hentEnesteValg = (options: FeltOptions) => {
	if (options.length === 1 && options[0] !== '<TOM>' && options[0] !== '<UTFYLT>') {
		return options[0]
	}
	return undefined
}

const erTom = (verdi: unknown) => verdi === undefined || verdi === null || verdi === ''

const erFeltUgyldig = (options: FeltOptions, verdi: unknown) => {
	if (options.includes('<TOM>') || hentEnesteValg(options) !== undefined) {
		return false
	}
	if (erTom(verdi)) {
		return true
	}
	return !optionsUtfylt(options) && !options.includes(verdi as string | boolean)
}

export const finnUgyldigeFelter = (inntekt: Record<string, any>) => {
	const gyldigeVerdier: GyldigeVerdier | undefined = inntekt?.[GYLDIGE_VERDIER]
	if (!gyldigeVerdier) {
		return []
	}
	return Object.entries(gyldigeVerdier)
		.filter(([felt, options]) => erFeltUgyldig(options, inntekt[felt]))
		.map(([felt]) => felt)
}

export const klargjoerInntektstubForBestilling = (inntektstub: any) => ({
	...inntektstub,
	inntektsinformasjon: inntektstub.inntektsinformasjon.map((inntektsinformasjon: any) =>
		inntektsinformasjon?.inntektsliste
			? {
					...inntektsinformasjon,
					inntektsliste: inntektsinformasjon.inntektsliste.map((inntekt: any) =>
						tilNestedInntekt(_.omit(inntekt, GYLDIGE_VERDIER)),
					),
				}
			: inntektsinformasjon,
	),
})
