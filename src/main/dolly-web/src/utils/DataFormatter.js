import dateFnsFormat from 'date-fns/format'
import dateFnsParse from 'date-fns/parse'
import _startCase from 'lodash/startCase'
import _capitalize from 'lodash/capitalize'

import { defaultDateFormat } from '~/components/fields/Datepicker/DateValidation'
import SelectOptionsManager from '~/service/kodeverk/SelectOptionsManager/SelectOptionsManager'

const Formatters = {}

Formatters.formatAlder = (alder, dodsdato) => {
	if (!alder) return ''
	return `${alder.toString()}${dodsdato ? ' (død)' : ''}`
}

// Format date to readable string format (AAAA-MM-DDTxx:xx:xx to DD.MM.AAAA?)
// Date ---> String
Formatters.formatDate = date => {
	if (!date) return date
	if (date.length == 10) return date
	return dateFnsFormat(date, defaultDateFormat, new Date())
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

Formatters.decamelize = (str, separator) => {
	separator = typeof separator === 'undefined' ? '_' : separator

	const res = str
		.replace(/([a-z\d])([A-Z])/g, '$1' + separator + '$2')
		.replace(/([A-Z]+)([A-Z][a-z\d]+)/g, '$1' + separator + '$2')
		.toLowerCase()

	return res.charAt(0).toUpperCase() + res.slice(1)
}

Formatters.kjonnToString = (kjonn = '') => {
	if (!kjonn) return kjonn
	const _kjonn = kjonn.toLowerCase()
	if (!['m', 'k'].includes(_kjonn)) return 'UDEFINERT'
	return _kjonn === 'm' ? 'MANN' : 'KVINNE'
}

Formatters.kjonnToStringBarn = (kjonn = '') => {
	const _kjonn = kjonn.toLowerCase()
	if (!['m', 'k'].includes(_kjonn)) return 'UDEFINERT'
	return _kjonn === 'm' ? 'GUTT' : 'JENTE'
}

Formatters.adressetypeToString = adressetype => {
	return adressetype === 'MATR' ? 'Matrikkeladresse' : adressetype === 'GATE' ? 'Gateadresse' : null
}

Formatters.arrayToString = (array, separator = ',') => {
	if (!array) return null

	return array.reduce((accumulator, nextString, idx) => {
		return `${accumulator}${accumulator ? separator : ''}${
			idx === 0 ? '' : ' '
		}${nextString.toUpperCase()}`
	}, '')
}

Formatters.camelCaseToLabel = camelCase => {
	if (!camelCase) return null
	return _capitalize(_startCase(camelCase))
}

Formatters.uppercaseAndUnderscoreToCapitalized = value => {
	if (!value) return null
	const clean = _startCase(value)
	return _capitalize(clean)
}

Formatters.allCapsToCapitalized = value => {
	return _capitalize(value)
}

Formatters.kodeverkLabel = kodeverk => {
	if (!kodeverk) return null
	return kodeverk.substring(kodeverk.indexOf('-') + 1)
}

Formatters.oversettBoolean = value => {
	if (value === null) return null
	return value === true || value === 'true'
		? 'Ja'
		: value === false || value === 'false'
			? 'Nei'
			: value
}

Formatters.booleanToServicebehov = value => {
	return value === true ? 'Med servicebehov' : value === false ? 'Uten servicebehov' : value
}

Formatters.servicebehovKodeTilBeskrivelse = value => {
	if (!value) return null
	let beskrivelse = value
	switch (value) {
		case 'IKVAL':
			beskrivelse = 'IKVAL - Standardinnsats'
			break
		case 'BFORM':
			beskrivelse = 'BFORM - Situasjonsbestemt innsats'
			break
		case 'BATT':
			beskrivelse = 'BATT - Spesielt tilpasset innsats'
			break
		case 'VARIG':
			beskrivelse = 'VARIG - Varig tilpasset innsats'
			break
	}
	return beskrivelse
}

Formatters.gtApiKodeverkId = gtType => {
	if (!gtType) return null

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

Formatters.gtTypeLabel = gtType => {
	if (!gtType) return null

	let gtTypeLabel = ''
	switch (gtType) {
		case 'KNR':
			gtTypeLabel = 'Kommune'
			break
		case 'BYDEL':
			gtTypeLabel = 'Bydel'
			break
		case 'LAND':
			gtTypeLabel = 'Land'
			break
	}

	return gtTypeLabel
}

Formatters.sort2DArray = (array, i) => {
	// i er indexen av verdi som man ønsker å sortere på
	return array.sort((a, b) => {
		var lengde = Formatters.getIdLengde(a[i])
		var aSub = a[i].substr(0, lengde)
		var bSub = b[i].substr(0, lengde)
		return bSub - aSub
	})
}

Formatters.flat2DArray = (array, i) => {
	if (!array) return null

	array.forEach(person => {
		if (person[i].includes(',')) {
			const arrayValues = person[i].split(',')
			person[i] = Math.max(...arrayValues).toString() + ' ...'
		}
	})
	return array
}

Formatters.getIdLengde = id => {
	if (!id) return null

	var forste = id.split(' ')
	return forste[0].length
}

Formatters.idUtenEllipse = id => {
	if (!id) return null

	var lengde = Formatters.getIdLengde(id)
	return id.substr(0, lengde)
}

Formatters.commaToSpace = streng => {
	if (!streng) return null
	return streng.split(',').join(', ')
}

Formatters.showLabel = (optionsGruppe, value) => {
	if (!value || !optionsGruppe) return value
	let copyOptionsGruppe = optionsGruppe

	optionsGruppe.includes('partner') && (copyOptionsGruppe = optionsGruppe.replace('partner_', ''))
	optionsGruppe.includes('barn') && (copyOptionsGruppe = optionsGruppe.replace('barn_', ''))

	const obj = SelectOptionsManager(copyOptionsGruppe).filter(options => options.value === value)
	return obj.label || obj[0].label
}

export default Formatters
