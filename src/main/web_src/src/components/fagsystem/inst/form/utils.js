import _isNil from 'lodash/isNil'

const getAllDatesBetween = (startdato, sluttdato) => {
	const startDate = new Date(startdato)
	const endDate = new Date(sluttdato)

	const arr = []
	for (let dt = new Date(startDate.toDateString()); dt <= endDate; dt.setDate(dt.getDate() + 1)) {
		arr.push(new Date(dt.toDateString()))
	}
	return arr
}

export const getExcludedDatesAndMaxDate = data => {
	let maxDate = null
	let excludeDates = []
	for (let i = 0; i < data.instdata.length; i++) {
		const startdato = data.instdata[i].startdato
		const sluttdato = data.instdata[i].sluttdato
		let days = []

		if (_isNil(sluttdato)) {
			const start = new Date(startdato)
			maxDate = start.setDate(start.getDate() - 1)
		} else {
			days = getAllDatesBetween(new Date(startdato), new Date(sluttdato))
			excludeDates = excludeDates.concat(days)
		}
	}

	return [excludeDates, maxDate]
}
