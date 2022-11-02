import { format, isDate } from 'date-fns'
import _startCase from 'lodash/startCase'
import _capitalize from 'lodash/capitalize'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const defaultDateFormat = 'dd.MM.yyyy'
export const defaultDateTimeFormat = 'dd.MM.yyyy HH:mm'
export const defaultDateTimeWithSecondsFormat = 'dd.MM.yyyy HH:mm:ss'

const Formatters = {}

Formatters.formatAlder = (alder, dodsdato) => {
	if (_isNil(alder)) return ''
	return `${alder.toString()}${dodsdato ? ' (død)' : ''}`
}

Formatters.formatAlderBarn = (alder, doedsdato, doedfoedt) => {
	if (_isNil(alder)) {
		return ''
	} else if (doedfoedt) {
		return `${alder.toString()} (dødfødt)`
	} else {
		return `${alder.toString()}${doedsdato ? ' (død)' : ''}`
	}
}

// Format date to readable string format (AAAA-MM-DDTxx:xx:xx to DD.MM.AAAA?)
// Date ---> String
Formatters.formatDate = (date) => {
	if (!date) return date
	// Parse date if not date
	if (!isDate(date)) date = new Date(date)
	return format(date, defaultDateFormat)
}

// Format dateTime to readable string format (AAAA-MM-DDTxx:xx:xx to DD.MM.AAAA hh:mm)
// Date ---> String
Formatters.formatDateTime = (date) => {
	if (!date) return date
	// Parse date if not date
	if (!isDate(date)) date = new Date(date)
	return format(date, defaultDateTimeFormat)
}

Formatters.formatDateTimeWithSeconds = (date) => {
	if (!date) return date
	// Parse date if not date
	if (!isDate(date)) date = new Date(date)
	return format(date, defaultDateTimeWithSecondsFormat)
}

// Format string to Date format
// String ---> Date
Formatters.parseDate = (date) => {
	if (!date) return date

	const parts = date.split('.')
	return new Date(Date.UTC(parts[2], parts[1] - 1, parts[0]))
}

// Format date AAAA-MM-DD to DD.MM.AAAA
Formatters.formatStringDates = (date) => {
	if (!date) return date
	const dateArray = date.split('-')
	return `${dateArray[2]}.${dateArray[1]}.${dateArray[0]}`
}

Formatters.kjonn = (kjonn, alder) => {
	return Formatters.kjonnToString(kjonn, alder < 18)
}

Formatters.kjonnToString = (kjonn = '', barn = false) => {
	if (!kjonn) return kjonn
	const _kjonn = kjonn.toLowerCase()
	if (!['m', 'k'].includes(_kjonn)) return 'UDEFINERT'

	if (barn) return _kjonn === 'm' ? 'GUTT' : 'JENTE'
	return _kjonn === 'm' ? 'MANN' : 'KVINNE'
}

Formatters.adressetypeToString = (adressetype) => {
	if (!adressetype) {
		return null
	}
	switch (adressetype) {
		case 'MATR':
			return 'Matrikkeladresse'
		case 'GATE':
			return 'Gateadresse'
		case 'STED':
			return 'Stedsadresse'
		case 'PBOX':
			return 'Postboksadresse'
		case 'UTAD':
			return 'Utenlandsadresse'
		default:
			return null
	}
}

Formatters.arrayToString = (array, separator = ',') => {
	if (!array) {
		return null
	}

	return array.reduce((accumulator, nextString, idx) => {
		return `${accumulator}${accumulator ? separator : ''}${idx === 0 ? '' : ' '}${
			isNaN(nextString) ? nextString?.toUpperCase() : nextString
		}`
	}, '')
}

Formatters.omraaderArrayToString = (array) => {
	if (!array) {
		return null
	}

	return Formatters.arrayToString(array).replace('*', '* (Alle)')
}

Formatters.uppercaseAndUnderscoreToCapitalized = (value) => {
	if (!value) {
		return null
	}
	const clean = _startCase(value)
	return _capitalize(clean)
}

Formatters.CapitalizedToUppercaseAndUnderscore = (value) => {
	return value.replace(' ', '_').toUpperCase()
}

Formatters.allCapsToCapitalized = (value) => {
	return _capitalize(value)
}

Formatters.codeToNorskLabel = (value) => {
	if (!value) {
		return null
	}
	return Formatters.uppercaseAndUnderscoreToCapitalized(value)
		.replace('oe', 'ø')
		.replace('Oe', 'Ø')
		.replace('ae', 'æ')
		.replace('Ae', 'Æ')
		.replace('aa', 'å')
		.replace('Aa', 'Å')
}

Formatters.oversettBoolean = (value) => {
	if (_isNil(value)) {
		return value
	} else if (value === true || value === 'true') {
		return 'Ja'
	} else if (value === false || value === 'false') {
		return 'Nei'
	} else {
		return value
	}
}

Formatters.gtTypeLabel = (gtType) => {
	if (!gtType) {
		return null
	}

	switch (gtType) {
		case 'KNR':
			return 'Kommune'
		case 'BYDEL':
			return 'Bydel'
		case 'LAND':
			return 'Land'
		default:
			return ''
	}
}

Formatters.showLabel = (optionsGruppe, value) => {
	if (!value || !optionsGruppe) return value
	let copyOptionsGruppe = optionsGruppe

	optionsGruppe.includes('partner') && (copyOptionsGruppe = optionsGruppe.replace('partner_', ''))
	optionsGruppe.includes('barn') && (copyOptionsGruppe = optionsGruppe.replace('barn_', ''))

	const obj = Options(copyOptionsGruppe).filter(
		(options) => options.value.toUpperCase() === value.toUpperCase()
	)

	if (_get(obj, 'label') || _get(obj, '[0].label')) {
		return obj.label || obj[0].label
	}

	return value
}

Formatters.getYearRangeOptions = (start, stop) => {
	const years = []
	for (let i = start; i <= stop; i++) {
		years.push({ value: i, label: i.toString() })
	}
	return years.reverse()
}
export default Formatters
