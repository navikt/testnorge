import * as _ from 'lodash-es'
import { isAfter, isBefore, isEqual } from 'date-fns'
import { Relasjon } from '@/components/fagsystem/pdlf/PdlTypes'

export const testDatoFom = (val, tomPath, feilmelding) => {
	return val.test(
		'is-before-tom',
		feilmelding || 'Dato må være før til-dato',
		function isBeforeTom(value) {
			const datoTom = _.get(this, `parent.${tomPath}`)
			if (!value || !datoTom) return true
			if (isEqual(new Date(value), new Date(datoTom))) return true
			return isBefore(new Date(value), new Date(datoTom))
		}
	)
}

export const testDatoTom = (val, fomPath, feilmelding) => {
	return val.test(
		'is-after-fom',
		feilmelding || 'Dato må være etter fra-dato',
		function isAfterFom(value) {
			const datoFom = _.get(this, `parent.${fomPath}`)
			if (!value || !datoFom) return true
			if (isEqual(new Date(value), new Date(datoFom))) return true
			return isAfter(new Date(value), new Date(datoFom))
		}
	)
}

export const getEksisterendeNyPerson = (
	relasjoner: Array<Relasjon>,
	ident: String,
	relasjonType: String
) => {
	const relasjon = relasjoner.find(
		(relasjon) =>
			relasjon?.relatertPerson?.ident === ident && relasjon?.relasjonType === relasjonType
	)
	return {
		value: relasjon?.relatertPerson?.ident,
		label: `${relasjon?.relatertPerson?.ident} - ${relasjon?.relatertPerson?.navn?.[0]?.fornavn} ${relasjon?.relatertPerson?.navn?.[0]?.etternavn}`,
	}
}
