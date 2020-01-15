import * as Yup from 'yup'
import _get from 'lodash/get'
import { isWithinInterval, getMonth } from 'date-fns'
import { requiredDate, requiredString, ifPresent } from '~/utils/YupValidations'

const innenforAnsettelsesforholdTest = (validation, validateMonthPath) => {
	const errorMsg = 'Dato må være innenfor ansettelsesforhold'
	const errorMsgMonth =
		'Dato må være innenfor ansettelsesforhold, og i samme kalendermåned og år som fra-dato'
	return validation.test(
		'range',
		validateMonthPath ? errorMsgMonth : errorMsg,
		function isWithinTest(val) {
			if (!val) return true
			const values = this.options.context

			if (validateMonthPath) {
				const fomMonth = _get(values, validateMonthPath)
				if (getMonth(val) !== getMonth(fomMonth)) return false
			}

			return isWithinInterval(val, {
				start: values.aareg[0].ansettelsesPeriode.fom,
				end: values.aareg[0].ansettelsesPeriode.tom || new Date()
			})
		}
	)
}

const antallTimerForTimeloennet = Yup.array().of(
	Yup.object({
		periode: Yup.object({
			fom: innenforAnsettelsesforholdTest(requiredDate),
			tom: innenforAnsettelsesforholdTest(
				requiredDate,
				'aareg[0].antallTimerForTimeloennet[0].periode.fom'
			)
		}),
		antallTimer: Yup.number()
			.min(1, 'Kan ikke være mindre enn 1')
			.required('Feltet er påkrevd')
	})
)

const permisjon = Yup.array().of(
	Yup.object({
		permisjonsPeriode: Yup.object({
			fom: innenforAnsettelsesforholdTest(requiredDate),
			tom: innenforAnsettelsesforholdTest(Yup.date().nullable())
		}),
		permisjonsprosent: Yup.number()
			.min(1, 'Kan ikke være mindre enn 1')
			.max(100, 'Kan ikke være større enn 100')
			.required('Feltet er påkrevd'),
		permisjonOgPermittering: requiredString
	})
)

const utenlandsopphold = Yup.array().of(
	Yup.object({
		periode: Yup.object({
			fom: innenforAnsettelsesforholdTest(requiredDate),
			tom: innenforAnsettelsesforholdTest(
				Yup.date().nullable(),
				'aareg[0].utenlandsopphold[0].periode.fom'
			)
		}),
		land: requiredString
	})
)

export const validation = {
	aareg: ifPresent(
		'$aareg',
		Yup.array().of(
			Yup.object({
				ansettelsesPeriode: Yup.object({
					fom: requiredDate,
					tom: Yup.date().nullable()
				}),
				arbeidsforholdstype: requiredString,
				arbeidsgiver: Yup.object({
					aktoertype: requiredString,
					orgnummer: Yup.string().when('aktoertype', {
						is: 'ORG',
						then: Yup.string()
							.matches(/^[0-9]*$/, 'Orgnummer må være et tall med 9 sifre')
							.test('len', 'Orgnummer må være et tall med 9 sifre', val => val && val.length === 9)
					}),
					ident: Yup.string().when('aktoertype', {
						is: 'PERS',
						then: Yup.string()
							.matches(/^[0-9]*$/, 'Ident må være et tall med 11 sifre')
							.test('len', 'Ident må være et tall med 11 sifre', val => val && val.length === 11)
					})
				}),
				arbeidsavtale: Yup.object({
					yrke: requiredString,
					stillingsprosent: Yup.number()
						.min(1, 'Kan ikke være mindre enn 1')
						.max(100, 'Kan ikke være større enn 100')
						.required('Feltet er påkrevd'),
					endringsdatoStillingsprosent: Yup.date().nullable(),
					arbeidstidsordning: requiredString
				}),
				antallTimerForTimeloennet: ifPresent(
					'$aareg[0].antallTimerForTimeloennet',
					antallTimerForTimeloennet
				),
				permisjon: ifPresent('$aareg[0].permisjon', permisjon),
				utenlandsopphold: ifPresent('$aareg[0].utenlandsopphold', utenlandsopphold)
			})
		)
	)
}
