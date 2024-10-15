import { ifPresent, requiredString } from '@/utils/YupValidations'
import * as Yup from 'yup'

export const validation = {
	yrkesskader: ifPresent(
		'$yrkesskader',
		Yup.array().of(
			Yup.object({
				rolletype: Yup.string().nullable(),
				innmelderrolle: requiredString,
				klassifisering: Yup.string().nullable(),
				referanse: Yup.string().nullable(),
				ferdigstillSak: Yup.string().nullable(),
				tidstype: Yup.string().nullable(),
				// skadetidspunkt: null,
				// perioder: null,
			}),
		),
	),
}
