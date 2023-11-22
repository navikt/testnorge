import * as _ from 'lodash'
import { isAfter, isBefore, isEqual } from 'date-fns'
import { Relasjon } from '@/components/fagsystem/pdlf/PdlTypes'

export const testDatoFom = (val, tomPath, feilmelding = 'Dato må være før til-dato') => {
	return val.test('is-before-tom', feilmelding, function isBeforeTom(value) {
		const datoTom = _.get(this, `parent.${tomPath}`)
		if (!value || !datoTom) return true
		if (isEqual(new Date(value), new Date(datoTom))) return true
		return isBefore(new Date(value), new Date(datoTom))
	})
}

export const testDatoTom = (val, fomPath, feilmelding = 'Dato må være etter fra-dato') => {
	return val.test('is-after-fom', feilmelding, function isAfterFom(value) {
		const datoFom = _.get(this, `parent.${fomPath}`)
		if (!value || !datoFom) return true
		if (isEqual(new Date(value), new Date(datoFom))) return true
		return isAfter(new Date(value), new Date(datoFom))
	})
}

export const filtrerKeysMedKunNullVerdier = (data) => {
	if (!data) {
		return null
	}
	JSON.stringify(
		data,
		(_key, value) => {
			return value === null || value === '' ? undefined : value
		},
		4,
	)
}

export const getEksisterendeNyPerson = (
	relasjoner: Array<Relasjon>,
	ident: String,
	relasjonTyper: Array<String>,
) => {
	const relasjon = relasjoner?.find(
		(relasjon) =>
			relasjon?.relatertPerson?.ident === ident && relasjonTyper.includes(relasjon.relasjonType),
	)

	if (!relasjon) {
		return null
	}

	return {
		value: relasjon?.relatertPerson?.ident,
		label: `${relasjon?.relatertPerson?.ident} - ${relasjon?.relatertPerson?.navn?.[0]?.fornavn} ${relasjon?.relatertPerson?.navn?.[0]?.etternavn}`,
	}
}

export const getRandomValue = (liste: Array<any>) => {
	if (!liste || liste?.length < 1) {
		return null
	}
	const random = Math.floor(Math.random() * liste.length) //NOSONAR not used in secure contexts
	return liste[random]
}
