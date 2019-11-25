import * as Yup from 'yup'
import _isUndefined from 'lodash/isUndefined'
import { isDate } from 'date-fns'

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
