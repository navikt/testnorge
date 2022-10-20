import * as Yup from 'yup'
import { messages, requiredDate, requiredString } from '~/utils/YupValidations'
import { isAfter, isBefore } from 'date-fns'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'

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
	} else if (personFoerLeggTil?.tpsf?.foedselsdato) {
		return [personFoerLeggTil.tpsf.foedselsdato]
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

const getFoedtFoer = (values) => {
	let foedtFoer = _get(values, 'pdldata.opprettNyPerson.foedtFoer')
	let alder = _get(values, 'pdldata.opprettNyPerson.alder')
	if (!_isNil(alder)) {
		foedtFoer = Date.now()
		foedtFoer.setFullYear(foedtFoer.getFullYear() - alder)
		foedtFoer.setMonth(foedtFoer.getMonth() - 3)
	}
	return foedtFoer
}

const getFoedtEtter = (values) => {
	let foedtEtter = _get(values, 'pdldata.opprettNyPerson.foedtEtter')
	let alder = _get(values, 'pdldata.opprettNyPerson.alder')
	if (!_isNil(alder)) {
		foedtEtter = Date.now()
		foedtEtter.setFullYear(foedtEtter.getFullYear() - alder - 1)
	}
	return foedtEtter
}

// Vedtak/støtte må deles opp i vedtak til fylte 25 år og vedtak etter fylte 25 år.
const overlapp25aarsdag = (fradato, tildato, values) => {
	const foedtFoer = getFoedtFoer(values)
	const foedtEtter = getFoedtEtter(values)

	if (_isNil(foedtFoer) && _isNil(foedtEtter)) {
		const foedselsdatoer = getFoedselsdatoer(values)
		if (foedselsdatoer?.length > 0) {
			for (let fdato of foedselsdatoer) {
				let tjuefem = new Date(fdato)
				tjuefem.setFullYear(tjuefem.getFullYear() + 25)
				if (isBefore(fradato, tjuefem) && !isBefore(tildato, tjuefem)) {
					return true
				}
			}
		}
	} else if (!_isNil(foedtEtter) && _isNil(foedtFoer)) {
		foedtEtter.setFullYear(foedtEtter.getFullYear() + 25)
		return isAfter(fradato, foedtEtter) || isAfter(tildato, foedtEtter)
	} else if (!_isNil(foedtFoer) && _isNil(foedtEtter)) {
		foedtFoer.setFullYear(foedtFoer.getFullYear() + 25)
		return isBefore(fradato, foedtFoer) || isBefore(tildato, foedtFoer)
	} else if (!_isNil(foedtFoer) && !_isNil(foedtEtter)) {
		foedtEtter.setFullYear(foedtEtter.getFullYear() + 25)
		foedtFoer.setFullYear(foedtFoer.getFullYear() + 25)
		return overlapperMedliste(fradato, tildato, [
			{
				fraDato: foedtEtter,
				tilDato: foedtFoer,
			},
		])
	} else {
		// hvis ingen alder satt
	}
	return false
}

// Vedtak/støtte må opphøre ved fylt 67.
const erEtter67aarsdag = (fradato, tildato, values) => {
	const foedtFoer = getFoedtFoer(values)
	const foedtEtter = getFoedtEtter(values)
	if (_isNil(foedtFoer) && _isNil(foedtEtter)) {
		const foedselsdatoer = getFoedselsdatoer(values)

		if (foedselsdatoer?.length > 0) {
			for (let fdato of foedselsdatoer) {
				let sisteDag = new Date(fdato)
				sisteDag.setFullYear(sisteDag.getFullYear() + 67)
				if (!isBefore(fradato, sisteDag) || isAfter(tildato, sisteDag)) {
					return true
				}
			}
		}
	} else if (!_isNil(foedtFoer) && _isNil(foedtEtter)) {
		foedtFoer.setFullYear(foedtFoer.getFullYear() + 67)
		return isBefore(fradato, foedtFoer) || isBefore(tildato, foedtFoer)
	} else if (!_isNil(foedtEtter)) {
		foedtEtter.setFullYear(foedtEtter.getFullYear() + 67)
		foedtEtter.setDate(foedtEtter.getDate() + 1)
		return !isBefore(fradato, foedtEtter) || isAfter(tildato, foedtEtter)
	} else {
		// hvis ingen alder satt -->
	}
	return false
}

const validTildato = (fradato, tildato) => {
	if (!fradato || !tildato) return true
	return isAfter(new Date(tildato), new Date(fradato))
}

const ingenOverlappFraTildato = (tildato, values) => {
	const fraDato = values.arenaforvalter.dagpenger?.[0].fraDato
	const aapFraDato = values.arenaforvalter.aap?.[0].fraDato

	if (tildato || !fraDato) return true

	if (isBefore(new Date(fraDato), new Date(aapFraDato))) return false

	if (values.tidligereBestillinger) {
		const arenaBestillinger = values.tidligereBestillinger.filter((bestilling) =>
			bestilling.data.hasOwnProperty('arenaforvalter')
		)
		for (let bestilling of arenaBestillinger) {
			let arenaInfo = bestilling.data.arenaforvalter
			for (let key of ikkeOverlappendeVedtak) {
				if (arenaInfo[key]?.length > 0) {
					const fraDatoBestilling = arenaInfo[key]?.[0].fraDato
					if (isBefore(new Date(fraDato), new Date(fraDatoBestilling))) return false
				}
			}
		}
	}
	return true
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
						fraDato: values.arenaforvalter[key]?.[0].fraDato,
						tilDato: values.arenaforvalter[key]?.[0].tilDato,
					}
				}

				// Hvis det bare er en type vedtak trengs det ikke å sjekkes videre
				if (!naavaerendeVerdier.dagpenger.fraDato && !naavaerendeVerdier.aap.fraDato) return true
				if (values.tidligereBestillinger) {
					return datoOverlapperIkkeAndreVedtak(
						vedtakType,
						naavaerendeVerdier,
						values.tidligereBestillinger
					)
				} else {
					let annenVedtakType = vedtakType === 'aap' ? 'dagpenger' : 'aap'

					return datoIkkeMellom(
						naavaerendeVerdier[vedtakType].fraDato,
						naavaerendeVerdier[annenVedtakType].fraDato,
						naavaerendeVerdier[annenVedtakType].tilDato
					)
				}
			}
		)
		.nullable()
		.required(messages.required)
}

