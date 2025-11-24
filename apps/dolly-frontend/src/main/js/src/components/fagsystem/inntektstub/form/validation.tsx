import * as Yup from 'yup'
import { addDays, areIntervalsOverlapping, subMonths } from 'date-fns'
import {
	ifPresent,
	messages,
	requiredDate,
	requiredNumber,
	requiredString,
} from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'

const unikOrgMndTest = (unikValidation: Yup.StringSchema<string, Yup.AnyObject>) => {
	const errorMsg = 'Kombinasjonen av år, måned og virksomhet er ikke unik'
	return unikValidation.test('unikhet', errorMsg, (orgnr, testContext) => {
		if (!orgnr) return true
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value

		const alleInntekter = fullForm.inntektstub?.inntektsinformasjon
		const currInntektsinformasjon = testContext.parent
		if (!currInntektsinformasjon?.sisteAarMaaned) return true

		return !nyeInntekterOverlapper(alleInntekter, currInntektsinformasjon)
	})
}

const nyeInntekterOverlapper = (alleInntekter, currInntektsinformasjon) => {
	const virksomheter = alleInntekter.map((inntektinfo) => inntektinfo.virksomhet)
	const maaneder = alleInntekter.map((inntektinfo) => ({
		sisteAarMaaned: inntektinfo.sisteAarMaaned,
		antallMaaneder: inntektinfo.antallMaaneder,
	}))

	const likeOrgnrIndex = indexOfLikeOrgnr(virksomheter, currInntektsinformasjon.virksomhet)
	//Hvis ingen orgnr er like trenger vi ikke sjekke datoer. Hvis orgnr er like -> finn måneder
	if (likeOrgnrIndex.length < 2) return false

	const tidsrom = finnTidsrom(maaneder)
	return finnesOverlappendeDato(tidsrom, likeOrgnrIndex)
}

const indexOfLikeOrgnr = (virksomheter, orgnr) => {
	const index = []
	virksomheter.forEach((virksomhet, idx) => virksomhet === orgnr && index.push(idx))
	return index
}

const dato = (aarMaaned) => {
	const year = aarMaaned.split('-')[0]
	const month = aarMaaned.split('-')[1]
	return new Date(year, month - 1)
}

const finnTidsrom = (maaneder) => maaneder.map((maaned) => getInterval(maaned))

const getInterval = (inntektsinformasjon) => {
	const currDato = dato(inntektsinformasjon.sisteAarMaaned)
	return inntektsinformasjon.antallMaaneder && inntektsinformasjon.antallMaaneder > 1
		? {
				start: subMonths(currDato, inntektsinformasjon.antallMaaneder - 1),
				end: currDato,
			}
		: {
				start: currDato,
				end: addDays(currDato, 1),
			}
}

const finnesOverlappendeDato = (tidsrom, index) => {
	const tidsromSomIkkeKanOverlappe = index.map((idx) => tidsrom[idx])
	const firstInterval = tidsromSomIkkeKanOverlappe[0]

	return tidsromSomIkkeKanOverlappe.some((t, idx) => {
		if (idx === 0) return //Tester mot første tidsrom
		return areIntervalsOverlapping(
			{ start: firstInterval.start, end: addDays(firstInterval.end, 1) },
			{ start: t.start, end: t.end },
		)
	})
}

const inntektsliste = Yup.array().of(
	Yup.object().shape(
		{
			beloep: requiredNumber.typeError(messages.required),
			inntektstype: requiredString,
			startOpptjeningsperiode: testDatoFom(Yup.string().nullable(), 'sluttOpptjeningsperiode'),
			sluttOpptjeningsperiode: testDatoTom(Yup.string().nullable(), 'startOpptjeningsperiode'),
			inngaarIGrunnlagForTrekk: Yup.boolean().required(messages.required),
			utloeserArbeidsgiveravgift: Yup.boolean().required(messages.required),
			fordel: ifPresent('fordel', requiredString),
			antall: ifPresent('$antall', requiredString),
			beskrivelse: ifPresent('beskrivelse', requiredString),
		},
		[
			['fordel', 'fordel'],
			['beskrivelse', 'beskrivelse'],
		],
	),
)

const fradragsliste = Yup.array().of(
	Yup.object({
		beloep: requiredNumber.typeError(messages.required),
		beskrivelse: requiredString,
	}),
)

const forskuddstrekksliste = Yup.array().of(
	Yup.object({
		beloep: requiredNumber.typeError(messages.required),
		beskrivelse: Yup.string().nullable(),
	}),
)

const arbeidsforholdsliste = Yup.array().of(
	Yup.object({
		arbeidsforholdstype: requiredString,
		startdato: testDatoFom(
			Yup.mixed().when('sluttdato', {
				is: (val) => val !== undefined,
				then: () => requiredDate,
			}),
			'sluttdato',
		),
		sluttdato: testDatoTom(Yup.string().nullable(), 'startdato'),
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
		sisteDatoForStillingsprosentendring: Yup.string().nullable(),
	}),
)

export const validation = {
	inntektstub: ifPresent(
		'$inntektstub',
		Yup.object({
			inntektsinformasjon: Yup.array().of(
				Yup.object({
					sisteAarMaaned: requiredString.matches(/^\d{4}-(0[1-9]|1[012])$/, {
						message: 'Dato må være på formatet yyyy-MM',
						excludeEmptyString: true,
					}),
					antallMaaneder: Yup.number()
						.integer('Kan ikke være et desimaltall')
						.transform((i, j) => (j === '' ? null : i))
						.min(1, 'Antall måneder må være et positivt tall')
						.max(500, 'Antall måneder kan maksimalt være 500')
						.nullable(),
					virksomhet: unikOrgMndTest(requiredString.typeError(messages.required)).nullable(),
					opplysningspliktig: requiredString,
					inntektsliste: inntektsliste,
					fradragsliste: fradragsliste,
					forskuddstrekksliste: forskuddstrekksliste,
					arbeidsforholdsliste: arbeidsforholdsliste,
					historikk: Yup.array().of(
						Yup.object({
							inntektsliste: inntektsliste,
							fradragsliste: fradragsliste,
							forskuddstrekksliste: forskuddstrekksliste,
							arbeidsforholdsliste: arbeidsforholdsliste,
						}),
					),
				}),
			),
		}),
	),
}
