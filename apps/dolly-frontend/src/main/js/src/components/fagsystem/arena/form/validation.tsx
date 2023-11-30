import * as Yup from 'yup'
import { messages, requiredString } from '@/utils/YupValidations'
import { isAfter, isBefore, isEqual } from 'date-fns'
import * as _ from 'lodash-es'

const ikkeOverlappendeVedtak = ['aap', 'dagpenger']

const datoIkkeMellom = (nyDatoFra, gjeldendeDatoFra, gjeldendeDatoTil) => {
	if (!gjeldendeDatoFra || !gjeldendeDatoTil) return true
	return (
		isAfter(new Date(nyDatoFra), new Date(gjeldendeDatoTil)) ||
		isBefore(new Date(nyDatoFra), new Date(gjeldendeDatoFra))
	)
}

export const getFoedselsdatoer = (values) => {
	const personFoerLeggTil = values?.personFoerLeggTil
	const importPersoner = values?.importPersoner
	if (values?.pdldata?.person?.foedsel?.[0]?.foedselsdato) {
		return [values.pdldata.person.foedsel[0].foedselsdato]
	} else if (personFoerLeggTil?.pdlforvalter?.person?.foedsel) {
		const foedselsdatoer = personFoerLeggTil.pdlforvalter.person.foedsel
			.map((foedsel) => foedsel.foedselsdato)
			.sort((a, b) => new Date(b) - new Date(a))
		return [foedselsdatoer?.[0]]
	} else if (personFoerLeggTil?.pdl) {
		const pdlPerson = personFoerLeggTil.pdl.hentPerson || personFoerLeggTil.pdl.person
		return [pdlPerson?.foedsel?.[0]?.foedselsdato]
	} else if (importPersoner) {
		return importPersoner.map((person) => person?.data?.hentPerson?.foedsel?.[0]?.foedselsdato)
	}
	return []
}

const getFoedtFoer = (alder) => {
	let foedtFoer = new Date()
	foedtFoer.setFullYear(foedtFoer.getFullYear() - alder)
	foedtFoer.setMonth(foedtFoer.getMonth() - 3)
	return foedtFoer
}

const getFoedtEtter = (alder) => {
	let foedtEtter = new Date()
	foedtEtter.setFullYear(foedtEtter.getFullYear() - alder - 1)
	return foedtEtter
}

// Vedtak/støtte må deles opp i vedtak til fylte 25 år og vedtak etter fylte 25 år.
const overlapp25aarsdag = (fradato, tildato, values) => {
	let foedtFoer = _.get(values, 'pdldata.opprettNyPerson.foedtFoer')
	foedtFoer = foedtFoer ? new Date(foedtFoer) : null
	let foedtEtter = _.get(values, 'pdldata.opprettNyPerson.foedtEtter')
	foedtEtter = foedtEtter ? new Date(foedtEtter) : null

	let alder = _.get(values, 'pdldata.opprettNyPerson.alder')
	if (!_.isNil(alder)) {
		foedtFoer = getFoedtFoer(alder)
		foedtEtter = getFoedtEtter(alder)
	}

	if (_.isNil(foedtFoer) && _.isNil(foedtEtter)) {
		const foedselsdatoer = getFoedselsdatoer(values)
		const foedselsaar = _.get(values, 'pdldata.person.foedsel[0].foedselsaar')
		if (foedselsdatoer?.length > 0) {
			for (let fdato of foedselsdatoer) {
				let tjuefem = new Date(fdato)
				tjuefem.setFullYear(tjuefem.getFullYear() + 25)
				if (isBefore(fradato, tjuefem) && !isBefore(tildato, tjuefem)) {
					return true
				}
			}
			return false
		} else if (!_.isNil(foedselsaar)) {
			foedtEtter = new Date(foedselsaar, 0, 1)
			foedtFoer = new Date(foedselsaar, 11, 31)
		} else {
			foedtEtter = getFoedtEtter(60)
			foedtFoer = getFoedtFoer(30)
		}
	}

	if (_.isNil(foedtFoer)) {
		foedtEtter.setFullYear(foedtEtter.getFullYear() + 25)
		return isAfter(fradato, foedtEtter) || isAfter(tildato, foedtEtter)
	} else if (_.isNil(foedtEtter)) {
		foedtFoer.setFullYear(foedtFoer.getFullYear() + 25)
		return isBefore(fradato, foedtFoer) || isBefore(tildato, foedtFoer)
	} else {
		foedtEtter.setFullYear(foedtEtter.getFullYear() + 25)
		foedtFoer.setFullYear(foedtFoer.getFullYear() + 25)
		return overlapperMedliste(
			fradato.toISOString().substring(0, 19),
			tildato.toISOString().substring(0, 19),
			[
				{
					fraDato: foedtEtter.toISOString().substring(0, 19),
					tilDato: foedtFoer.toISOString().substring(0, 19),
				},
			],
		)
	}
}

