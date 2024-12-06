import _ from 'lodash'
import { isAfter, isBefore, isEqual } from 'date-fns'
import { Relasjon } from '@/components/fagsystem/pdlf/PdlTypes'

export const testDatoFom = (val, tomPath, feilmelding = 'Dato må være før til-dato') => {
	return val.test('is-before-tom', feilmelding, (value, testContext) => {
		const datoTom = _.get(testContext.parent, tomPath)
		if (!value || !datoTom) return true
		if (isEqual(value, datoTom)) return true
		return isBefore(value, datoTom)
	})
}

export const testDatoTom = (val, fomPath, feilmelding = 'Dato må være etter fra-dato') => {
	return val.test('is-after-fom', feilmelding, (value, testContext) => {
		const datoFom = _.get(testContext.parent, fomPath)
		if (!value || !datoFom) return true
		if (isEqual(value, datoFom)) return true
		return isAfter(value, datoFom)
	})
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
