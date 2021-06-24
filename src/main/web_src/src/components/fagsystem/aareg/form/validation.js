import * as Yup from 'yup'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { isWithinInterval, getMonth } from 'date-fns'
import { ifPresent, requiredDate, requiredString, messages } from '~/utils/YupValidations'
import { yupToFormErrors } from 'formik'

const innenforAnsettelsesforholdTest = (validation, validateFomMonth) => {
	const errorMsg = 'Dato må være innenfor ansettelsesforhold'
	const errorMsgMonth =
		'Dato må være innenfor ansettelsesforhold, og i samme kalendermåned og år som fra-dato'
	return validation.test(
		'range',
		validateFomMonth ? errorMsgMonth : errorMsg,
		function isWithinTest(val) {
			if (!val) return true

			// Husk at dato som kommer fra en Mal kan være av typen String
			const dateValue = new Date(val)
			const path = this.path
			const values = this.options.context

			if (validateFomMonth) {
				const fomPath = path.replace('.tom', '.fom')
				const fomMonth = _get(values, fomPath)
				if (getMonth(dateValue) !== getMonth(new Date(fomMonth))) return false
			}

			const arrayPos = path.split('.')[0] // feks: aareg[1]

			const ansattFom = _get(values, `${arrayPos}.ansettelsesPeriode.fom`)
			const ansattTom = _get(values, `${arrayPos}.ansettelsesPeriode.tom`)

			return isWithinInterval(dateValue, {
				start: new Date(ansattFom),
				end: _isNil(ansattTom) ? new Date() : new Date(ansattTom)
			})
		}
	)
}

const antallTimerForTimeloennet = Yup.array().of(
	Yup.object({
		periode: Yup.object({
			fom: innenforAnsettelsesforholdTest(requiredDate),
			tom: innenforAnsettelsesforholdTest(requiredDate, true)
		}),
		antallTimer: Yup.number()
			.min(1, 'Kan ikke være mindre enn ${min}')
			.typeError(messages.required)
	})
)

const utenlandsopphold = Yup.array().of(
	Yup.object({
		periode: Yup.object({
			fom: innenforAnsettelsesforholdTest(requiredDate),
			tom: innenforAnsettelsesforholdTest(Yup.date().nullable(), true)
		}),
		land: requiredString
	})
)

const permisjon = Yup.array().of(
	Yup.object({
		permisjonsPeriode: Yup.object({
			fom: innenforAnsettelsesforholdTest(requiredDate),
			tom: innenforAnsettelsesforholdTest(Yup.date().nullable())
		}),
		permisjonsprosent: Yup.number()
			.min(1, 'Kan ikke være mindre enn ${min}')
			.max(100, 'Kan ikke være større enn ${max}')
			.typeError(messages.required),
		permisjon: requiredString
	})
)

const permittering = Yup.array().of(
	Yup.object({
		permitteringsPeriode: Yup.object({
			fom: innenforAnsettelsesforholdTest(requiredDate),
			tom: innenforAnsettelsesforholdTest(Yup.date().nullable())
		}),
		permitteringsprosent: Yup.number()
			.min(1, 'Kan ikke være mindre enn ${min}')
			.max(100, 'Kan ikke være større enn ${max}')
			.typeError(messages.required)
	})
)

const arbeidsforhold = Yup.array().of(
	Yup.object({
		ansettelsesPeriode: Yup.object({
			fom: requiredDate,
			tom: Yup.date().nullable()
		}),
		arbeidsforholdstype: requiredString, // TODO: Maa settes i formikbag ved togglechange
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
			ansettelsesform: requiredString,
			stillingsprosent: Yup.number()
				.min(0, 'Kan ikke være mindre enn ${min}')
				.max(100, 'Kan ikke være større enn ${max}')
				.typeError(messages.required),
			endringsdatoStillingsprosent: Yup.date().nullable(),
			endringsdatoLoenn: Yup.date().nullable(),
			arbeidstidsordning: requiredString,
			avtaltArbeidstimerPerUke: Yup.number()
				.transform((i, j) => (j === '' ? null : i))
				.nullable()
				.min(1, 'Kan ikke være mindre enn ${min}')
				.max(75, 'Kan ikke være større enn ${max}')
		}),
		antallTimerForTimeloennet: antallTimerForTimeloennet,
		utenlandsopphold: utenlandsopphold,
		permisjon: permisjon,
		permittering: permittering
	})
)

const amelding = Yup.array().of(
	Yup.object({
		arbeidsforhold: arbeidsforhold
	})
)

export const validation = {
	aareg: Yup.array().of(
		Yup.object({
			arbeidsforhold: ifPresent('$aareg[0].arbeidsforhold', arbeidsforhold),
			amelding: ifPresent('$aareg[0].amelding', amelding),
			// arbeidsforholdstype: ifPresent('$aareg[0].amelding', requiredString)
			arbeidsforholdstype: requiredString
		})
	)
}
