import { ifPresent, messages, requiredDate, requiredNumber } from '@/utils/YupValidations'
import * as Yup from 'yup'
import { differenceInCalendarMonths, isAfter, isBefore, isValid } from 'date-fns'
import { formatDate } from '@/utils/DataFormatter'

export const validation = {
	alderspensjon: ifPresent(
		'$pensjonforvalter.alderspensjon',
		Yup.object({
			iverksettelsesdato: Yup.date().when('soknad', {
				is: (soknad: boolean) => soknad,
				then: () =>
					Yup.date()
						.test('is-month-after-now', 'Dato må være frem i tid.', (iverksettelsesdato) => {
							return iverksettelsesdato && isAfter(iverksettelsesdato, new Date())
						})
						.nullable(),
			}),
			saksbehandler: Yup.string().nullable(),
			attesterer: Yup.string().nullable(),
			uttaksgrad: requiredNumber.typeError(messages.required),
			navEnhetId: Yup.string().nullable(),
			relasjoner: Yup.array().of(
				Yup.object({
					sumAvForvArbKapPenInntekt: Yup.number()
						.transform((i, j) => (j === '' ? null : i))
						.nullable(),
				}),
			),
			inkluderAfpPrivat: Yup.boolean().nullable(),
			afpPrivatResultat: Yup.string().nullable(),
		}),
	),
}

const validFomDateTest = (schema: Yup.DateSchema<Date, Yup.AnyObject>) =>
	schema.test('gyldig-fom-dato', 'Feil', (value, context) => {
		if (value == null) return true
		const valgtDato = new Date(value)

		const apDates = context?.options?.context?.personFoerLeggTil?.alderspensjon?.map((ap: any) => {
			if (ap?.data?.system === 'PEN_AP') {
				return ap.data?.transaksjonId?.iverksettelsesdato
			} else if (ap?.data?.system === 'PEN_AP_NY_UTTAKSGRAD') {
				return ap?.data?.transaksjonId?.fom
			}
		})

		const formattedDates = apDates
			?.map((dateString) => new Date(dateString))
			?.filter((date) => isValid(date))

		if (!formattedDates || formattedDates.length === 0) {
			return true
		}

		const sisteVedtak = Math.max(...formattedDates)
		const sisteVedtakDato = new Date(sisteVedtak)
		const nyUttaksgrad = context?.parent?.nyUttaksgrad

		if (isBefore(valgtDato, sisteVedtakDato)) {
			return context.createError({
				message: `Automatisk vedtak av ny uttaksgrad ikke mulig for dato tidligere enn dato på forrige vedtak (${formatDate(sisteVedtakDato)}).`,
			})
		}

		if (
			differenceInCalendarMonths(valgtDato, sisteVedtakDato) < 12 &&
			nyUttaksgrad !== 100 &&
			nyUttaksgrad !== 0
		) {
			return context.createError({
				message: `Automatisk vedtak med gradert uttak ikke mulig oftere enn hver 12 måned. Dato forrige vedtak: ${formatDate(sisteVedtakDato)}.`,
			})
		}
		return true
	})

export const validationNyUttaksgrad = {
	alderspensjonNyUtaksgrad: ifPresent(
		'$pensjonforvalter.alderspensjonNyUtaksgrad',
		Yup.object({
			nyUttaksgrad: requiredNumber.typeError(messages.required),
			fom: validFomDateTest(requiredDate),
			saksbehandler: Yup.string().nullable(),
			attesterer: Yup.string().nullable(),
			navEnhetId: Yup.string().nullable(),
		}),
	),
}
