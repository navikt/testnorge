import * as Yup from 'yup'
import * as _ from 'lodash-es'
import { ifPresent, requiredNumber, requiredString } from '@/utils/YupValidations'
import { TjenestepensjonForm } from '@/components/fagsystem/tjenestepensjon/form/Form'
import { AlderspensjonForm } from '@/components/fagsystem/alderspensjon/form/Form'
import { UforetrygdForm } from '@/components/fagsystem/uforetrygd/form/Form'
import { PensjonsavtaleForm } from '@/components/fagsystem/pensjonsavtale/form/Form'
import { AfpOffentligForm } from '@/components/fagsystem/afpOffentlig/form/Form'
import { AlderspensjonNyUttaksgradForm } from '@/components/fagsystem/alderspensjon/form/AlderspensjonNyUttaksgradForm'

interface RootValues {
	pdldata?: any
	importPersoner?: any[]
	[k: string]: any
}

interface PersonFoerLeggTil {
	pdlforvalter?: any
	pdl?: any
	[k: string]: any
}

function calculate_age(dob: Date): number {
	const diffMs = Date.now() - dob.getTime()
	const ageDate = new Date(diffMs)
	return Math.abs(ageDate.getUTCFullYear() - 1970)
}

export const getAlder = (
	values: RootValues,
	personFoerLeggTil?: PersonFoerLeggTil,
	importPersoner?: any[],
): number | undefined => {
	let alder: unknown =
		_.get(values, 'pdldata.opprettNyPerson.alder') ??
		_.get(values, 'pdldata.person.alder') ??
		_.get(values, 'alder')

	if (typeof alder === 'string') {
		const trimmed = alder.trim()
		if (trimmed.length === 0) {
			alder = undefined
		} else {
			const parsed = Number(trimmed)
			alder = Number.isNaN(parsed) ? undefined : parsed
		}
	}

	if (typeof alder !== 'number') {
		let foedselsdato: string | number | null = null

		if (values?.pdldata?.person?.foedselsdato?.[0]?.foedselsdato) {
			foedselsdato = values.pdldata.person.foedselsdato[0].foedselsdato
		} else if (values?.pdldata?.person?.foedsel?.[0]?.foedselsdato) {
			foedselsdato = values.pdldata.person.foedsel[0].foedselsdato
		} else if (values?.pdldata?.person?.foedselsdato?.[0]?.foedselsaar) {
			foedselsdato = new Date().setFullYear(values.pdldata.person.foedselsdato[0].foedselsaar)
		} else if (values?.pdldata?.opprettNyPerson?.foedtFoer) {
			foedselsdato = values.pdldata.opprettNyPerson.foedtFoer
		} else if (personFoerLeggTil?.pdlforvalter?.person?.foedselsdato) {
			const foedselsdatoer = personFoerLeggTil.pdlforvalter.person.foedselsdato
				.map((f: any) => f.foedselsdato)
				.filter(Boolean)
				.sort((a: string, b: string) => new Date(b).getTime() - new Date(a).getTime())

			foedselsdato = foedselsdatoer?.[0] ?? null
		} else if (personFoerLeggTil?.pdlforvalter?.person?.foedsel) {
			const foedselsdatoer = personFoerLeggTil.pdlforvalter.person.foedsel
				.map((f: any) => f.foedselsdato)
				.filter(Boolean)
				.sort((a: string, b: string) => new Date(b).getTime() - new Date(a).getTime())

			foedselsdato = foedselsdatoer?.[0] ?? null
		} else if (personFoerLeggTil?.pdl) {
			const pdlPerson = personFoerLeggTil.pdl.hentPerson || personFoerLeggTil.pdl.person

			foedselsdato =
				pdlPerson?.foedselsdato?.[0]?.foedselsdato || pdlPerson?.foedsel?.[0]?.foedselsdato || null
		} else if (importPersoner?.length) {
			const foedselsdatoer = importPersoner
				.map(
					(p: any) =>
						p?.data?.hentPerson?.foedselsdato?.[0]?.foedselsdato ||
						p?.data?.hentPerson?.foedsel?.[0]?.foedselsdato,
				)
				.filter(Boolean)
				.sort((a: string, b: string) => new Date(b).getTime() - new Date(a).getTime())

			foedselsdato = foedselsdatoer?.[0] ?? null
		}

		if (foedselsdato != null) {
			const d = new Date(foedselsdato)
			if (!isNaN(d.getTime())) {
				alder = calculate_age(d)
			}
		}
	}

	return typeof alder === 'number' && !Number.isNaN(alder) ? alder : undefined
}

const validFomDateTest = (schema: Yup.NumberSchema<number, Yup.AnyObject>) =>
	schema.test('gyldig-fom-aar', 'Feil', (value, ctx) => {
		if (value == null) return true
		const values: RootValues | undefined = ctx.from?.[ctx.from.length - 1]?.value
		const personFoerLeggTil = ctx?.options?.context?.personFoerLeggTil
		const alder = values ? getAlder(values, personFoerLeggTil, values.importPersoner) : undefined

		if (typeof alder !== 'number') {
			return true
		}

		const birthYear = new Date().getFullYear() - alder
		const minAlder = birthYear < 1997 ? 17 : 13
		const earliestYear = birthYear + minAlder
		if (value < earliestYear) {
			return ctx.createError({
				message: `F.o.m kan tidligst være året personen fyller ${minAlder} år`,
			})
		}

		const tom = (ctx.options.parent as any)?.tomAar
		if (tom != null && value > tom) {
			return ctx.createError({ message: 'F.o.m. dato må være før t.o.m. dato' })
		}
		return true
	})

const validTomDateTest = (schema: Yup.NumberSchema<number, Yup.AnyObject>) =>
	schema.test('gyldig-tom-aar', 'Feil', (value, ctx) => {
		if (value == null) return true
		const fom = (ctx.options.parent as any)?.fomAar
		if (fom != null && value < fom) {
			return ctx.createError({ message: 'T.o.m. dato må være etter f.o.m. dato' })
		}
		return true
	})

export const validation = {
	pensjonforvalter: ifPresent(
		'$pensjonforvalter',
		Yup.object({
			inntekt: ifPresent(
				'$pensjonforvalter.inntekt',
				Yup.object({
					fomAar: validFomDateTest(requiredNumber),
					tomAar: validTomDateTest(requiredNumber).typeError('Velg et gyldig år'),
					belop: Yup.number()
						.min(0, 'Tast inn et gyldig beløp')
						.typeError('Tast inn et gyldig beløp'),
					redusertMedGrunnbelop: Yup.boolean(),
				}),
			),
			generertInntekt: ifPresent(
				'$pensjonforvalter.generertInntekt',
				Yup.object({
					generer: Yup.object({
						tomAar: validTomDateTest(requiredNumber).required('Velg et gyldig år'),
					}),
					inntekter: Yup.array()
						.of(Yup.object({ inntekt: requiredString }))
						.required('Generer minst én inntekt'),
				}),
			),
			...PensjonsavtaleForm.validation,
			...TjenestepensjonForm.validation,
			...AlderspensjonForm.validation,
			...AlderspensjonNyUttaksgradForm.validation,
			...UforetrygdForm.validation,
			...AfpOffentligForm.validation,
		}),
	),
}
