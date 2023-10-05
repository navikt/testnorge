import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

export const validation = {
	sigrunstubPensjonsgivende: ifPresent(
		'$sigrunstubPensjonsgivende',
		Yup.array().of(
			Yup.object({
				inntektsaar: requiredString,
				testdataEier: Yup.string().nullable(),
				pensjonsgivendeInntekt: Yup.array().min(1, 'Må ha minst én inntekt'),
			}),
		),
	),
}
