import * as _ from 'lodash-es'

const getAllDatesBetween = (startdato, sluttdato) => {
	const startDate = new Date(startdato)
	const endDate = new Date(sluttdato)

	const arr = []
	for (let dt = startDate; dt <= endDate; dt.setDate(dt.getDate() + 1)) {
		arr.push(new Date(dt.toDateString()))
	}
	return arr
}

export const getExcludedDatesAndMaxDate = (data) => {
	let maxDate = null
	let excludeDates = []
	for (const instdata of data.instdata) {
		const startdato = instdata.startdato
		const sluttdato = instdata.sluttdato
		let days = []

		if (_.isNil(sluttdato)) {
			const start = new Date(startdato)
			maxDate = start.setDate(start.getDate() - 1)
		} else {
			days = getAllDatesBetween(new Date(startdato), new Date(sluttdato))
			excludeDates = excludeDates.concat(days)
		}
	}

	return [excludeDates, maxDate]
}
