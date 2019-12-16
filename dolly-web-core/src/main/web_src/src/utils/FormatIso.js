import formatISO from 'date-fns/formatISO'

// Override default ISO formatering for å unngå timezone
Date.prototype.toISOString = function() {
	return formatISO(this)
}
