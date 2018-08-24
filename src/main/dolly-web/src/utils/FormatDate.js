import Moment from 'moment'

export default function FormatDate(date) {
	return Moment(date).format('DD.MM.YYYY')
}
