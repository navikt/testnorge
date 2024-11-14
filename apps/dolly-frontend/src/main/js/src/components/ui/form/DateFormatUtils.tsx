import { isDate } from 'date-fns'
import dayjs from 'dayjs'

class DateFormatUtils {
	static generateValidDateFormats = () => {
		const dayFormats = ['DD', 'D']
		const monthFormats = ['MM', 'M']
		const yearFormats = ['YYYY', 'YY']
		const separators = ['-', '.', '/', '']

		const formats = []

		dayFormats.forEach((day) => {
			monthFormats.forEach((month) => {
				yearFormats.forEach((year) => {
					separators.forEach((separator) => {
						formats.push(`${day}${separator}${month}${separator}${year}`)
						formats.push(`${year}${separator}${month}${separator}${day}`)
					})
				})
			})
		})

		// Spesifikke formater som ikke kan utledes enkelt
		formats.push('YYYY-MM-DDTHH:mm:ss.SSSZ', 'YYYY-MM-DDTHH:mm:ss')

		return formats
	}
}

export const convertInputToDate = (date: any) => {
	if (!date) {
		return date
	}
	const dateString = isDate(date) ? date.toLocaleDateString() : date
	const dateLocalTime = dayjs(dateString, DateFormatUtils.generateValidDateFormats(), true)
	return dateLocalTime.add(dateLocalTime.utcOffset(), 'minute')
}