const datoOverlapperIkkeAndreVedtak = (vedtaktype, naeverendeVerdier, tidligereBestillinger) => {
	const nyDatoFra = naeverendeVerdier[vedtaktype].fraDato
	const nyDatoTil = naeverendeVerdier[vedtaktype].tilDato

	const arenaBestillinger = tidligereBestillinger.filter((bestilling) =>
		bestilling.data.hasOwnProperty('arenaforvalter')
	)

	for (const [key, value] of Object.entries(naeverendeVerdier)) {
		if (key !== vedtaktype && !datoIkkeMellom(nyDatoFra, value.fraDato, value.tilDato)) {
			return false
		}

		for (let bestilling of arenaBestillinger) {
			let arenaInfo = bestilling.data.arenaforvalter
			if (
				key in arenaInfo &&
				arenaInfo[key].length > 0 &&
				overlapperMedliste(nyDatoFra, nyDatoTil, arenaInfo[key])
			)
				return false
		}
	}
	return true
}

const overlapperMedliste = (originalFradato, orginialTildato, vedtakListe) => {
	for (let vedtak of vedtakListe) {
		const fraDato = vedtak.fraDato
		const tilDato = vedtak.tilDato

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
					const fradato = this.options.context.arenaforvalter.aap[0].fraDato
					return validTildato(fradato, tildato)
				})
				.nullable()
				.required(messages.required),
		})
	),
	aap115: Yup.array().of(
		Yup.object({
			fraDato: requiredDate,
		})
	),
	arenaBrukertype: requiredString,
	inaktiveringDato: Yup.mixed().nullable().when('arenaBrukertype', {
		is: 'UTEN_SERVICEBEHOV',
		then: requiredDate,
	}),
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
				.test('etter-fradato', 'Til-dato må være etter fra-dato.', function validDate(tildato) {
					const fradato = this.options.context.arenaforvalter.dagpenger[0].fraDato
					return validTildato(fradato, tildato)
				})
				.test(
					'skaper-ikke-overlapp',
					'Manglende til-dato skaper overlapp med annet vedtak.',
					function validDate(tildato) {
						return ingenOverlappFraTildato(tildato, this.options.context)
					}
				)
				.nullable(),
			mottattDato: Yup.date().nullable(),
		})
	),
})
