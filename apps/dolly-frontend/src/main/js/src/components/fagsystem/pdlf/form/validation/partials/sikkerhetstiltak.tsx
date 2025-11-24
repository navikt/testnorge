import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'
import { differenceInWeeks, isAfter } from 'date-fns'
import * as _ from 'lodash-es'

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
				(dato, testContext) => {
					const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
					const gyldigFraOgMed = new Date(
						_.get(fullForm, 'pdldata.person.sikkerhetstiltak[0].gyldigFraOgMed'),
					)
					return (
						isAfter(new Date(dato), gyldigFraOgMed) && differenceInWeeks(dato, gyldigFraOgMed) <= 12
					)
				},
			)
			.nullable(),
	}),
)
