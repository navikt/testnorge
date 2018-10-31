import dateFnsFormat from 'date-fns/format'
import dateFnsParse from 'date-fns/parse'
import _startCase from 'lodash/startCase'

import { defaultDateFormat } from '~/components/fields/Datepicker/DateValidation'

const formatters = {}

// Skriv ut FNR og DNR med mellom mellom fødselsdato og personnummer
// Ex: 010195 12345
formatters.formatIdentNr = ident => {
	if (!ident) return ident
	const birth = ident.substring(0, 6)
	const personnummer = ident.substring(6, 11)
	return `${birth}${personnummer}`
}

formatters.formatAlder = (alder, dodsdato) => {
	return `${alder.toString()}${dodsdato ? ' (død)' : ''}`
}

// Format date to readable string format
// Date ---> String
formatters.formatDate = date => {
	if (!date) return date
	return dateFnsFormat(date, defaultDateFormat, new Date())
}

// Format string to Date format
// String ---> Date
formatters.parseDate = date => {
	if (!date) return date

	const parts = date.split('.')
	return new Date(Date.UTC(parts[2], parts[1] - 1, parts[0]))
}

formatters.kjonnToString = (kjonn = '') => {
	const _kjonn = kjonn.toLowerCase()
	if (!['m', 'k'].includes(_kjonn)) return 'udefinert'
	return _kjonn === 'm' ? 'Mann' : 'Kvinne'
}

formatters.kjonnToStringBarn = (kjonn = '') => {
	const _kjonn = kjonn.toLowerCase()
	if (!['m', 'k'].includes(_kjonn)) return 'udefinert'
	return _kjonn === 'm' ? 'Gutt' : 'Jente'
}

formatters.arrayToString = (array, separator = ',') => {
	return array.reduce((accumulator, nextString) => {
		return `${accumulator}${accumulator ? separator : ''} ${nextString.toUpperCase()}`
	}, '')
}

formatters.camelCaseToLabel = camelCase => {
	return _startCase(camelCase)
}

export default formatters
