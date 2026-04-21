import * as Yup from 'yup'
import { requiredDate, requiredString } from '@/utils/YupValidations'
import { differenceInWeeks, isAfter } from 'date-fns'

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
					const valgtDato = dato && new Date(dato)
					const gyldigFraOgMed = new Date(testContext.parent?.gyldigFraOgMed)
					return isAfter(valgtDato, gyldigFraOgMed) && differenceInWeeks(dato, gyldigFraOgMed) <= 12
				},
			)
			.nullable(),
	}),
)
