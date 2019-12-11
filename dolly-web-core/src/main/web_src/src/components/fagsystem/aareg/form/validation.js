import * as Yup from 'yup'
import { isWithinInterval, getMonth } from 'date-fns'
import { requiredDate, requiredString, ifPresent } from '~/utils/YupValidations'

const antallTimerForTimeloennet = Yup.array().of(
	Yup.object({
		periode: Yup.object({
			fom: Yup.date()
				.required('Feltet er påkrevd')
				.test('range', 'Dato må være innenfor ansettelsesforhold', function isWithinTest(val) {
					const values = this.options.context
					return isWithinInterval(val, {
						start: values.aareg[0].ansettelsesPeriode.fom,
						end: values.aareg[0].ansettelsesPeriode.tom || new Date()
					})
				}),
			tom: Yup.date()
				.required('Feltet er påkrevd')
				.test(
					'range',
					'Dato må være innenfor ansettelsesforhold, og i samme kalendermåned og år som fra-dato',
					function isWithinTest(val) {
						const values = this.options.context
						if (
							getMonth(val) !== getMonth(values.aareg[0].antallTimerForTimeloennet[0].periode.fom)
						)
							return false
						return isWithinInterval(val, {
							start: values.aareg[0].ansettelsesPeriode.fom,
							end: values.aareg[0].ansettelsesPeriode.tom || new Date()
						})
					}
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
			fom: Yup.date()
				.required('Feltet er påkrevd')
				.test('range', 'Dato må være innenfor ansettelsesforhold', function isWithinTest(val) {
					const values = this.options.context
					return isWithinInterval(val, {
						start: values.aareg[0].ansettelsesPeriode.fom,
						end: values.aareg[0].ansettelsesPeriode.tom || new Date()
					})
				}),
			tom: Yup.date()
				.test('range', 'Dato må være innenfor ansettelsesforhold', function isWithinTest(val) {
					if (!val) return true
					const values = this.options.context
					return isWithinInterval(val, {
						start: values.aareg[0].ansettelsesPeriode.fom,
						end: values.aareg[0].ansettelsesPeriode.tom || new Date()
					})
				})
				.nullable()
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
			fom: Yup.date()
				.required('Feltet er påkrevd')
				.test('range', 'Dato må være innenfor ansettelsesforhold', function isWithinTest(val) {
					const values = this.options.context
					return isWithinInterval(val, {
						start: values.aareg[0].ansettelsesPeriode.fom,
						end: values.aareg[0].ansettelsesPeriode.tom || new Date()
					})
				}),
			tom: Yup.date()
				.test(
					'range',
					'Dato må være innenfor ansettelsesforhold, og i samme kalendermåned og år som fra-dato',
					function isWithinTest(val) {
						const values = this.options.context
						if (!val) return true
						if (getMonth(val) !== getMonth(values.aareg[0].utenlandsopphold[0].periode.fom))
							return false
						return isWithinInterval(val, {
							start: values.aareg[0].ansettelsesPeriode.fom,
							end: values.aareg[0].ansettelsesPeriode.tom || new Date()
						})
					}
				)
				.nullable()
		}),
		land: requiredString
	})
)

export const validation = {
	aareg: Yup.array().of(
		Yup.object({
			ansettelsesPeriode: Yup.object({
				fom: requiredDate,
				tom: Yup.date().nullable()
			}),
			arbeidsforholdstype: Yup.string(),
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
}
