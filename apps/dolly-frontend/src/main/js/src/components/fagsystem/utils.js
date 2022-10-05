import _get from 'lodash/get'
import { isAfter, isBefore, isEqual } from 'date-fns'

export const testDatoFom = (val, tomPath, feilmelding) => {
	return val.test(
		'is-before-tom',
		feilmelding || 'Dato må være før til-dato',
		function isBeforeTom(value) {
			const datoTom = _get(this, `parent.${tomPath}`)
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
			const datoFom = _get(this, `parent.${fomPath}`)
			if (!value || !datoFom) return true
			if (isEqual(new Date(value), new Date(datoFom))) return true
			return isAfter(new Date(value), new Date(datoFom))
		}
	)
}
