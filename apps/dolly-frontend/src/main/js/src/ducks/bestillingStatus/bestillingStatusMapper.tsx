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
	status: [System]
}

type System = {
	id: string
	navn: string
	statuser: [
		{
			identer: string[]
			melding: string
			detaljert: [
				{
					miljo: string
					identer: string[]
				}
			]
		}
	]
}

const finnesDetAvvikForBestillinger = (systemListe: [System]) => {
	if (!systemListe) return false
	return systemListe.some((system) => {
		return system.statuser.some((status) => status.melding !== 'OK')
	})
}

const antallIdenterOpprettetPaaBestilling = (statusListe: [System]) => {
	if (!statusListe) return 0
	if (statusListe.some((status) => status.id === 'ORGANISASJON_FORVALTER')) return null

	const addOpprettedeIdenter = (system: System) =>
		system.statuser.flatMap((status) => {
			if (status?.identer) {
				return status.identer
			} else {
				return status?.detaljert?.flatMap((detaljert) => detaljert.identer)
			}
		})

	const identStatusIdList = ['TPSF', 'TPSIMPORT', 'PDLIMPORT', 'PDL_FORVALTER']
	const aktivIdList = statusListe.filter((system) => identStatusIdList.includes(system.id))

	const identerOpprettet =
		aktivIdList.length > 0 ? aktivIdList.flatMap((system) => addOpprettedeIdenter(system)) : null

	// Kun unike identer
	return identerOpprettet && [...new Set(identerOpprettet)].length
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
					(status: System) => status?.statuser?.[0]?.detaljert?.[0]?.identer
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
