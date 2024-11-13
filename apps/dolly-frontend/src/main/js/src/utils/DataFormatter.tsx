import { format, isDate } from 'date-fns'
import _ from 'lodash'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import dayjs from 'dayjs'
import customParseFormat from 'dayjs/plugin/customParseFormat'
import { convertInputToDate } from '@/components/ui/form/formUtils'

dayjs.locale('nb')
dayjs.extend(customParseFormat)
export const yearFormat = 'yyyy'
export const defaultDateFormat = 'dd.MM.yyyy'
export const defaultDateTimeFormat = 'dd.MM.yyyy HH:mm'
export const defaultDateTimeWithSecondsFormat = 'dd.MM.yyyy HH:mm:ss'

export const formatAlder = (alder, dodsdato) => {
	if (_.isNil(alder)) return ''
	return `${alder.toString()}${dodsdato ? ' (død)' : ''}`
}

export const formatAlderBarn = (alder, doedsdato, doedfoedt) => {
	if (_.isNil(alder)) {
		return ''
	} else if (doedfoedt) {
		return `${alder.toString()} (dødfødt)`
	} else {
		return `${alder.toString()}${doedsdato ? ' (død)' : ''}`
	}
}

// Format date to readable string format (AAAA-MM-DDTxx:xx:xx to DD.MM.AAAA?)
// Date ---> String
export const formatDate = (date: any, formatString?: string) => {
	if (!date) return date
	const dayjsDate = convertInputToDate(date)
	const valid = dayjsDate.isValid()
	// Parse date if valid date
	if (!valid) {
		return date
	}
	return dayjsDate.format(formatString || 'DD.MM.YYYY')
}

// Format date to readable string format (AAAAMMDD to DD.MM.AAAA)
export const formatTenorDate = (dateString: any, formatString?: string) => {
	if (!dateString) return dateString
	// Parse date from string
	const year = dateString.substring(0, 4)
	const month = dateString.substring(4, 6)
	const day = dateString.substring(6, 8)
	const date = new Date(year, month - 1, day)

	return format(date, formatString || defaultDateFormat)
}

export const formatDateToYear = (date) => {
	if (!date) return date
	// Parse date if not date
	if (!isDate(date)) date = new Date(date)
	return format(date, yearFormat)
}

// Format dateTime to readable string format (AAAA-MM-DDTxx:xx:xx to DD.MM.AAAA hh:mm)
// Date ---> String
export const formatDateTime = (date) => {
	if (!date) return date
	// Parse date if not date
	if (!isDate(date)) date = new Date(date)
	return format(date, defaultDateTimeFormat)
}

export const formatDateTimeWithSeconds = (date) => {
	if (!date) return date
	// Parse date if not date
	if (!isDate(date)) date = new Date(date)
	return format(date, defaultDateTimeWithSecondsFormat)
}

// Format string to Date format
// String ---> Date
export const parseDate = (date) => {
	if (!date) return date

	const parts = date.split('.')
	return new Date(Date.UTC(parts[2], parts[1] - 1, parts[0]))
}

// Format date AAAA-MM-DD to DD.MM.AAAA
export const formatStringDates = (date) => {
	if (!date) return date
	const dateArray = date.split('-')
	return `${dateArray[2]}.${dateArray[1]}.${dateArray[0]}`
}

export const formatKjonn = (kjonn, alder) => {
	return formatKjonnToString(kjonn, alder < 18)
}

export const formatKjonnToString = (kjonn = '', barn = false) => {
	if (!kjonn) return kjonn
	const _kjonn = kjonn.toLowerCase()
	if (!['m', 'k'].includes(_kjonn)) return 'UDEFINERT'

	if (barn) return _kjonn === 'm' ? 'GUTT' : 'JENTE'
	return _kjonn === 'm' ? 'MANN' : 'KVINNE'
}

export const adressetypeToString = (adressetype) => {
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

export const arrayToString = (array, separator = ',') => {
	if (!array) {
		return null
	}

	return array.reduce((accumulator, nextString, idx) => {
		return `${accumulator}${accumulator ? separator : ''}${idx === 0 ? '' : ' '}${nextString}`
	}, '')
}

export const omraaderArrayToString = (array) => {
	if (!array) {
		return null
	}

	return arrayToString(array.map((item) => item.tema)).replace('*', '* (Alle)')
}

export const uppercaseAndUnderscoreToCapitalized = (value) => {
	if (!value) {
		return null
	}
	const clean = _.startCase(value)
	return _.capitalize(clean)
}

export const CapitalizedToUppercaseAndUnderscore = (value) => {
	return value.replace(' ', '_').toUpperCase()
}

export const allCapsToCapitalized = (value) => {
	return _.capitalize(value)
}

export const toTitleCase = (value) => {
	if (!value) {
		return ''
	}
	return value
		.split(' ')
		.map(_.capitalize)
		.join(' ')
		.split('-')
		.map((str) => str.charAt(0).toUpperCase() + str.slice(1))
		.join('-')
}

export const codeToNorskLabel = (value) => {
	if (!value) {
		return null
	}
	return uppercaseAndUnderscoreToCapitalized(value)
		.replace('oe', 'ø')
		.replace('Oe', 'Ø')
		.replace('ae', 'æ')
		.replace('Ae', 'Æ')
		.replace('aa', 'å')
		.replace('Aa', 'Å')
}

export const oversettBoolean = (value) => {
	if (_.isNil(value)) {
		return value
	} else if (value === true || value === 'true' || value === 'J') {
		return 'Ja'
	} else if (value === false || value === 'false' || value === 'N') {
		return 'Nei'
	} else {
		return value
	}
}

export const gtTypeLabel = (gtType) => {
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

export const showLabel = (optionsGruppe, value) => {
	if (!value || !optionsGruppe) return value
	let copyOptionsGruppe = optionsGruppe

	optionsGruppe.includes('partner') && (copyOptionsGruppe = optionsGruppe.replace('partner_', ''))
	optionsGruppe.includes('barn') && (copyOptionsGruppe = optionsGruppe.replace('barn_', ''))

	const obj = Options(copyOptionsGruppe).filter((options) =>
		typeof value === 'string'
			? options.value.toUpperCase() === value.toUpperCase()
			: options.value === value,
	)

	if (_.get(obj, 'label') || _.get(obj, '[0].label')) {
		return obj.label || obj[0].label
	}

	return value
}

export const showKodeverkLabel = (kodeverkNavn, value) => {
	if (!kodeverkNavn || !value) {
		return value
	}
	if (value.includes('*')) {
		return 'Alle (*)'
	}
	const { kodeverk, loading, error } = useKodeverk(kodeverkNavn)
	if (loading || error) {
		return value
	}
	return kodeverk?.find((kode) => kode?.value === value)?.label
}

export const getYearRangeOptions = (start, stop) => {
	const years = []
	for (let i = start; i <= stop; i++) {
		years.push({ value: i, label: i.toString() })
	}
	return years.reverse()
}

export const formatXml = (xml: string, tab = '\t') => {
	let formatted = ''
	let indent = ''
	xml.split(/>\s*</).forEach(function (node) {
		if (/^\/\w/.exec(node)) {
			indent = indent.substring(tab.length)
		}
		formatted += indent + '<' + node + '>\r\n'
		if (/^<?\w[^>]*[^/]$/.exec(node)) {
			indent += tab
		}
	})
	return formatted.substring(1, formatted.length - 3)
}
