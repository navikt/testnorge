import * as Yup from 'yup'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { requiredDate, requiredString, requiredNumber, messages } from '~/utils/YupValidations'

const inntektsliste = Yup.array().of(
	Yup.object({
		beloep: requiredNumber.typeError(messages.required),
		startOpptjeningsperiode: Yup.string().nullable(),
		sluttOpptjeningsperiode: Yup.string().nullable()
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
		avloenningstype: Yup.string(),
		yrke: Yup.string(),
		arbeidstidsordning: Yup.string(),
		stillingsprosent: Yup.number()
			.transform((i, j) => (j === '' ? null : i))
			.nullable(),
		sisteLoennsendringsdato: Yup.string().nullable(),
		sisteDatoForStillingsprosentendring: Yup.string().nullable()
	})
)

export const validation = {
	inntektstub: Yup.object({
		// prosentOekningPerAaar: Yup.number()
		// 	.transform((i, j) => (j === '' ? null : i))
		// 	.nullable(),
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
