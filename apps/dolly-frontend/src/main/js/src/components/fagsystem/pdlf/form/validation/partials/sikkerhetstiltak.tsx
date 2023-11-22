import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'
import { differenceInWeeks, isAfter } from 'date-fns'
import * as _ from 'lodash'

export const sikkerhetstiltak = Yup.array().of(
	Yup.object({
		tiltakstype: requiredString,
		beskrivelse: Yup.string().nullable(),
		kontaktperson: Yup.object({
			personident: requiredString,
			enhet: requiredString,
		}),
		gyldigFraOgMed: requiredDate.nullable(),
		gyldigTilOgMed: Yup.string()
			.test(
				'is-after-startdato',
				'Dato må være etter startdato, og ikke mer enn 12 uker etter startdato',
				function validDate(dato) {
					const values = this.options.context
					return (
						isAfter(
							new Date(dato),
							new Date(_.get(values, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed')),
						) &&
						differenceInWeeks(
							new Date(dato),
							new Date(_.get(values, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed')),
						) <= 12
					)
				},
			)
			.nullable(),
	}),
)
