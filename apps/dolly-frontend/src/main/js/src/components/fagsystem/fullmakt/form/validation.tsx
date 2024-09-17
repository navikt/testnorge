import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'
import { testDatoTom } from '@/components/fagsystem/utils'

export const validation = {
	fullmakt: ifPresent(
		'$fullmakt',
		Yup.array()
			.min(1)
			.of(
				Yup.object({
					gyldigFraOgMed: ifPresent('$fullmakt.gyldigFraOgMed', requiredDate),
					gyldigTilogMed: ifPresent(
						'$fullmakt.gyldigTilOgMed',
						testDatoTom(Yup.string(), 'gyldigFraOgMed'),
					),
					fullmektig: requiredString,
					omraade: Yup.array()
						.min(1)
						.of(
							Yup.object({
								tema: requiredString,
								handling: Yup.array().min(1).of(requiredString),
							}),
						),
				}),
			),
	),
}
