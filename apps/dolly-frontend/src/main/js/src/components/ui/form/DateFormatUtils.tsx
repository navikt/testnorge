import { isValid } from 'date-fns'
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
		formats.push(
			'YYYY-MM-DDTHH:mm:ss.SSSZ',
			'YYYY-MM-DDTHH:mm:ss.SSS',
			'YYYY-MM-DDTHH:mm:ss',
			'DD.MM.YYYY HH:mm',
		)

		return formats
	}
}

export const initDayjs = () => {
	dayjs.extend(customParseFormat)
	dayjs.extend(utc)
	dayjs.extend(timezone)
	return dayjs
}

export const convertInputToDate = (
	date: any,
	fixOffset: boolean = false,
	specificFormat?: string,
) => {
	if (!date || date === '') {
		return date
	}
	const customDayjs = initDayjs()

	const isISOFormat =
		typeof date === 'string' && /^\d{4}-\d{2}-\d{2}(T\d{2}:\d{2}:\d{2})?/.test(date)

	const dayJs = specificFormat
		? customDayjs(date, specificFormat, true)
		: isISOFormat
			? customDayjs(date)
			: isValid(new Date(date))
				? customDayjs(date)
				: customDayjs(date, DateFormatUtils.generateValidDateFormats(), true)

	if (fixOffset) {
		return dayJs.utc().add(dayJs.utcOffset(), 'minute')
	}
	return dayJs
}
