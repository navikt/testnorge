import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'
import { differenceInWeeks, isAfter, isSameDay } from 'date-fns'
import _get from 'lodash/get'

export const sikkerhetstiltak = Yup.array().of(
	Yup.object({
		tiltakstype: requiredString.nullable(),
		beskrivelse: Yup.string().nullable(),
		kontaktperson: Yup.object({
			personident: requiredString.nullable(),
			enhet: requiredString.nullable(),
		}),
		gyldigFraOgMed: requiredDate.nullable(),
		gyldigTilOgMed: Yup.string()
			.test(
				'is-after-startdato',
				'Dato må være lik eller etter startdato, og ikke mer enn 12 uker etter startdato',
				function validDate(dato) {
					const values = this.options.context
					return (
						(isAfter(
							new Date(dato),
							new Date(_get(values, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed'))
						) ||
							isSameDay(
								new Date(dato),
								new Date(_get(values, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed'))
							)) &&
						differenceInWeeks(
							new Date(dato),
							new Date(_get(values, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed'))
						) <= 12
					)
				}
			)
			.nullable(),
	})
)
