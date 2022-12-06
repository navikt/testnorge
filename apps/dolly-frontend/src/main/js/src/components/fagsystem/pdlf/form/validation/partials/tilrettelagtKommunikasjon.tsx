import * as Yup from 'yup'
import { requiredString } from '@/utils/YupValidations'

export const tilrettelagtKommunikasjon = Yup.array().of(
	Yup.object().shape(
		{
			spraakForTaletolk: Yup.mixed().when('spraakForTegnspraakTolk', {
				is: null,
				then: requiredString.nullable(),
			}),
			spraakForTegnspraakTolk: Yup.mixed().when('spraakForTaletolk', {
				is: null,
				then: requiredString.nullable(),
			}),
		},
		['spraakForTaletolk', 'spraakForTegnspraakTolk']
	)
)
