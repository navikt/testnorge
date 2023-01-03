import { isAfter, isBefore } from 'date-fns'

export const getLastDateBefore = (hoveddato: Date, datoer: Date[]) => {
	if (!datoer || datoer.length === 0) {
		return null
	}
	const mindreDatoer = datoer.filter((dato) => isBefore(dato, hoveddato))
	if (!mindreDatoer || mindreDatoer.length === 0) {
		return null
	}
	const minDate = new Date(Math.max.apply(null, mindreDatoer))
	if (minDate) {
		minDate.setDate(minDate.getDate() + 1)
	}
	return minDate
}

export const getFirstDateAfter = (hoveddato: Date, datoer: Date[]) => {
	if (!datoer || datoer.length === 0) {
		return null
	}
	const stoerreDatoer = datoer.filter((dato) => isAfter(dato, hoveddato))
	if (!stoerreDatoer || stoerreDatoer.length === 0) {
		return null
	}
	const maxDate = new Date(Math.min.apply(null, stoerreDatoer))
	if (maxDate) {
		maxDate.setDate(maxDate.getDate() - 1)
	}
	return maxDate
}
