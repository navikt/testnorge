import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredString } from '@/utils/YupValidations'
import { testDatoTom } from '@/components/fagsystem/utils'
import * as _ from 'lodash-es'
import { isBefore, subDays } from 'date-fns'

export const validation = {
	fullmakt: ifPresent(
		'$fullmakt',
		Yup.array()
			.min(1, 'Må inneholde minst en fullmakt')
			.of(
				Yup.object({
					omraade: Yup.array()
						.min(1, 'Må inneholde minst ett område')
						.of(
							Yup.object({
								tema: requiredString,
								handling: Yup.array().min(1, 'Feltet er påkrevd').of(requiredString),
							}),
						),
					gyldigFraOgMed: requiredDate.test(
						'reqDate',
						'Dato kan ikke være tilbake i tid',
						(value) => {
							const chosenDate = _.isString(value) ? new Date(value) : value
							const currentDate = subDays(new Date(), 1)
							return isBefore(currentDate, chosenDate)
						},
					),
					gyldigTilOgMed: ifPresent(
						'$fullmakt.gyldigTilOgMed',
						testDatoTom(
							Yup.date().nullable(),
							'gyldigFraOgMed',
							'Sluttdato må være etter startdato',
						),
					),
				}),
			),
	),
}
