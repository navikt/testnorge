import * as Yup from 'yup'
import * as _ from 'lodash-es'
import { parseDate } from '@/utils/DataFormatter'
import { isDate } from 'date-fns'

/**
 * Valideringsmeldinger
 */
export const messages = {
	required: 'Feltet er påkrevd',
	positive: 'Verdien må være større enn 0',
}

// Dato som kommer fra Maler er av typen String
export const requiredDate = Yup.mixed()
	.nullable()
	.test('reqDate', messages.required, (value) => {
		let _value = value
		if (_.isString(value)) _value = parseDate(_value)
		return isDate(_value)
	})
export const requiredString = Yup.string().required(messages.required)
export const requiredBoolean = Yup.boolean().required(messages.required)
export const requiredNumber = Yup.number().required(messages.required)

export const ifPresent = (key, schema) =>
	Yup.mixed().when(key, (_val, _foo, resolveOptions) => {
		return _.isUndefined(resolveOptions.value) ? Yup.mixed().notRequired() : schema
	})

export const ifNotBlank = (key, schema) =>
	Yup.mixed().when(key, {
		is: (v) => !_.isNull(v) && !_.isEmpty(v),
		then: () => schema,
		otherwise: () => Yup.mixed().notRequired(),
	})