// Vedtak/støtte må opphøre ved fylt 67.
const erEtter67aarsdag = (fradato, tildato, values) => {
	let foedtFoer = _.get(values, 'pdldata.opprettNyPerson.foedtFoer')
	foedtFoer = foedtFoer ? new Date(foedtFoer) : null
	let foedtEtter = _.get(values, 'pdldata.opprettNyPerson.foedtEtter')
	foedtEtter = foedtEtter ? new Date(foedtEtter) : null

	let alder = _.get(values, 'pdldata.opprettNyPerson.alder')
	if (!_.isNil(alder)) {
		foedtFoer = getFoedtFoer(alder)
		foedtEtter = getFoedtEtter(alder)
	}

	if (_.isNil(foedtFoer) && _.isNil(foedtEtter)) {
		const foedselsdatoer = getFoedselsdatoer(values)
		const foedselsaar = _.get(values, 'pdldata.person.foedsel[0].foedselsaar')
		if (foedselsdatoer?.length > 0) {
			for (let fdato of foedselsdatoer) {
				let sisteDag = new Date(fdato)
				sisteDag.setFullYear(sisteDag.getFullYear() + 67)
				if (!isBefore(fradato, sisteDag) || isAfter(tildato, sisteDag)) {
					return true
				}
			}
			return false
		} else if (!_.isNil(foedselsaar)) {
			let tidligsteDato = new Date(foedselsaar + 67, 0, 1)
			return !isBefore(fradato, tidligsteDato) || isAfter(tildato, tidligsteDato)
		} else {
			let tidligsteDato = new Date()
			tidligsteDato.setFullYear(tidligsteDato.getFullYear() + 6)
			return !isBefore(fradato, tidligsteDato) || isAfter(tildato, tidligsteDato)
		}
	} else if (_.isNil(foedtEtter)) {
		foedtFoer.setFullYear(foedtFoer.getFullYear() + 67)
		return isBefore(fradato, foedtFoer) || isBefore(tildato, foedtFoer)
	} else {
		foedtEtter.setFullYear(foedtEtter.getFullYear() + 67)
		foedtEtter.setDate(foedtEtter.getDate() + 1)
		return !isBefore(fradato, foedtEtter) || isAfter(tildato, foedtEtter)
	}
}

const validTildato = (fradato, tildato) => {
	if (!fradato || !tildato) return true
	return isAfter(new Date(tildato), new Date(fradato))
}

const ingenOverlappFraTildato = (tildato, values) => {
	const fraDato = values.arenaforvalter.dagpenger?.[0]?.fraDato
	const aapFraDato = values.arenaforvalter.aap?.[0]?.fraDato

	if (tildato || !fraDato) return true

	if (isBefore(new Date(fraDato), new Date(aapFraDato))) return false

	if (values.tidligereBestillinger) {
		const arenaBestillinger = values.tidligereBestillinger.filter((bestilling) =>
			bestilling.data.hasOwnProperty('arenaforvalter'),
		)
		for (let bestilling of arenaBestillinger) {
			let arenaInfo = bestilling.data.arenaforvalter
			for (let key of ikkeOverlappendeVedtak) {
				if (arenaInfo[key]?.length > 0) {
					const fraDatoBestilling = arenaInfo[key]?.[0]?.fraDato
					if (isBefore(new Date(fraDato), new Date(fraDatoBestilling))) return false
				}
			}
		}
	}
	return true
}

