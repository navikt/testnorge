import * as Yup from 'yup'
import * as _ from 'lodash-es'
import { ifPresent, requiredNumber, requiredString } from '@/utils/YupValidations'
import { TjenestepensjonForm } from '@/components/fagsystem/tjenestepensjon/form/Form'
import { AlderspensjonForm } from '@/components/fagsystem/alderspensjon/form/Form'
import { UforetrygdForm } from '@/components/fagsystem/uforetrygd/form/Form'
import { PensjonsavtaleForm } from '@/components/fagsystem/pensjonsavtale/form/Form'
import { getYear } from 'date-fns'
import { AfpOffentligForm } from '@/components/fagsystem/afpOffentlig/form/Form'

function calculate_age(dob) {
	const diff_ms = Date.now() - dob.getTime()
	const age_dt = new Date(diff_ms)

	return Math.abs(age_dt.getUTCFullYear() - 1970)
}

export const getAlder = (values, personFoerLeggTil, importPersoner) => {
	let alder = _.get(values, 'pdldata.opprettNyPerson.alder')
	if (_.isNil(alder)) {
		let foedselsdato = null
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
				.map((foedsel) => foedsel.foedselsdato)
				.sort((a, b) => new Date(b) - new Date(a))
			foedselsdato = foedselsdatoer?.[0]
		} else if (personFoerLeggTil?.pdlforvalter?.person?.foedsel) {
			const foedselsdatoer = personFoerLeggTil.pdlforvalter.person.foedsel
				.map((foedsel) => foedsel.foedselsdato)
				.sort((a, b) => new Date(b) - new Date(a))
			foedselsdato = foedselsdatoer?.[0]
		} else if (personFoerLeggTil?.pdl) {
			const pdlPerson = personFoerLeggTil.pdl.hentPerson || personFoerLeggTil.pdl.person
			foedselsdato =
				pdlPerson?.foedselsdato?.[0]?.foedselsdato || pdlPerson?.foedsel?.[0]?.foedselsdato
		} else if (importPersoner) {
			const foedselsdatoer = importPersoner
				.map(
					(person) =>
						person?.data?.hentPerson?.foedselsdato?.[0]?.foedselsdato ||
						person?.data?.hentPerson?.foedsel?.[0]?.foedselsdato,
				)
				.sort((a, b) => new Date(b) - new Date(a))
			foedselsdato = foedselsdatoer?.[0]
		}
		if (!_.isNil(foedselsdato)) alder = calculate_age(new Date(foedselsdato))
	}
	return alder
}

const invalidAlderFom = (inntektFom, values, personFoerLeggTil, minAlder) => {
	const importPersoner = values.importPersoner
	const alder = getAlder(values, personFoerLeggTil, importPersoner)
	const foedtFoer = _.get(values, 'pdldata.opprettNyPerson.foedtFoer')
	const foedtEtter = _.get(values, 'pdldata.opprettNyPerson.foedtEtter')

	if (!_.isNil(alder) && alder !== '') {
		if (new Date().getFullYear() - alder + minAlder > inntektFom) {
			return true
		}
	} else if (!_.isNil(foedtFoer)) {
		const foedtFoerDate = new Date(foedtFoer)
		const day = foedtFoerDate.getDate()
		const month = foedtFoerDate.getMonth()
		let year = foedtFoerDate.getFullYear()

		year = day === 1 && month === 0 ? year - 1 : year
		if (year + minAlder || 13 > inntektFom) {
			return true
		}
	} else if (!_.isNil(foedtEtter) && _.isNil(foedtFoer)) {
		const foedtEtterDate = new Date(foedtEtter)
		if (foedtEtterDate.getFullYear() + minAlder || 13 > inntektFom) {
			return true
		}
	}
	return false
}

const invalidAlderTom = (inntektTom, values, personFoerLeggTil) => {
	const importPersoner = values?.importPersoner

	const alder = getAlder(values, personFoerLeggTil, importPersoner)
	const foedtFoer = _.get(values, 'pdldata.opprettNyPerson.foedtFoer')
	const foedtEtter = _.get(values, 'pdldata.opprettNyPerson.foedtEtter')
	if (!_.isNil(alder)) {
		if (inntektTom >= new Date().getFullYear() - alder + 75) {
			return true
		}
	} else if (!_.isNil(foedtEtter)) {
		const foedtEtterDate = new Date(foedtEtter)
		if (inntektTom >= foedtEtterDate.getFullYear() + 75) {
			return true
		}
	} else if (!_.isNil(foedtFoer)) {
		const foedtFoerDate = new Date(foedtFoer)
		if (foedtFoerDate.getFullYear() + 75 <= inntektTom) {
			return true
		}
	}
	return false
}

