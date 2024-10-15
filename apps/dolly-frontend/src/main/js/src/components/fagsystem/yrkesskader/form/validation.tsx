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
				skadetidspunkt: Yup.mixed().when('tidstype', {
					is: (tidstype: string) => tidstype === 'tidspunkt',
					then: () => requiredString,
					otherwise: () => Yup.mixed().notRequired(),
				}),
				perioder: Yup.array()
					.of(
						Yup.object({
							fra: ifPresent('$fra', requiredString),
							til: ifPresent('$til', requiredString),
						}),
					)
					.nullable(),
			}),
		),
	),
}
