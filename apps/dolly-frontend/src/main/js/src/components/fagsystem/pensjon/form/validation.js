import * as Yup from 'yup'
import _get from 'lodash/get'
import { ifPresent, requiredNumber } from '~/utils/YupValidations'

function calculate_age(dob) {
	const diff_ms = Date.now() - dob.getTime()
	const age_dt = new Date(diff_ms)

	return Math.abs(age_dt.getUTCFullYear() - 1970)
}

const getAlder = (values, personFoerLeggTil, importPersoner) => {
	let alder = _get(values, 'pdldata.opprettNyPerson.alder')

	let foedselsdato = null
	if (personFoerLeggTil?.tpsf?.foedselsdato) {
		foedselsdato = personFoerLeggTil.tpsf.foedselsdato
	} else if (personFoerLeggTil?.pdl) {
		const pdlPerson = personFoerLeggTil.pdl.hentPerson || personFoerLeggTil.pdl.person
		foedselsdato = pdlPerson?.foedsel?.[0]?.foedselsdato
	} else if (importPersoner) {
		const foedselsdatoer = importPersoner
			.map((person) => person?.data?.hentPerson?.foedsel?.[0]?.foedselsdato)
			.sort((a, b) => new Date(b) - new Date(a))
		foedselsdato = foedselsdatoer?.[0]
	}
	if (foedselsdato) alder = calculate_age(new Date(foedselsdato))
	return alder
}

const getDoedsdato = (values, personFoerLeggTil, importPersoner) => {
	let doedsdato = values?.pdldata?.person?.doedsfall?.[0]?.doedsdato
	if (!doedsdato) {
		if (personFoerLeggTil?.tpsf?.doedsdato) {
			doedsdato = values.personFoerLeggTil.tpsf.doedsdato
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
	return doedsdato
}

const validFomDateTest = (validation) => {
	return validation.test('gyldig-fom-aar', function isWithinTest(val) {
		if (!val) return true
		const inntektFom = val

		const path = this.path.substring(0, this.path.lastIndexOf('.'))
		const values = this.options.context
		const personFoerLeggTil = values.personFoerLeggTil
		const importPersoner = values.importPersoner

		let alderFeil = false
		const alder = getAlder(values, personFoerLeggTil, importPersoner)
		const foedtFoer = _get(values, 'pdldata.opprettNyPerson.foedtFoer')
		const foedtEtter = _get(values, 'pdldata.opprettNyPerson.foedtEtter')
		if (alder) {
			if (new Date().getFullYear() - alder + 18 > inntektFom) {
				alderFeil = true
			}
		} else if (foedtFoer) {
			const foedtFoerDate = new Date(foedtFoer)
			const day = foedtFoerDate.getDate()
			const month = foedtFoerDate.getMonth()
			let year = foedtFoerDate.getFullYear()

			year = day === 1 && month === 0 ? year - 1 : year

			if (year + 18 > inntektFom) {
				alderFeil = true
			}
		} else if (foedtEtter && !foedtFoer) {
			const foedtEtterDate = new Date(foedtEtter)
			if (foedtEtterDate.getFullYear() + 18 > inntektFom) {
				alderFeil = true
			}
		} else {
			if (new Date().getFullYear() - 12 > inntektFom) {
				alderFeil = true
			}
		}

		if (alderFeil) {
			return this.createError({
				message: 'F.o.m kan tidligst være året personen fyller 18 år.',
			})
		}

		let inntektTom = _get(values, `${path}.tomAar`)
		if (inntektTom && inntektFom > inntektTom) {
			return this.createError({ message: 'F.o.m. dato må være før t.o.m. dato' })
		}

		return true
	})
}

const validTomDateTest = (validation) => {
	return validation.test('gyldig-tom-aar', function isWithinTest(val) {
		if (!val) return true
		let inntektTom = val

		const path = this.path.substring(0, this.path.lastIndexOf('.'))
		const values = this.options.context
		const personFoerLeggTil = values.personFoerLeggTil
		const importPersoner = values.importPersoner

		const doedsdato = getDoedsdato(values, personFoerLeggTil, importPersoner)
		if (doedsdato) {
			const year = new Date(doedsdato).getFullYear()
			if (year < inntektTom) {
				return this.createError({ message: 'T.o.m kan ikke være etter at person har dødd.' })
			}
		}

		const inntektFom = _get(values, `${path}.fomAar`)
		if (inntektTom < inntektFom) {
			return this.createError({ message: 'T.o.m. dato må være etter f.o.m. dato' })
		}

		return true
	})
}

export const validation = {
	pensjonforvalter: ifPresent(
		'$pensjonforvalter',
		Yup.object({
			inntekt: Yup.object({
				fomAar: validFomDateTest(requiredNumber),
				tomAar: validTomDateTest(requiredNumber).typeError('Velg et gyldig år'),
				belop: Yup.number()
					.min(0, 'Tast inn et gyldig beløp')

					.typeError('Tast inn et gyldig beløp'),
				redusertMedGrunnbelop: Yup.boolean(),
			}),
		})
	),
}