const invalidDoedsdato = (arenaFom, values) => {
	const personFoerLeggTil = values.personFoerLeggTil
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
		return isAfter(new Date(arenaFom), new Date(doedsdato))
	}
	return false
}

const validFradato = (vedtakType) => {
	return Yup.string()
		.test(
			'har-gjeldende-vedtak',
			'AAP- og Dagpenger-vedtak kan ikke overlappe hverandre',
			function validVedtak() {
				const values = this.options.context
				const naavaerendeVerdier = {}
				for (let key of ikkeOverlappendeVedtak) {
					naavaerendeVerdier[key] = {
						fraDato: values.arenaforvalter[key]?.[0]?.fraDato,
						tilDato: values.arenaforvalter[key]?.[0]?.tilDato,
					}
				}
				// Hvis det bare er en type vedtak trengs det ikke å sjekkes videre
				if (!naavaerendeVerdier.dagpenger?.fraDato || !naavaerendeVerdier.aap?.fraDato) return true
				let annenVedtakType = vedtakType === 'aap' ? 'dagpenger' : 'aap'
				return datoIkkeMellom(
					naavaerendeVerdier[vedtakType]?.fraDato,
					naavaerendeVerdier[annenVedtakType]?.fraDato,
					naavaerendeVerdier[annenVedtakType]?.tilDato,
				)
			},
		)
		.test(
			'tiltak-fra-etter-doedsdato',
			'Ident kan ikke ha tiltak etter dødsfall',
			function validDate(value) {
				const values = this.options.context
				return !invalidDoedsdato(value, values)
			},
		)
		.nullable()
		.required(messages.required)
}

const overlapperMedliste = (originalFradato, orginialTildato, vedtakListe) => {
	for (let vedtak of vedtakListe) {
		const fraDato = vedtak?.fraDato
		const tilDato = vedtak?.tilDato

		if (
			fraDato &&
			(!datoIkkeMellom(originalFradato, fraDato, tilDato) ||
				!datoIkkeMellom(fraDato, originalFradato, orginialTildato))
		) {
			return true
		}
		if (fraDato && !tilDato && isBefore(new Date(fraDato), new Date(originalFradato))) return true
	}
	return false
}

