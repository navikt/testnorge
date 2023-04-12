import * as Yup from 'yup'
import { requiredString } from '@/utils/YupValidations'

export const tilrettelagtKommunikasjon = Yup.array().of(
	Yup.object().shape(
		{
			spraakForTaletolk: Yup.string().when('spraakForTegnspraakTolk', {
				is: (value: string) => {
					return !value
				},
				then: () => {
					return requiredString
				},
				otherwise: () => Yup.string().nullable(),
			}),
			spraakForTegnspraakTolk: Yup.string().when('spraakForTaletolk', {
				is: (value: string) => !value,
				then: () => requiredString,
				otherwise: () => Yup.string().nullable(),
			}),
		},
		['spraakForTaletolk', 'spraakForTegnspraakTolk']
	)
)
