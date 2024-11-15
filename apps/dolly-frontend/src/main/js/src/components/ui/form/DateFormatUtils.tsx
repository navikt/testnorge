import { isDate } from 'date-fns'
import dayjs from 'dayjs'
import customParseFormat from 'dayjs/plugin/customParseFormat'
import utc from 'dayjs/plugin/utc'
import timezone from 'dayjs/plugin/timezone'

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
		formats.push('YYYY-MM-DDTHH:mm:ss.SSSZ', 'YYYY-MM-DDTHH:mm:ss.SSS', 'YYYY-MM-DDTHH:mm:ss')

		return formats
	}
}

export const initDayjs = () => {
	dayjs.extend(customParseFormat)
	dayjs.extend(utc)
	dayjs.extend(timezone)
	dayjs.tz.setDefault('Europe/Oslo')
	return dayjs
}

export const convertInputToDate = (date: any) => {
	if (!date || date === '') {
		return date
	}
	const customDayjs = initDayjs()
	const dateLocalTime = isDate(date)
		? customDayjs(date)
		: customDayjs(date, DateFormatUtils.generateValidDateFormats(), true)
	return dateLocalTime.add(dateLocalTime.utcOffset(), 'minute')
}