export const validation = Yup.object({
	aap: Yup.array().of(
		Yup.object({
			fraDato: validFradato('aap'),
			tilDato: Yup.string()
				.test('etter-fradato', 'Til-dato må være etter fra-dato', function validDate(tildato) {
					const fradato = this.options.context.arenaforvalter.aap[0]?.fraDato
					return validTildato(fradato, tildato)
				})
				.test(
					'overlapper-ikke-25',
					'Vedtak kan ikke overlappe dato person fyller 25',
					function validDate(tildato) {
						const values = this.options.context
						const fradato = this.options.context.arenaforvalter.aap[0]?.fraDato
						return !overlapp25aarsdag(new Date(fradato), new Date(tildato), values)
					},
				)
				.test(
					'avslutter-ved-67',
					'Person kan ikke ha vedtak etter fylte 67 år',
					function validDate(tildato) {
						const values = this.options.context
						const fradato = this.options.context.arenaforvalter.aap[0]?.fraDato
						return !erEtter67aarsdag(new Date(fradato), new Date(tildato), values)
					},
				)
				.test(
					'tiltak-til-etter-doedsdato',
					'Ident kan ikke ha tiltak etter dødsfall',
					function validDate(value) {
						const values = this.options.context
						return !invalidDoedsdato(value, values)
					},
				)
				.nullable()
				.required(messages.required),
		}),
	),
	aap115: Yup.array().of(
		Yup.object({
			fraDato: Yup.string()
				.test(
					'avslutter-ved-67',
					'Person kan ikke ha vedtak etter fylte 67 år',
					function validDate(fradato) {
						const values = this.options.context
						return !erEtter67aarsdag(new Date(fradato), null, values)
					},
				)
				.test(
					'tiltak-fra-etter-doedsdato',
					'Ident kan ikke ha tiltak etter dødsfall',
					function validDate(value) {
						const values = this.options.context
						return !invalidDoedsdato(value, values)
					},
				)
				.nullable()
				.required(messages.required),
		}),
	),
	arenaBrukertype: requiredString,
	aktiveringDato: Yup.mixed()
		.test('er-paakrevd', 'Feltet er påkrevd', function isRequired(dato) {
			const values = this.options.context
			if (values.personFoerLeggTil && values.personFoerLeggTil.arenaforvalteren) return true
			const { arenaforvalter } = values
			const ingenYtelser =
				arenaforvalter?.arenaBrukertype === 'MED_SERVICEBEHOV' &&
				!arenaforvalter?.aap115 &&
				!arenaforvalter?.aap &&
				!arenaforvalter?.dagpenger
			return !(ingenYtelser && !dato)
		})
		.test(
			'aktivert-etter-doedsdato',
			'Ident kan ikke aktiveres etter dødsfall',
			function validDate(value) {
				const values = this.options.context
				return !invalidDoedsdato(value, values)
			},
		)
		.nullable(),
	inaktiveringDato: Yup.mixed()
		.test(
			'er-etter-aktiveringsdato',
			'Dato må være etter aktiveringsdato',
			function isAfterAktivering(dato) {
				const arenaValues = this.options.context?.arenaforvalter
				if (arenaValues.inaktiveringDato === undefined) {
					return true
				}
				if (arenaValues?.arenaBrukertype === 'UTEN_SERVICEBEHOV' && !dato) {
					return this.createError({
						message: 'Feltet er påkrevd',
					})
				}
				return (
					!arenaValues?.aktiveringDato ||
					isEqual(new Date(dato), new Date(arenaValues.aktiveringDato)) ||
					isAfter(new Date(dato), new Date(arenaValues.aktiveringDato))
				)
			},
		)
		.nullable(),
	automatiskInnsendingAvMeldekort: Yup.boolean().nullable(),
	kvalifiseringsgruppe: Yup.string()
		.test('har-verdi', messages.required, function validKvalifiseringsgruppe(gruppe) {
			const values = this.options.context
			if (values.arenaforvalter.arenaBrukertype === 'UTEN_SERVICEBEHOV') return true
			if (values.personFoerLeggTil && values.personFoerLeggTil.arenaforvalteren) return true

			return gruppe
		})
		.nullable(),
	dagpenger: Yup.array().of(
		Yup.object({
			rettighetKode: Yup.string().required(messages.required),
			fraDato: validFradato('dagpenger'),
			tilDato: Yup.string()
				.test('etter-fradato', 'Til-dato må være etter fra-dato', function validDate(tildato) {
					if (!tildato) {
						return true
					}
					const fradato = this.options.context.arenaforvalter.dagpenger[0]?.fraDato
					return validTildato(fradato, tildato)
				})
				.test(
					'skaper-ikke-overlapp',
					'Manglende til-dato skaper overlapp med annet vedtak',
					function validDate(tildato) {
						if (!tildato) {
							return true
						}
						return ingenOverlappFraTildato(tildato, this.options.context)
					},
				)
				.test(
					'overlapper-ikke-25',
					'Vedtak kan ikke overlappe dato person fyller 25',
					function validDate(tildato) {
						if (!tildato) {
							return true
						}
						const values = this.options.context
						const fradato = this.options.context.arenaforvalter.dagpenger[0]?.fraDato
						return !overlapp25aarsdag(new Date(fradato), new Date(tildato), values)
					},
				)
				.test(
					'avslutter-ved-67',
					'Person kan ikke ha vedtak etter fylte 67 år',
					function validDate(tildato) {
						if (!tildato) {
							return true
						}
						const values = this.options.context
						const fradato = this.options.context.arenaforvalter.dagpenger[0]?.fraDato
						return !erEtter67aarsdag(new Date(fradato), new Date(tildato), values)
					},
				)
				.test(
					'tiltak-fra-etter-doedsdato',
					'Ident kan ikke ha tiltak etter dødsfall',
					function validDate(value) {
						const values = this.options.context
						return !invalidDoedsdato(value, values)
					},
				)
				.nullable(),
			mottattDato: Yup.date().nullable(),
		}),
	),
})
