import { ifPresent, messages, requiredDate, requiredNumber } from '@/utils/YupValidations'
import * as Yup from 'yup'

export const validation = {
	alderspensjon: ifPresent(
		'$pensjonforvalter.alderspensjon',
		Yup.object({
			iverksettelsesdato: requiredDate,
			uttaksgrad: requiredNumber.typeError(messages.required),
			relasjoner: Yup.array().of(
				Yup.object({
					sumAvForvArbKapPenInntekt: Yup.number()
						.transform((i, j) => (j === '' ? null : i))
						.nullable(),
				})
			),
		})
	),
}
