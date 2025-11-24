import * as Yup from 'yup'
import { ifPresent, messages, requiredDate, requiredString } from '@/utils/YupValidations'

export const histarkValidation = {
	histark: ifPresent(
		'$histark',
		Yup.object({
			dokumenter: Yup.array().of(
				Yup.object({
					tittel: requiredString,
					temakoder: Yup.array().required().min(1, 'Velg minst en temakode'),
					enhetsnavn: Yup.string().required('Velg en NAV-enhet'),
					enhetsnummer: requiredString,
					skanner: requiredString,
					skannested: requiredString,
					skanningsTidspunkt: requiredDate.nullable(),
					startYear: Yup.number()
						.required(messages.required)
						.test('start-before-slutt', 'Startår må være før sluttår', (value, context) => {
							const sluttAar = context.parent.endYear
							return value && sluttAar && value < sluttAar
						}),
					endYear: Yup.number()
						.required(messages.required)
						.test('slutt-after-start', 'Sluttår må være etter startår', (value, context) => {
							const startAar = context.parent.startYear
							return value && startAar && value > startAar
						}),
				}),
			),
		}),
	),
}
