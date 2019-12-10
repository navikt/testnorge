import * as Yup from 'yup'
import _isUndefined from 'lodash/isUndefined'
import { isDate, isWithinInterval } from 'date-fns'

/**
 * Valideringsmeldinger
 */
export const messages = {
	required: 'Feltet er påkrevd'
}

export const requiredDate = Yup.mixed().test('reqDate', messages.required, value => isDate(value))
export const requiredString = Yup.string().required(messages.required)
export const requiredBoolean = Yup.boolean().required(messages.required)
export const requiredNumber = Yup.number().required(messages.required)

export const ifPresent = (key, schema) =>
	Yup.mixed().when(key, {
		is: v => !_isUndefined(v),
		then: schema,
		otherwise: Yup.mixed().notRequired()
	})

export const ifKeyHasValue = (key, values, schema) =>
	Yup.mixed().when(key, {
		is: v => values.includes(v),
		then: schema,
		otherwise: Yup.mixed().notRequired()
	})

// export const validDate = () =>
// 	Yup.date().test('range', 'Feilmelding', val =>
// 		isWithinInterval(val, { start: new Date(2014, 1, 1), end: new Date(2014, 12, 7) })
// 	)
