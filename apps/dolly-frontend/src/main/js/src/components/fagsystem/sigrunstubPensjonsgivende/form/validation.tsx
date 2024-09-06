import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

export const validation = {
	sigrunstubPensjonsgivende: ifPresent(
		'$sigrunstubPensjonsgivende',
		Yup.array()
			.test(
				'duplicate',
				'Kan ikke ha to pensjonsgivende inntekter for samme år',
				(val, _context) => {
					const years = val?.map((v) => v.inntektsaar)
					return years?.length === new Set(years).size
				},
			)
			.of(
				Yup.object({
					inntektsaar: requiredString,
					testdataEier: Yup.string().nullable(),
					pensjonsgivendeInntekt: Yup.array()
						.test('duplicate', 'Skatteordningene kan ikke være like', (value, _context) => {
							return value?.[0]?.skatteordning !== value?.[1]?.skatteordning
						})
						.min(1, 'Må ha minst én inntekt'),
				}),
			),
	),
}
