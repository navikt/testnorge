import {
	ifPresent,
	messages,
	requiredDate,
	requiredNumber,
	requiredString,
} from '~/utils/YupValidations'
import * as Yup from 'yup'

export const validation = {
	alderspensjon: ifPresent(
		'$pensjonforvalter.alderspensjon',
		Yup.object({
			iverksettelsesdato: requiredDate,
			uttaksgrad: requiredNumber.typeError(messages.required),
			sivilstand: requiredString.nullable(),
			sivilstatusDatoFom: Yup.date().nullable(),
		})
	),
}
