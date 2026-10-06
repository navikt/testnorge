import * as _ from 'lodash-es'
import tilleggsinformasjonPaths from '@/components/fagsystem/inntektstub/validerInntekt/paths'

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
		.map(([felt, options]) => ({ felt, feltPath: tilleggsinformasjonPaths(felt), options }))
		.filter(({ feltPath, options }) => erFeltUgyldig(options, _.get(inntekt, feltPath)))
		.map(({ feltPath }) => feltPath)
}

export const fjernGyldigeVerdier = (inntektstub: any) => ({
	...inntektstub,
	inntektsinformasjon: inntektstub.inntektsinformasjon.map((inntektsinformasjon: any) =>
		inntektsinformasjon?.inntektsliste
			? {
					...inntektsinformasjon,
					inntektsliste: inntektsinformasjon.inntektsliste.map((inntekt: any) =>
						_.omit(inntekt, GYLDIGE_VERDIER),
					),
				}
			: inntektsinformasjon,
	),
})
