import * as Yup from 'yup'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { requiredDate, requiredString, requiredNumber, messages } from '~/utils/YupValidations'

const inntektsliste = Yup.array().of(
	Yup.object({
		beloep: requiredNumber.typeError(messages.required),
		startOpptjeningsperiode: Yup.string().nullable(),
		sluttOpptjeningsperiode: Yup.string().nullable()

		// inntektstype: requiredString.typeError(messages.required)
		// inngaarIGrunnlagForTrekk: null,
		// utloeserArbeidsgiveravgift: null,
		// fordel: '',
		// skatteOgAvgiftsregel: '',
		// skattemessigBosattILand: '',
		// opptjeningsland: '',
		// beskrivelse: '', REQUIRED
		// tilleggsinformasjon: {
		//     bilOgBaat: {},
		//     bonusFraForsvaret: {
		//         aaretUtbetalingenGjelderFor: ''
		//     },
		//     dagmammaIEgenBolig: {},
		//     etterbetalingsperiode: {
		//         startdato: null,
		//         sluttdato: null
		//     },
		//     inntektPaaNorskKontinentalsokkel: {},
		//     livrente: {},
		//     lottOgPart: {},
		//     nettoloenn: {},
		//     pensjon: {
		//         grunnpensjonsbeloep: '',
		//         heravEtterlattepensjon: '',
		//         pensjonsgrad: '',
		//         tidsrom: {
		//             startdato: null,
		//             sluttdato: null
		//         },
		//         tilleggspensjonsbeloep: '',
		//         ufoeregrad: ''
		//     },
		//     reiseKostOgLosji: {
		//         persontype: ''
		//     },
		//     inntjeningsforhold: {
		//         inntjeningsforhold: ''
		//     },
		//     utenlandskArtist: {}
		// },
		// antall: ''
	})
)

const fradragsliste = Yup.array().of(
	Yup.object({
		beloep: requiredNumber.nullable(),
		beskrivelse: requiredString
	})
)

const forskuddstrekksliste = Yup.array().of(
	Yup.object({
		beloep: requiredNumber.nullable(),
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
		sluttdato: Yup.date(),
		antallTimerPerUkeSomEnFullStillingTilsvarer: Yup.number().nullable(),
		avloenningstype: Yup.string(),
		yrke: Yup.string(),
		arbeidstidsordning: Yup.string(),
		stillingsprosent: Yup.number().nullable(),
		sisteLoennsendringsdato: Yup.date(),
		sisteDatoForStillingsprosentendring: Yup.date()
	})
)

export const validation = {
	inntektstub: Yup.object({
		// prosentOekningPerAaar: Yup.number()
		// 	.transform((i, j) => (j === '' ? null : i))
		// 	.nullable(),
		inntektsinformasjon: Yup.array().of(
			Yup.object({
				startAarMaaned: requiredString,
				antallMaaneder: Yup.number()
					.integer('Kan ikke være et desimaltall')
					.transform((i, j) => (j === '' ? null : i))
					.nullable(),
				opplysningspliktig: requiredString,
				virksomhet: requiredString,
				inntektsliste: inntektsliste,
				fradragsliste: fradragsliste,
				forskuddstrekksliste: forskuddstrekksliste,
				arbeidsforholdsliste: arbeidsforholdsliste
			})
		)
	})
}
