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

type Bestilling = {
	id: number
	antallIdenter: number
	antallLevert: number
	ferdig: boolean
	sistOppdatert: Date
	bruker: {
		brukerId: string
		brukernavn: string
		brukertype: string
		epost: string
	}
	gruppeId: number
	stoppet: boolean
	environments: [string]
	status: [Status]
}

type Status = {
	id: string
	navn: string
	statuser: [
		{
			identer: [string]
			melding: string
			detaljert: [
				{
					miljo: string
					identer: [string]
				}
			]
		}
	]
}

const finnesDetAvvikForBestillinger = (statusListe: [Status]) => {
	if (!statusListe) return false
	return statusListe.some((source) => {
		return source.statuser.some((status) => status.melding !== 'OK')
	})
}

const antallIdenterOpprettetPaaBestilling = (statusListe: [Status]) => {
	if (!statusListe) return 0
	if (statusListe.some((status) => status.id === 'ORGANISASJON_FORVALTER')) return null

	let identerOpprettet: string[] = []
	if (statusListe.length) {
		const tpsf = statusListe.find((f) => f.id === 'TPSF')
		const importFraTps = statusListe.find((f) => f.id === 'TPSIMPORT')
		const importFraPdl = statusListe.find((f) => f.id === 'PDLIMPORT')
		const importFraPdlforvalter = statusListe.find((f) => f.id === 'PDL_FORVALTER')

		const addOpprettedeIdenter = (system: Status) => {
			system.statuser.forEach((stat) => {
				stat?.identer
					? (identerOpprettet = identerOpprettet.concat(stat.identer))
					: stat?.detaljert?.forEach((miljo) => {
							identerOpprettet = identerOpprettet.concat(miljo.identer)
					  })
			})
		}

		if (tpsf) addOpprettedeIdenter(tpsf)
		if (importFraTps) addOpprettedeIdenter(importFraTps)
		if (importFraPdl) addOpprettedeIdenter(importFraPdl)
		if (importFraPdlforvalter) addOpprettedeIdenter(importFraPdlforvalter)
	}

	// Kun unike identer
	identerOpprettet = [...new Set(identerOpprettet)]

	return identerOpprettet.length
}

// Setter bestillingstatus
const extractBestillingstatusKode = (
	bestilling: Bestilling,
	harAvvik: boolean,
	antallIdenterOpprettet: number
) => {
	if (bestilling.stoppet) return 'Stoppet'
	if (!bestilling.ferdig) return 'Pågår'
	if (antallIdenterOpprettet === 0) return 'Feilet'
	return harAvvik ? 'Avvik' : 'Ferdig'
}

/**
 * Lager et String[] med verdier som vises i bestillingsliste
 * slik at man kan enkelt søke i verdier og samtidig liste de ut
 */
const extractValuesForBestillingListe = (
	data: Bestilling,
	identer: string[],
	statusKode: string
) => {
	const values = {
		id: data.id.toString(),
		antallIdenter: data.antallIdenter ? data.antallIdenter.toString() : null,
		sistOppdatert: Formatters.formatDate(data.sistOppdatert),
		environments: Formatters.arrayToString(data.environments),
		statusKode,
		identer: Formatters.arrayToString(identer),
	}

	return Object.values(values)
}

export default function bestillingStatusMapper(data: [Object]) {
	return data.map((bestilling: Bestilling) => {
		const alleIdenter = [
			...new Set(
				bestilling.status?.flatMap(
					(status: Status) => status?.statuser?.[0]?.detaljert?.[0]?.identer
				)
			),
		]?.filter((ident) => ident !== undefined)
		const harAvvik = finnesDetAvvikForBestillinger(bestilling.status)
		const antallIdenterOpprettet = antallIdenterOpprettetPaaBestilling(bestilling.status)
		const statusKode = extractBestillingstatusKode(bestilling, harAvvik, antallIdenterOpprettet)
		const listedata = extractValuesForBestillingListe(bestilling, alleIdenter, statusKode)
		return {
			...bestilling,
			antallIdenterOpprettet,
			listedata,
			status: bestilling.status || [],
		}
	})
}
