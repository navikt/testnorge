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

const _extract = (arr, id, navn) => {
	if (!arr) return false
	return {
		id,
		navn,
		statuser: arr.map(status => {
			const obj = {
				melding: status.statusMelding || status.status
			}

			if (status.identer) obj.identer = status.identer

			const envNode = status.environmentIdents || status.envIdent || status.environmentIdentsForhold

			if (envNode) {
				obj.detaljert = Object.keys(envNode).map(key => {
					const identer = envNode[key]
					return {
						miljo: key,
						identer: Array.isArray(identer) ? identer : Object.keys(identer)
					}
				})
			}

			return obj
		})
	}
}

/**
 * Mapper ulike statuser som kommer fra API. Disse ligger
 * på separate noder (tpsfStatus, aaregStatus, etc.) Disse mapper om til å
 * følge et generisk statusobject 
 * 
 * Eksempel:
const example = {
	id: 'tpsfStatus',
	navn: 'TPSF',
	statuser: [
		{
			melding: 'OK',
			identer: ['31106329632'], // optional
			detaljert: [ // Optional
				{
					miljo: 't4',
					identer: ['31106329632']
				}
			]
		}
	]
}
 */
const mapStatusStructure = data => {
	return [
		_extract(data.tpsfStatus, 'TPSF', 'Tjenestebasert personsystem (TPS)'),
		_extract(data.sigrunStubStatus, 'sigrunStubStatus', 'Sigrun'),
		_extract(data.krrStubStatus, 'krrStubStatus', 'KRR'),
		_extract(data.udiStubStatus, 'udiStubStatus', 'UDI'),
		_extract(data.arenaforvalterStatus, 'arenaforvalterStatus', 'ARENA'),
		_extract(data.aaregStatus, 'aaregStatus', 'AAREG'),
		_extract(data.instdataStatus, 'instdataStatus', 'INST'),

		// PDLF
		_extract(_get(data, 'pdlforvalterStatus.pdlForvalter'), 'pdlforvalterStatus', 'PDLF'),
		_extract(
			_get(data, 'pdlforvalterStatus.falskIdentitet'),
			'pdlforvalterStatus',
			'PDLF: Falsk Identitet'
		),
		_extract(
			_get(data, 'pdlforvalterStatus.kontaktinfoDoedsbo'),
			'pdlforvalterStatus',
			'PDLF: Kontaktinfo Dødsbo'
		),
		_extract(
			_get(data, 'pdlforvalterStatus.utenlandsid'),
			'pdlforvalterStatus',
			'PDLF: Utenlands ID'
		)
	].filter(x => x) // fjern falsy values
}

const finnnesDetAvvik = status => {
	return status.some(source => {
		return source.statuser.some(status => status.melding !== 'OK')
	})
}

const antallIdenterOpprettetPaaBestilling = status => {
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

export default function BestillingStatusMapper(data) {
	return data.map(bestilling => {
		const status = mapStatusStructure(bestilling)
		const harAvvik = finnnesDetAvvik(status)
		const antallIdenterOpprettet = antallIdenterOpprettetPaaBestilling(status)
		const statusKode = extractBestillingstatusKode(bestilling, harAvvik, antallIdenterOpprettet)
		const listedata = extractValuesForBestillingListe(bestilling, statusKode)
		return {
			...bestilling,
			antallIdenterOpprettet,
			status,
			listedata
		}
	})
}
