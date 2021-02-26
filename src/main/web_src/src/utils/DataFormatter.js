import { format, isDate } from 'date-fns'
import _startCase from 'lodash/startCase'
import _capitalize from 'lodash/capitalize'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const defaultDateFormat = 'dd.MM.yyyy'

const Formatters = {}

Formatters.formatAlder = (alder, dodsdato) => {
	if (_isNil(alder)) return ''
	return `${alder.toString()}${dodsdato ? ' (død)' : ''}`
}

Formatters.formatAlderBarn = (alder, doedsdato, doedfoedt) => {
	if (_isNil(alder)) return ''
	return doedfoedt
		? `${alder.toString()} (dødfødt)`
		: `${alder.toString()}${doedsdato ? ' (død)' : ''}`
}

// Format date to readable string format (AAAA-MM-DDTxx:xx:xx to DD.MM.AAAA?)
// Date ---> String
Formatters.formatDate = date => {
	if (!date) return date
	if (date.length == 10) return date
	// Parse date if not date
	if (!isDate(date)) date = new Date(date)
	return format(date, defaultDateFormat)
}

// Format string to Date format
// String ---> Date
Formatters.parseDate = date => {
	if (!date) return date

	const parts = date.split('.')
	return new Date(Date.UTC(parts[2], parts[1] - 1, parts[0]))
}

// Format date AAAA-MM-DD to DD.MM.AAAA
Formatters.formatStringDates = date => {
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

Formatters.adressetypeToString = adressetype => {
	if (!adressetype) return null
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
	if (!array) return null

	return array.reduce((accumulator, nextString, idx) => {
		return `${accumulator}${accumulator ? separator : ''}${idx === 0 ? '' : ' '}${
			isNaN(nextString) ? nextString.toUpperCase() : nextString
		}`
	}, '')
}

Formatters.omraaderArrayToString = array => {
	if (!array) return null

	return Formatters.arrayToString(array).replace('*', '* (Alle)')
}

Formatters.uppercaseAndUnderscoreToCapitalized = value => {
	if (!value) return null
	const clean = _startCase(value)
	return _capitalize(clean)
}

Formatters.CapitalizedToUppercaseAndUnderscore = value => {
	return value.replace(' ', '_').toUpperCase()
}

Formatters.allCapsToCapitalized = value => {
	return _capitalize(value)
}

Formatters.codeToNorskLabel = value => {
	if (!value) return null
	return Formatters.uppercaseAndUnderscoreToCapitalized(value)
		.replace('oe', 'ø')
		.replace('Oe', 'Ø')
		.replace('ae', 'æ')
		.replace('Ae', 'Æ')
		.replace('aa', 'å')
		.replace('Aa', 'Å')
}

Formatters.oversettBoolean = value => {
	if (_isNil(value)) return value
	return value === true || value === 'true'
		? 'Ja'
		: value === false || value === 'false'
		? 'Nei'
		: value
}

Formatters.gtTypeLabel = gtType => {
	if (!gtType) return null

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

/**
 * Returns a random integer between min (inclusive) and max (inclusive).
 * The value is no lower than min (or the next integer greater than min
 * if min isn't an integer) and no greater than max (or the next integer
 * lower than max if max isn't an integer).
 * Using Math.round() will give you a non-uniform distribution!
 *
 * https://stackoverflow.com/a/1527820/3336235
 */
Formatters.randomIntInRange = (min, max) => {
	min = Math.ceil(min)
	max = Math.floor(max)
	return Math.floor(Math.random() * (max - min + 1)) + min
}

Formatters.showLabel = (optionsGruppe, value) => {
	if (!value || !optionsGruppe) return value
	let copyOptionsGruppe = optionsGruppe

	optionsGruppe.includes('partner') && (copyOptionsGruppe = optionsGruppe.replace('partner_', ''))
	optionsGruppe.includes('barn') && (copyOptionsGruppe = optionsGruppe.replace('barn_', ''))

	const obj = Options(copyOptionsGruppe).filter(options => options.value === value)

	if (_get(obj, 'label') || _get(obj, '[0].label')) {
		return obj.label || obj[0].label
	}
}

Formatters.getYearRangeOptions = (start, stop) => {
	const years = []
	for (let i = start; i <= stop; i++) {
		years.push({ value: i, label: i.toString() })
	}
	return years.reverse()
}
export default Formatters
