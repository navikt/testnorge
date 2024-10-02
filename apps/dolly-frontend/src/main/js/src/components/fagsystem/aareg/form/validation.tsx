import * as Yup from 'yup'
import _ from 'lodash'
import { getMonth, getYear, isWithinInterval } from 'date-fns'
import { ifPresent, messages, requiredDate, requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

const innenforAnsettelsesforholdTest = (periodeValidation, validateFomMonth) => {
	const errorMsg = 'Dato må være innenfor ansettelsesforhold'
	const errorMsgMonth =
		'Dato må være innenfor ansettelsesforhold, og i samme kalendermåned og år som fra-dato'
	return periodeValidation.test(
		'range',
		validateFomMonth ? errorMsgMonth : errorMsg,
		(val, testContext) => {
			if (!val) return true
			const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value

			// Husk at dato som kommer fra en Mal kan være av typen String
			const dateValue = new Date(val)
			const gjeldendeArbeidsforholdPath = testContext.path.substring(
				0,
				testContext.path.indexOf('.'),
			)

			if (validateFomMonth) {
				const fomPath = testContext.path.replace('.tom', '.fom')
				const fomMonth = _.get(fullForm, fomPath)
				if (
					getMonth(dateValue) !== getMonth(new Date(fomMonth)) ||
					getYear(dateValue) !== getYear(new Date(fomMonth))
				)
					return false
			}

			const ansattFom = _.get(fullForm, `${gjeldendeArbeidsforholdPath}.ansettelsesPeriode.fom`)
			const ansattTom = _.get(fullForm, `${gjeldendeArbeidsforholdPath}.ansettelsesPeriode.tom`)

			return isWithinInterval(dateValue, {
				start: new Date(ansattFom),
				end: _.isNil(ansattTom) ? new Date() : new Date(ansattTom),
			})
		},
	)
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
	avtaltArbeidstimerPerUke: fullArbeidsforholdTest(
		Yup.number()
			.min(1, 'Kan ikke være mindre enn ${min}')
			.max(75, 'Kan ikke være større enn ${max}')
			.typeError(messages.required),
	).nullable(),
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

const requiredPeriode = Yup.mixed()
	.when('$aareg[0].arbeidsforholdstype', {
		is: 'frilanserOppdragstakerHonorarPersonerMm',
		then: () => requiredDate,
	})
	.when('$aareg[0].arbeidsforholdstype', {
		is: 'maritimtArbeidsforhold',
		then: () => requiredDate,
	})
	.when('$aareg[0].arbeidsforholdstype', {
		is: 'ordinaertArbeidsforhold',
		then: () => requiredDate,
	})
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
							tom: innenforAnsettelsesforholdTest(requiredDate, true),
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
							tom: innenforAnsettelsesforholdTest(requiredDate, true),
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
				amelding: ifPresent(
					'$aareg[0].amelding',
					Yup.array().of(
						Yup.object({
							arbeidsforhold: Yup.array().of(
								Yup.object({
									ansettelsesPeriode: ansettelsesPeriode,
									arbeidsforholdId: Yup.string().nullable(),
									arbeidsgiver: arbeidsgiver,
									arbeidsavtale: arbeidsavtale,
									fartoy: fartoy,
									antallTimerForTimeloennet: Yup.array().of(
										Yup.object({
											periode: Yup.object({
												fom: requiredDate,
												tom: requiredDate,
											}),
											antallTimer: Yup.number()
												.min(1, 'Kan ikke være mindre enn ${min}')
												.typeError(messages.required),
										}),
									),
									utenlandsopphold: Yup.array().of(
										Yup.object({
											periode: Yup.object({
												fom: requiredDate,
												tom: requiredDate,
											}),
											land: requiredString,
										}),
									),
									permisjon: Yup.array().of(
										Yup.object({
											permisjonsPeriode: Yup.object({
												fom: requiredDate,
												tom: Yup.date().nullable(),
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
												fom: requiredDate,
												tom: Yup.date().nullable(),
											}),
											permitteringsprosent: Yup.number()
												.min(1, 'Kan ikke være mindre enn ${min}')
												.max(100, 'Kan ikke være større enn ${max}')
												.typeError(messages.required),
										}),
									),
								}),
							),
						}),
					),
				),
				genererPeriode: ifPresent(
					'$aareg[0].amelding[0]',
					Yup.object({
						fom: testDatoFom(requiredDate.nullable(), 'tom'),
						tom: testDatoTom(requiredDate.nullable(), 'fom'),
					}).nullable(),
				),
			}),
		),
	),
}
