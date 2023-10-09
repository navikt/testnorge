import { ifPresent, messages, requiredDate, requiredNumber } from '@/utils/YupValidations'
import * as Yup from 'yup'
import { isAfter } from 'date-fns'

export const validation = {
	alderspensjon: ifPresent(
		'$pensjonforvalter.alderspensjon',
		Yup.object({
			iverksettelsesdato: requiredDate.test(
				'er-fremtid',
				'Måned må være frem i tid',
				function validDate(iverksettelsesdato) {
					return isAfter(new Date(iverksettelsesdato), new Date())
				},
			),
			uttaksgrad: requiredNumber.typeError(messages.required),
			relasjoner: Yup.array().of(
				Yup.object({
					sumAvForvArbKapPenInntekt: Yup.number()
						.transform((i, j) => (j === '' ? null : i))
						.nullable(),
				}),
			),
		}),
	),
}