const invalidDoedsdato = (inntektTom, values, personFoerLeggTil) => {
	const importPersoner = values.importPersoner

	let doedsdato = values?.pdldata?.person?.doedsfall?.[0]?.doedsdato
	if (_.isNil(doedsdato)) {
		if (personFoerLeggTil?.pdlforvalter?.person?.doedsfall) {
			const doedsdatoer = personFoerLeggTil.pdlforvalter.person.doedsfall
				.map((doedsfall) => doedsfall.doedsdato)
				.sort((a, b) => new Date(a) - new Date(b))
			doedsdato = doedsdatoer?.[0]
		} else if (personFoerLeggTil?.pdl) {
			const pdlPerson = personFoerLeggTil.pdl.hentPerson || personFoerLeggTil.pdl.person
			const pdlDoedsdato = pdlPerson?.doedsfall?.[0]?.doedsdato
			if (pdlDoedsdato) doedsdato = pdlDoedsdato
		} else if (importPersoner) {
			const doedsdatoer = importPersoner
				.map((person) => person?.data?.hentPerson?.doedsfall?.[0]?.doedsdato)
				.sort((a, b) => new Date(a) - new Date(b))
			doedsdato = doedsdatoer?.[0]
		}
	}
	if (!_.isNil(doedsdato)) {
		const year = new Date(doedsdato).getFullYear()
		return year < inntektTom
	}
	return false
}

const validFomDateTest = (val: Yup.NumberSchema<number, Yup.AnyObject>) => {
	return val.test('gyldig-fom-aar', 'Feil', (value, context) => {
		if (!value) return true
		const inntektFom = value
		const values = context.from?.[context.from.length - 1]?.value
		const personFoerLeggTil = context?.options?.context?.personFoerLeggTil

		const alder = getAlder(values, personFoerLeggTil, values.importPersoner)
		const foedselsAar = alder && getYear(new Date()) - alder

		const minAlder = foedselsAar && foedselsAar < 1997 ? 17 : 13

		if (invalidAlderFom(inntektFom, values, personFoerLeggTil, minAlder)) {
			return context.createError({
				message: `F.o.m kan tidligst være året personen fyller ${minAlder.toString()} år`,
			})
		}

		let inntektTom = context.options.parent.tomAar
		if (!_.isNil(inntektTom) && inntektFom > inntektTom) {
			return context.createError({ message: 'F.o.m. dato må være før t.o.m. dato' })
		}

		return true
	})
}

const validTomDateTest = (val: Yup.NumberSchema<number, Yup.AnyObject>) => {
	return val.test('gyldig-tom-aar', 'Feil', (value, context) => {
		if (!value) return true
		let inntektTom = value

		const values = context.from?.[context.from.length - 1]?.value
		const personFoerLeggTil = context?.options?.context?.personFoerLeggTil

		if (invalidAlderTom(inntektTom, values, personFoerLeggTil)) {
			return context.createError({
				message: 'T.o.m kan ikke være etter året personen fyller 75',
			})
		}

		if (invalidDoedsdato(inntektTom, values, personFoerLeggTil)) {
			return context.createError({ message: 'T.o.m kan ikke være etter at person har dødd' })
		}

		const inntektFom = context.options.parent.fomAar
		if (!_.isNil(inntektFom) && inntektTom < inntektFom) {
			return context.createError({ message: 'T.o.m. dato må være etter f.o.m. dato' })
		}

		return true
	})
}

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
						.of(
							Yup.object({
								inntekt: requiredString,
							}),
						)
						.required('Generer minst én inntekt'),
				}),
			),
			...PensjonsavtaleForm.validation,
			...TjenestepensjonForm.validation,
			...AlderspensjonForm.validation,
			...UforetrygdForm.validation,
			...AfpOffentligForm.validation,
		}),
	),
}
