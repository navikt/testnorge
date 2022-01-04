import * as Yup from 'yup'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { getMonth, getYear, isWithinInterval } from 'date-fns'
import { ifPresent, messages, requiredDate, requiredString } from '~/utils/YupValidations'

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
				const fomMonth = _get(values, fomPath)
				if (
					getMonth(dateValue) !== getMonth(new Date(fomMonth)) ||
					getYear(dateValue) !== getYear(new Date(fomMonth))
				)
					return false
			}
			const arrayPos = _get(values, 'aareg[0].amelding')
				? `aareg[0].amelding[${ameldingIndex}].arbeidsforhold[${arbeidsforholdIndex}]`
				: `aareg[${aaregIndex}]`

			const ansattFom = _get(values, `${arrayPos}.ansettelsesPeriode.fom`)
			const ansattTom = _get(values, `${arrayPos}.ansettelsesPeriode.tom`)

			return isWithinInterval(dateValue, {
				start: new Date(ansattFom),
				end: _isNil(ansattTom) ? new Date() : new Date(ansattTom),
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
			const arbeidsforholdType = _get(values, `aareg[${index}].arbeidsforholdstype`)
			if (fullArbeidsforholdTyper.some((value) => value === arbeidsforholdType) && !val) {
				gyldig = false
			}
			return gyldig
		}
	)
}

const ansettelsesPeriode = Yup.object({
	fom: requiredDate,
	tom: Yup.date().nullable(),
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
	ansettelsesform: Yup.string(),
	stillingsprosent: Yup.number().typeError(messages.required),
	endringsdatoStillingsprosent: Yup.date().nullable(),
	sisteLoennsendringsdato: Yup.date().nullable(),
	arbeidstidsordning: fullArbeidsforholdTest(Yup.string()),
	avtaltArbeidstimerPerUke: fullArbeidsforholdTest(
		Yup.number()
			.min(1, 'Kan ikke være mindre enn ${min}')
			.max(75, 'Kan ikke være større enn ${max}')
			.typeError(messages.required)
	),
})

const fartoy = Yup.array().of(
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
			ansettelsesPeriode: ifPresent('$aareg[0].arbeidsgiver', ansettelsesPeriode),
			arbeidsforholdstype: requiredString,
			arbeidsforholdID: Yup.string().nullable(),
			arbeidsgiver: ifPresent('$aareg[0].arbeidsgiver', arbeidsgiver),
			arbeidsavtale: ifPresent('$aareg[0].arbeidsgiver', arbeidsavtale),
			fartoy: ifPresent('$aareg[0].fartoy', fartoy),
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
				'$aareg[0].amelding',
				Yup.object({
					fom: requiredPeriode,
					tom: requiredPeriode,
				})
			),
		})
	),
}
