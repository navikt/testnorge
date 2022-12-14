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
			relasjonListe: Yup.array().of(
				Yup.object({
					samboerFraDato: requiredDate,
					dodsdato: Yup.date().nullable(),
					varigAdskilt: Yup.boolean(),
					fnr: Yup.string(),
					samlivsbruddDato: Yup.date().nullable(),
					harVaertGift: Yup.boolean(),
					harFellesBarn: Yup.boolean(),
					sumAvForvArbKapPenInntekt: Yup.number()
						.transform((i, j) => (j === '' ? null : i))
						.nullable(),
					relasjonType: Yup.string().nullable(),
				})
			),
			flyktning: Yup.boolean(),
			utvandret: Yup.boolean(),
		})
	),
}
