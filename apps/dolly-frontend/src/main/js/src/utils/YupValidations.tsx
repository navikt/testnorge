import * as Yup from 'yup'
import * as _ from 'lodash'
import { parseDate } from '@/utils/DataFormatter'
import { isDate } from 'date-fns'

/*
For custom validation der vi kan bruke f.eks. context.
*/
export const validate = async (values, schema) => {
	if (!schema) return
	try {
		await schema.validate(values, { abortEarly: false, context: values })
		return {}
	} catch (err) {
		if (err.name === 'ValidationError') {
			console.warn('Validation error: ', err)
			// return yupToFormErrors(err)
		} else {
			console.info('Validation error')
			throw err
		}
	}
}

/**
 * Valideringsmeldinger
 */
export const messages = {
	required: 'Feltet er påkrevd',
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
