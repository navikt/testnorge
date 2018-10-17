import dateFnsFormat from 'date-fns/format'
import dateFnsParse from 'date-fns/parse'
import { defaultDateFormat } from '~/components/fields/Datepicker/DateValidation'

const formatters = {}

// Skriv ut FNR og DNR med mellom mellom fødselsdato og personnummer
// Ex: 010195 12345
formatters.formatIdentNr = ident => {
	if (!ident) return ident
	const birth = ident.substring(0, 6)
	const personnummer = ident.substring(6, 11)
	return `${birth} ${personnummer}`
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
// IMPORTANT! Used only for datepicker that has default time 00:00:00, offset in timezone is added to correctly set time
formatters.parseDate = date => {
	if (!date) return date
	const parsedDate = dateFnsParse(date, defaultDateFormat, new Date())
	const offSett = parsedDate.getTimezoneOffset() * 60000
	return new Date(parsedDate.getTime() - offSett)
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

export default formatters
