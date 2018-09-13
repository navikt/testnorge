import format from 'date-fns/format'

export default function FormatDate(date) {
	return format(date, 'DD.MM.YYYY')
}
