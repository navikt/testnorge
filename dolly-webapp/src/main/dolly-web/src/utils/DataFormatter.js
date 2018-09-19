import dateFormatter from 'date-fns/format'

const formatters = {}

// Skriv ut FNR og DNR med mellom mellom fødselsdato og personnummer
// Ex: 010195 12345
formatters.formatIdentNr = identNr => {
	const birth = identNr.substring(0, 6)
	const personnummer = identNr.substring(6, 11)
	return `${birth} ${personnummer}`
}

// Format date to
formatters.formatDate = date => {
	return dateFormatter(date, 'DD.MM.YYYY')
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

export default formatters
