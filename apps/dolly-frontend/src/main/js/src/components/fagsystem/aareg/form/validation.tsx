import * as Yup from 'yup'
import * as _ from 'lodash-es'
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
		function isWithinTest(val) {
			if (!val) return true

			// Husk at dato som kommer fra en Mal kan være av typen String
			const dateValue = new Date(val)
			const path = this.path
			const values = this.options.context
			const aaregIndex = parseInt(path.match(/\d+/g)[0])
			const ameldingIndex = parseInt(path.match(/\d+/g)[1])
			const arbeidsforholdIndex = this.options.index

			if (validateFomMonth) {
				const fomPath = path.replace('.tom', '.fom')
				const fomMonth = _.get(values, fomPath)
				if (
					getMonth(dateValue) !== getMonth(new Date(fomMonth)) ||
					getYear(dateValue) !== getYear(new Date(fomMonth))
				)
					return false
			}

			const amelding = _.get(values, 'aareg[0].amelding')
			const arrayPos =
				amelding && amelding?.length > 0
					? `aareg[0].amelding[${ameldingIndex}].arbeidsforhold[${arbeidsforholdIndex}]`
					: `aareg[${aaregIndex}]`

			const ansattFom = _.get(values, `${arrayPos}.ansettelsesPeriode.fom`)
			const ansattTom = _.get(values, `${arrayPos}.ansettelsesPeriode.tom`)

			return isWithinInterval(dateValue, {
				start: new Date(ansattFom),
				end: _.isNil(ansattTom) ? new Date() : new Date(ansattTom),
			})
		}
	)
}

const fullArbeidsforholdTest = (arbeidsforholdValidation) => {
	const fullArbeidsforholdTyper = ['', 'ordinaertArbeidsforhold', 'maritimtArbeidsforhold']
	return arbeidsforholdValidation.test(
		'isRequired',
		'Feltet er påkrevd',
		function checkRequired(val) {
			let gyldig = true
			const values = this.options.context
			const index = this.options.index
			const arbeidsforholdType = _.get(values, `aareg[${index}].arbeidsforholdstype`)
			if (fullArbeidsforholdTyper.some((value) => value === arbeidsforholdType) && !val) {
				gyldig = false
			}
			return gyldig
		}
	)
}

const ansettelsesPeriode = Yup.object({
	fom: testDatoFom(requiredDate, 'tom'),
	tom: testDatoTom(Yup.date().nullable(), 'fom'),
	sluttaarsak: Yup.string().nullable(),
})

const arbeidsgiver = Yup.object({
	aktoertype: requiredString,
	orgnummer: Yup.string().when('aktoertype', {
		is: 'ORG',
		then: Yup.string()
			.matches(/^\d*$/, 'Orgnummer må være et tall med 9 sifre')
			.test('len', 'Orgnummer må være et tall med 9 sifre', (val) => val && val.length === 9),
	}),
	ident: Yup.string().when('aktoertype', {
		is: 'PERS',
		then: Yup.string()
			.matches(/^\d*$/, 'Ident må være et tall med 11 sifre')
			.test('len', 'Ident må være et tall med 11 sifre', (val) => val && val.length === 11),
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
			.typeError(messages.required)
	).nullable(),
})

const fartoy = Yup.array()
	.nullable()
	.of(
		Yup.object({
			skipsregister: requiredString,
			skipstype: requiredString,
			fartsomraade: requiredString,
		})
	)

const requiredPeriode = Yup.mixed()
	.when('$aareg[0].arbeidsforholdstype', {
		is: 'frilanserOppdragstakerHonorarPersonerMm',
		then: requiredDate,
	})
	.when('$aareg[0].arbeidsforholdstype', {
		is: 'maritimtArbeidsforhold',
		then: requiredDate,
	})
	.when('$aareg[0].arbeidsforholdstype', {
		is: 'ordinaertArbeidsforhold',
		then: requiredDate,
	})
	.nullable()

export const validation = {
	aareg: Yup.array().of(
		Yup.object({
			ansettelsesPeriode: ifPresent('$aareg[0].arbeidsgiver.aktoertype', ansettelsesPeriode),
			arbeidsforholdstype: requiredString,
			arbeidsforholdID: Yup.string().nullable(),
			arbeidsgiver: ifPresent('$aareg[0].arbeidsgiver.aktoertype', arbeidsgiver),
			arbeidsavtale: ifPresent('$aareg[0].arbeidsgiver.aktoertype', arbeidsavtale),
			fartoy: ifPresent('$aareg[0].fartoy.skipstype', fartoy),
			antallTimerForTimeloennet: Yup.array().of(
				Yup.object({
					periode: Yup.object({
						fom: innenforAnsettelsesforholdTest(requiredDate),
						tom: innenforAnsettelsesforholdTest(requiredDate, true),
					}),
					antallTimer: Yup.number()
						.min(1, 'Kan ikke være mindre enn ${min}')
						.typeError(messages.required),
				})
			),
			utenlandsopphold: Yup.array().of(
				Yup.object({
					periode: Yup.object({
						fom: innenforAnsettelsesforholdTest(requiredDate),
						tom: innenforAnsettelsesforholdTest(requiredDate, true),
					}),
					land: requiredString,
				})
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
				})
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
				})
			),
			amelding: ifPresent(
				'$aareg[0].amelding',
				Yup.array().of(
					Yup.object({
						arbeidsforhold: Yup.array().of(
							Yup.object({
								ansettelsesPeriode: ansettelsesPeriode,
								arbeidsforholdID: Yup.string().nullable(),
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
									})
								),
								utenlandsopphold: Yup.array().of(
									Yup.object({
										periode: Yup.object({
											fom: requiredDate,
											tom: requiredDate,
										}),
										land: requiredString,
									})
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
									})
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
									})
								),
							})
						),
					})
				)
			),
			genererPeriode: ifPresent(
				'$aareg[0].amelding[0]',
				Yup.object({
					fom: testDatoFom(requiredPeriode, 'tom'),
					tom: testDatoTom(requiredPeriode, 'fom'),
				})
			),
		})
	),
}
