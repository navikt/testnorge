import * as Yup from 'yup'
import { ifPresent, messages, requiredNumber, requiredString } from '@/utils/YupValidations'
import { testDatoFom, testDatoTom } from '@/components/fagsystem/utils'
import { finnUgyldigeFelter } from '@/components/fagsystem/inntektstub/validerInntekt/gyldigeVerdier'
import { Inntektsinformasjon } from '@/components/fagsystem/inntektstub/form/partials/inntektstubTypes'

const unikOrgMndTest = (unikValidation: Yup.StringSchema<string, Yup.AnyObject>) => {
	const errorMsg = 'Kombinasjonen av år, måned og virksomhet er ikke unik'
	return unikValidation.test('unikhet', errorMsg, (orgnr, testContext) => {
		if (!orgnr) return true
		const fullForm = testContext.from && testContext.from[testContext.from.length - 1]?.value

		const alleInntekter = fullForm?.inntekter ?? []
		const currInntektsinformasjon = testContext.parent
		if (!currInntektsinformasjon?.perioder?.length) return true

		return !nyeInntekterOverlapper(alleInntekter, currInntektsinformasjon)
	})
}

const nyeInntekterOverlapper = (
	alleInntekter: Inntektsinformasjon[],
	currInntektsinformasjon: Inntektsinformasjon,
) => {
	const antallOverlappendeInntekter = alleInntekter.filter(
		(inntektinfo) =>
			inntektinfo?.virksomhet === currInntektsinformasjon.virksomhet &&
			harFellesPeriode(inntektinfo?.perioder, currInntektsinformasjon.perioder),
	).length
	return antallOverlappendeInntekter > 1
}

const harFellesPeriode = (perioder: string[] = [], andrePerioder: string[] = []) =>
	perioder.some((periode) => andrePerioder.includes(periode))

const inntektsliste = Yup.array().of(
	Yup.object()
		.shape({
			beloep: requiredNumber.typeError(messages.required),
			inntektstype: requiredString,
			beskrivelse: requiredString,
			startOpptjeningsperiode: testDatoFom(Yup.string().nullable(), 'sluttOpptjeningsperiode'),
			sluttOpptjeningsperiode: testDatoTom(Yup.string().nullable(), 'startOpptjeningsperiode'),
		})
		.test('gyldigeVerdier', messages.required, function (inntekt) {
			const ugyldigeFelter = finnUgyldigeFelter(inntekt)
			if (ugyldigeFelter.length === 0) {
				return true
			}
			return new Yup.ValidationError(
				ugyldigeFelter.map((feltPath) =>
					this.createError({ path: `${this.path}.${feltPath}`, message: messages.required }),
				),
			)
		}),
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

export const validation = {
	inntekter: ifPresent(
		'$inntekter',
		Yup.array().of(
			Yup.object({
				perioder: Yup.array()
					.of(Yup.string())
					.min(1, 'Velg minst én måned')
					.required('Velg minst én måned'),
				rapporteringsdato: Yup.date().nullable(),
				virksomhet: unikOrgMndTest(requiredString.typeError(messages.required)).nullable(),
				opplysningspliktig: requiredString,
				inntektsliste: inntektsliste,
				fradragsliste: fradragsliste,
				forskuddstrekksliste: forskuddstrekksliste,
			}),
		),
	),
}
