import * as Yup from 'yup'
import * as _ from 'lodash-es'
import { isAfter, isEqual, isWithinInterval } from 'date-fns'
import { ifPresent, messages, requiredDate, requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

const innenforAnsettelsesforholdTest = (periodeValidation) => {
	const errorMsg = 'Dato må være innenfor ansettelsesforhold'

	return periodeValidation.test('range', errorMsg, (val, testContext) => {
		if (!val) return true
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value

		// Husk at dato som kommer fra en Mal kan være av typen String
		const dateValue = new Date(val)
		const gjeldendeArbeidsforholdPath = testContext.path.substring(0, testContext.path.indexOf('.'))

		const ansattFom = _.get(fullForm, `${gjeldendeArbeidsforholdPath}.ansettelsesPeriode.fom`)
		const ansattTom = _.get(fullForm, `${gjeldendeArbeidsforholdPath}.ansettelsesPeriode.tom`)

		if (_.isNil(ansattTom)) {
			return isEqual(dateValue, new Date(ansattFom)) || isAfter(dateValue, new Date(ansattFom))
		}

		return isWithinInterval(dateValue, {
			start: new Date(ansattFom),
			end: new Date(ansattTom),
		})
	})
}

const fullArbeidsforholdTest = (arbeidsforholdValidation) => {
	const fullArbeidsforholdTyper = ['', 'ordinaertArbeidsforhold', 'maritimtArbeidsforhold']
	return arbeidsforholdValidation.test('isRequired', 'Feltet er påkrevd', (val, testContext) => {
		let gyldig = true
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value
		const index = testContext.options.index
		const arbeidsforholdType = _.get(fullForm, `aareg[${index}].arbeidsforholdstype`)
		if (fullArbeidsforholdTyper.some((value) => value === arbeidsforholdType) && !val) {
			gyldig = false
		}
		return gyldig
	})
}

const ansettelsesPeriode = Yup.object({
	fom: testDatoFom(requiredDate.nullable(), 'tom'),
	tom: testDatoTom(Yup.date().nullable(), 'fom'),
	sluttaarsak: Yup.string().nullable(),
})

const arbeidsgiver = Yup.object({
	aktoertype: requiredString,
	orgnummer: Yup.string().when('aktoertype', {
		is: 'ORG',
		then: () =>
			Yup.string()
				.matches(/^\d*$/, 'Orgnummer må være et tall med 9 sifre')
				.test('len', 'Orgnummer må være et tall med 9 sifre', (val) => val?.length === 9),
	}),
	ident: Yup.string().when('aktoertype', {
		is: 'PERS',
		then: () =>
			requiredString
				.matches(/^\d*$/, 'Ident må være et tall med 11 sifre')
				.test('len', 'Ident må være et tall med 11 sifre', (val) => val?.length === 11),
	}),
})

const arbeidsavtale = Yup.object({
	yrke: fullArbeidsforholdTest(requiredString),
	ansettelsesform: Yup.string().nullable(),
	stillingsprosent: Yup.number().typeError(messages.required).nullable(),
	endringsdatoStillingsprosent: Yup.date().nullable(),
	sisteLoennsendringsdato: Yup.date().nullable(),
	arbeidstidsordning: fullArbeidsforholdTest(Yup.string()).nullable(),
	avtaltArbeidstimerPerUke: ifPresent(
		'$avtaltArbeidstimerPerUke',
		Yup.string()
			.test('isBetween', 'Må være mellom 1 og 75', (val, context) => {
				const value = _.toNumber(val)
				return !val || isNaN(value) || (value >= 1 && value <= 75)
			})
			.nullable(),
	),
})

const fartoy = Yup.array()
	.of(
		Yup.object({
			skipsregister: requiredString,
			skipstype: requiredString,
			fartsomraade: requiredString,
		}),
	)
	.nullable()

export const validation = {
	aareg: ifPresent(
		'$aareg',
		Yup.array().of(
			Yup.object({
				ansettelsesPeriode: ifPresent('$aareg[0].arbeidsgiver.aktoertype', ansettelsesPeriode),
				arbeidsforholdstype: requiredString,
				arbeidsforholdId: Yup.string().nullable(),
				arbeidsgiver: ifPresent('$aareg[0].arbeidsgiver.aktoertype', arbeidsgiver),
				arbeidsavtale: ifPresent('$aareg[0].arbeidsgiver.aktoertype', arbeidsavtale),
				fartoy: Yup.mixed().when({
					is: (exists) => !!exists,
					then: () => fartoy,
				}),
				antallTimerForTimeloennet: Yup.array().of(
					Yup.object({
						periode: Yup.object({
							fom: innenforAnsettelsesforholdTest(requiredDate),
							tom: innenforAnsettelsesforholdTest(requiredDate),
						}),
						antallTimer: Yup.number()
							.min(1, 'Kan ikke være mindre enn ${min}')
							.typeError(messages.required),
					}),
				),
				utenlandsopphold: Yup.array().of(
					Yup.object({
						periode: Yup.object({
							fom: innenforAnsettelsesforholdTest(requiredDate),
							tom: innenforAnsettelsesforholdTest(requiredDate),
						}),
						land: requiredString,
					}),
				),
				permisjon: Yup.array().of(
					Yup.object({
						permisjonsPeriode: Yup.object({
							fom: innenforAnsettelsesforholdTest(requiredDate),
							tom: innenforAnsettelsesforholdTest(Yup.date().nullable()),
						}),
						permisjonsprosent: Yup.number()
							.min(1, 'Kan ikke være mindre enn ${min}')
							.max(100, 'Kan ikke være større enn ${max}')
							.typeError(messages.required),
						permisjon: requiredString,
					}),
				),
				permittering: Yup.array().of(
					Yup.object({
						permitteringsPeriode: Yup.object({
							fom: innenforAnsettelsesforholdTest(requiredDate),
							tom: innenforAnsettelsesforholdTest(Yup.date().nullable()),
						}),
						permitteringsprosent: Yup.number()
							.min(1, 'Kan ikke være mindre enn ${min}')
							.max(100, 'Kan ikke være større enn ${max}')
							.typeError(messages.required),
					}),
				),
			}),
		),
	),
}
