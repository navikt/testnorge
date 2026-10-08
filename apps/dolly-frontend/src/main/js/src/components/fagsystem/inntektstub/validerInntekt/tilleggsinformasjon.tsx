import * as _ from 'lodash-es'
import {
	tilleggsinformasjonAttributter,
	tilleggsinformasjonPaths,
} from '@/components/fagsystem/inntektstub/validerInntekt/paths'

const flatFields = Object.keys(tilleggsinformasjonPaths)

const erTom = (verdi: unknown) => verdi === undefined || verdi === null || verdi === ''

const finnType = (tilleggsinformasjon: Record<string, unknown>) =>
	Object.keys(tilleggsinformasjonAttributter).find((type) =>
		_.has(tilleggsinformasjon, tilleggsinformasjonAttributter[type]),
	)

export const tilFlatInntekt = (inntekt: Record<string, any>) => {
	const tilleggsinformasjon = inntekt?.tilleggsinformasjon
	if (!_.isPlainObject(tilleggsinformasjon)) {
		return inntekt
	}
	const flatValues = Object.fromEntries(
		flatFields
			.map((felt) => [felt, _.get(inntekt, tilleggsinformasjonPaths[felt])])
			.filter(([, verdi]) => !erTom(verdi)),
	)
	return {
		..._.omit(inntekt, 'tilleggsinformasjon'),
		...flatValues,
		tilleggsinformasjonstype: inntekt.tilleggsinformasjonstype || finnType(tilleggsinformasjon),
	}
}

export const tilNestedInntekt = (inntekt: Record<string, any>) => {
	const attributt = tilleggsinformasjonAttributter[inntekt?.tilleggsinformasjonstype]
	const inntektUtenTilleggsinformasjon = _.omit(inntekt, [
		...flatFields,
		'tilleggsinformasjonstype',
	])
	if (!attributt) {
		return inntektUtenTilleggsinformasjon
	}
	const tilleggsinformasjon = { [attributt]: {} }
	flatFields
		.filter((felt) => tilleggsinformasjonPaths[felt].split('.')[1] === attributt)
		.filter((felt) => !erTom(inntekt[felt]))
		.forEach((felt) =>
			_.set(
				tilleggsinformasjon,
				tilleggsinformasjonPaths[felt].replace('tilleggsinformasjon.', ''),
				inntekt[felt],
			),
		)
	return { ...inntektUtenTilleggsinformasjon, tilleggsinformasjon }
}
