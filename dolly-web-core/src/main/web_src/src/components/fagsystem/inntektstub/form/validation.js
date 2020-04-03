import * as Yup from 'yup'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { requiredDate, requiredString, requiredNumber, messages } from '~/utils/YupValidations'

const innenforMaanedAarTest = validation => {
	const errorMsg = 'Dato må være innenfor måned/år for denne inntektsinformasjonen'

	return validation.test('range', errorMsg, function isWithinMonth(val) {
		if (!val) return true

		const dateValue = new Date(val)
		const path = this.path
		const values = this.options.context

		const dateValueMaanedAar = `${dateValue.getFullYear()}-${(
			'0' +
			(dateValue.getMonth() + 1)
		).slice(-2)}`

		const inntektsinformasjonPath = path.split('.', 2).join('.')
		const inntektsinformasjonMaanedAar = _get(values, `${inntektsinformasjonPath}.sisteAarMaaned`)

		if (!inntektsinformasjonMaanedAar || dateValueMaanedAar === inntektsinformasjonMaanedAar) {
			return true
		}
		return false
	})
}

const inntektsliste = Yup.array().of(
	Yup.object({
		beloep: requiredNumber.typeError(messages.required),
		startOpptjeningsperiode: innenforMaanedAarTest(Yup.string().nullable()),
		sluttOpptjeningsperiode: innenforMaanedAarTest(Yup.string().nullable())
	})
)

const fradragsliste = Yup.array().of(
	Yup.object({
		beloep: requiredNumber.typeError(messages.required),
		beskrivelse: requiredString
	})
)

const forskuddstrekksliste = Yup.array().of(
	Yup.object({
		beloep: requiredNumber.typeError(messages.required),
		beskrivelse: Yup.string()
	})
)

const arbeidsforholdsliste = Yup.array().of(
	Yup.object({
		arbeidsforholdstype: requiredString,
		startdato: Yup.mixed().when('sluttdato', {
			is: val => val !== undefined,
			then: requiredDate
		}),
		sluttdato: Yup.string().nullable(),
		antallTimerPerUkeSomEnFullStillingTilsvarer: Yup.number()
			.transform((i, j) => (j === '' ? null : i))
			.nullable(),
		avloenningstype: Yup.string().nullable(),
		yrke: Yup.string().nullable(),
		arbeidstidsordning: Yup.string().nullable(),
		stillingsprosent: Yup.number()
			.transform((i, j) => (j === '' ? null : i))
			.nullable(),
		sisteLoennsendringsdato: Yup.string().nullable(),
		sisteDatoForStillingsprosentendring: Yup.string().nullable()
	})
)

export const validation = {
	inntektstub: Yup.object({
		inntektsinformasjon: Yup.array().of(
			Yup.object({
				sisteAarMaaned: requiredString,
				antallMaaneder: Yup.number()
					.integer('Kan ikke være et desimaltall')
					.transform((i, j) => (j === '' ? null : i))
					.nullable(),
				virksomhet: requiredString.typeError(messages.required),
				opplysningspliktig: requiredString,
				inntektsliste: inntektsliste,
				fradragsliste: fradragsliste,
				forskuddstrekksliste: forskuddstrekksliste,
				arbeidsforholdsliste: arbeidsforholdsliste
			})
		)
	})
}
