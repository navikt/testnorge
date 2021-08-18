import * as Yup from 'yup'
import { messages, requiredDate, requiredString } from '~/utils/YupValidations'
import { isAfter, isBefore } from 'date-fns'

const ikkeOverlappendeVedtak = ['aap', 'dagpenger']

const datoIkkeMellom = (nyDatoFra, gjeldendeDatoFra, gjeldendeDatoTil) => {
	if (!gjeldendeDatoFra || !gjeldendeDatoTil) return true
	return (
		isAfter(new Date(nyDatoFra), new Date(gjeldendeDatoTil)) ||
		isBefore(new Date(nyDatoFra), new Date(gjeldendeDatoFra))
	)
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
		const arenaBestillinger = values.tidligereBestillinger.filter(bestilling =>
			bestilling.data.hasOwnProperty('arenaforvalter')
		)
		for (let bestilling of arenaBestillinger) {
			let arenaInfo = bestilling.data.arenaforvalter
			for (let key of ikkeOverlappendeVedtak) {
				if (arenaInfo[key].length > 0) {
					const fraDatoBestilling = arenaInfo[key]?.[0].fraDato
					if (isBefore(new Date(fraDato), new Date(fraDatoBestilling))) return false
				}
			}
		}
	}
	return true
}

const validFradato = vedtakType => {
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
						tilDato: values.arenaforvalter[key]?.[0].tilDato
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

	const arenaBestillinger = tidligereBestillinger.filter(bestilling =>
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
				.required(messages.required)
		})
	),
	aap115: Yup.array().of(
		Yup.object({
			fraDato: requiredDate
		})
	),
	arenaBrukertype: requiredString,
	inaktiveringDato: Yup.mixed()
		.nullable()
		.when('arenaBrukertype', {
			is: 'UTEN_SERVICEBEHOV',
			then: requiredDate
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
			mottattDato: Yup.date().nullable()
		})
	)
})
