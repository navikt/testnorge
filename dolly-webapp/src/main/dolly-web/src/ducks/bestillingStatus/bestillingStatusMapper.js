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

const _extract = (arr, kilde, navn) => {
	if (!arr) return false
	return {
		kilde,
		navn,
		statuser: arr.map(status => {
			const obj = {
				statusMelding: status.statusMelding || status.status
			}

			if (status.identer) obj.identer = status.identer

			const envNode = status.environmentIdents || status.envIdent || status.environmentIdentsForhold

			if (envNode) {
				obj.environments = Object.keys(envNode).map(key => {
					const identer = envNode[key]
					return {
						miljonavn: key,
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
	kilde: 'tpsfStatus',
	navn: 'TPSF',
	statuser: [
		{
			status: 'OK',
			identer: ['31106329632'], // optional
			environments: [ // Optional
				{
					miljonavn: 't4',
					identer: ['31106329632']
				}
			]
		}
	]
}
 */
const mapStatusStructure = data => {
	return [
		_extract(data.tpsfStatus, 'tpsfStatus', 'TPSF'),
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

const finnnesDetAvvik = statusrapport => {
	return statusrapport.some(source => {
		return source.statuser.some(status => status.statusMelding !== 'OK')
	})
}

// Setter bestillingstatus
const extractBestillingstatus = (data, harAvvik) => {
	const harIkkeIdenter = item => !Boolean(item.tpsfStatus)
	return data.stoppet
		? 'Stoppet'
		: !data.ferdig
			? 'Pågår'
			: harIkkeIdenter(data)
				? 'Feilet'
				: harAvvik
					? 'Avvik'
					: 'Ferdig'
}

/**
 * Lager et String[] med verdier som vises i bestillingsliste
 * slik at man kan enkelt søke i verdier og samtidig liste de ut
 */
const extractValuesForBestillingListe = (data, status) => {
	const values = {
		id: data.id.toString(),
		antallIdenter: data.antallIdenter.toString(),
		sistOppdatert: Formatters.formatDate(data.sistOppdatert),
		environments: Formatters.arrayToString(data.environments),
		status
	}

	return Object.values(values)
}

export default function BestillingStatusMapper(data) {
	return data.map(bestilling => {
		const statusrapport = mapStatusStructure(bestilling)
		const harAvvik = finnnesDetAvvik(statusrapport)
		const status = extractBestillingstatus(bestilling, harAvvik)
		const listedata = extractValuesForBestillingListe(bestilling, status)
		return {
			...bestilling,
			status,
			statusrapport,
			listedata
		}
	})
}
