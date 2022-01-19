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

import Formatters from '~/utils/DataFormatter'

const finnesDetAvvikForBestillinger = (status) => {
	if (!status) return false
	return status.some((source) => {
		return source.statuser.some((status) => status.melding !== 'OK')
	})
}

const antallIdenterOpprettetPaaBestilling = (status) => {
	if (!status) return 0
	if (status.some((status) => status.id === 'ORGANISASJON_FORVALTER')) return null
	let identerOpprettet = []
	if (status.length) {
		const tpsf = status.find((f) => f.id === 'TPSF')
		const importFraTps = status.find((f) => f.id === 'TPSIMPORT')
		const importFraPdl = status.find((f) => f.id === 'PDLIMPORT')

		const addOpprettedeIdenter = (system) => {
			system.statuser.forEach((stat) => {
				stat.detaljert.forEach((miljo) => {
					identerOpprettet = identerOpprettet.concat(miljo.identer)
				})
			})
		}

		if (tpsf) addOpprettedeIdenter(tpsf)
		if (importFraTps) addOpprettedeIdenter(importFraTps)
		if (importFraPdl) addOpprettedeIdenter(importFraPdl)
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
		antallIdenter: data.antallIdenter ? data.antallIdenter.toString() : null,
		sistOppdatert: Formatters.formatDate(data.sistOppdatert),
		environments: Formatters.arrayToString(data.environments),
		statusKode,
	}

	return Object.values(values)
}

export default function bestillingStatusMapper(data) {
	return data.map((bestilling) => {
		const harAvvik = finnesDetAvvikForBestillinger(bestilling.status)
		const antallIdenterOpprettet = antallIdenterOpprettetPaaBestilling(bestilling.status)
		const statusKode = extractBestillingstatusKode(bestilling, harAvvik, antallIdenterOpprettet)
		const listedata = extractValuesForBestillingListe(bestilling, statusKode)
		return {
			...bestilling,
			antallIdenterOpprettet,
			listedata,
			status: bestilling.status || [],
		}
	})
}
