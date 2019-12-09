/**
 * Se Swagger for hvilke felter som kommer direkte fra API
 * url --> /swagger-ui.html#!/bestilling-controller/getBestillingerUsingGET
 *
 * NOTE: _Forhåpentligvis skal ikke denne leve så lenge, og at API kan levere dette direkte på sikt_
 *
 * :: Hovedfunksjonalitet for MAPPER
 * - Ny struktur for statuser fra fagsystemer
 * - Sette bestillingstatus ( STOPPET / PÅGÅR / FEILET / AVVIK / FERDIG)
 * - Pre-mappe verdier for liste
 */

import _get from 'lodash/get'
import Formatters from '~/utils/DataFormatter'

const finnesDetAvvikForBestillinger = status => {
	if (!status) return false
	return status.some(source => {
		return source.statuser.some(status => status.melding !== 'OK')
	})
}

const antallIdenterOpprettetPaaBestilling = status => {
	if (!status) return 0
	let identerOpprettet = []
	if (status.length) {
		const tpsf = status.find(f => f.id === 'TPSF')
		if (tpsf) {
			tpsf.statuser.forEach(stat => {
				stat.detaljert.forEach(miljo => {
					identerOpprettet = identerOpprettet.concat(miljo.identer)
				})
			})
		}
	}

	// Kun unike identer
	identerOpprettet = [...new Set(identerOpprettet)]

	return identerOpprettet.length
}

// Setter bestillingstatus
const extractBestillingstatusKode = (bestilling, harAvvik, antallIdenterOpprettet) => {
	return bestilling.stoppet
		? 'Stoppet'
		: !bestilling.ferdig
		? 'Pågår'
		: antallIdenterOpprettet === 0
		? 'Feilet'
		: harAvvik
		? 'Avvik'
		: 'Ferdig'
}

/**
 * Lager et String[] med verdier som vises i bestillingsliste
 * slik at man kan enkelt søke i verdier og samtidig liste de ut
 */
const extractValuesForBestillingListe = (data, statusKode) => {
	const values = {
		id: data.id.toString(),
		antallIdenter: data.antallIdenter.toString(),
		sistOppdatert: Formatters.formatDate(data.sistOppdatert),
		environments: Formatters.arrayToString(data.environments),
		statusKode
	}

	return Object.values(values)
}

export function BestillingStatusMapper(data) {
	return data.map(bestilling => {
		const harAvvik = finnesDetAvvikForBestillinger(bestilling.status)
		const antallIdenterOpprettet = antallIdenterOpprettetPaaBestilling(bestilling.status)
		const statusKode = extractBestillingstatusKode(bestilling, harAvvik, antallIdenterOpprettet)
		const listedata = extractValuesForBestillingListe(bestilling, statusKode)
		return {
			...bestilling,
			antallIdenterOpprettet,
			listedata,
			status: bestilling.status || []
		}
	})
}

const appendAvvikmeldingIfPresent = (status, source, personStatus) => {
	let nyMelding = {
		id: source.id,
		melding: status.melding
	}
	return {
		statusKode: 'Avvik',
		meldinger:
			personStatus && personStatus.meldinger
				? personStatus.meldinger.concat(nyMelding)
				: [nyMelding]
	}
}

const extractNewestBestillingstatusForPerson = (
	personStatusMap,
	bestilling,
	source,
	sourceStatus,
	ident
) => {
	// hvis bruker allerede finnes, og bestillingsid er nyere enn den forrige
	// bestillingen på brukeren, så skal den overskrives med
	if (personStatusMap.has(ident) && bestilling.id > personStatusMap.get(ident).bestillingId) {
		personStatusMap.delete(ident)
	}

	if (sourceStatus.melding === 'OK') {
		!personStatusMap.has(ident) &&
			personStatusMap.set(ident, {
				bestillingId: bestilling.id,
				statusKode: !bestilling.ferdig ? 'Pågår' : 'Ferdig'
			})
	} else {
		personStatusMap.set(ident, {
			...appendAvvikmeldingIfPresent(sourceStatus, source, personStatusMap.get(ident)),
			bestillingId: bestilling.id
		})
	}
}

export function ExtractBestillingStatusForPersoner(data) {
	const personStatusMap = new Map()
	data.forEach(bestilling =>
		_get(bestilling, 'status', []).forEach(source => {
			_get(source, 'statuser', []).forEach(sourceStatus => {
				_get(sourceStatus, 'detaljert', []).forEach(detalj => {
					_get(detalj, 'identer', []).forEach(ident => {
						extractNewestBestillingstatusForPerson(
							personStatusMap,
							bestilling,
							source,
							sourceStatus,
							ident
						)
					})
				})
			})
		})
	)

	return personStatusMap
}
