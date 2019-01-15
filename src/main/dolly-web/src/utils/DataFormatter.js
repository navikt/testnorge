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
	if (!['m', 'k'].includes(_kjonn)) return 'UDEFINERT'
	return _kjonn === 'm' ? 'MANN' : 'KVINNE'
}

formatters.kjonnToStringBarn = (kjonn = '') => {
	const _kjonn = kjonn.toLowerCase()
	if (!['m', 'k'].includes(_kjonn)) return 'UDEFINERT'
	return _kjonn === 'm' ? 'GUTT' : 'JENTE'
}

formatters.arrayToString = (array, separator = ',') => {
	return array.reduce((accumulator, nextString, idx) => {
		return `${accumulator}${accumulator ? separator : ''}${
			idx === 0 ? '' : ' '
		}${nextString.toUpperCase()}`
	}, '')
}

formatters.camelCaseToLabel = camelCase => {
	return _startCase(camelCase)
}

formatters.kodeverkLabel = kodeverk => {
	return kodeverk.substring(kodeverk.indexOf('-') + 1)
}

formatters.gtApiKodeverkId = gtType => {
	let gtApiKodeverkId = ''
	switch (gtType) {
		case 'KNR':
			gtApiKodeverkId = 'Kommuner'
			break
		case 'BYDEL':
			gtApiKodeverkId = 'Bydeler'
			break
		case 'LAND':
			gtApiKodeverkId = 'Landkoder'
			break
	}

	return gtApiKodeverkId
}
formatters.sort2DArray = (array, i) => {
	// i er indexen av verdi som man ønsker å sortere på
	return array.sort((a, b) => b[i] - a[i])
}

formatters.commaToSpace = streng => {
	return streng.split(',').join(', ')
}

export default formatters
