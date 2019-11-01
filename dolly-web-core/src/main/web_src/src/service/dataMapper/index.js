import { mapBestillingId } from './Utils'
import { getBestillingById } from '~/ducks/bestillingStatus'
import { getIdentByIdSelector } from '~/ducks/gruppe'
import Formatters from '~/utils/DataFormatter'
import { mapTpsfData } from './mapTpsDataToIdent'
import { mapPdlData } from './mapPdlDataToIdent'
import {
	mapKrrData,
	mapSigrunData,
	mapAaregData,
	mapArenaData,
	mapInstData,
	mapUdiData,
	mapAliasData
} from './mapRegistreDataToIdent'

// * Mapper testperson-data for å vise under testpersonliste
const DataMapper = {
	// Testbrukersliste
	getData(state) {
		const { gruppe, testbruker } = state

		if (!testbruker.items.tpsf) return null

		return testbruker.items.tpsf.map(i => {
			return [
				i.ident,
				i.identtype,
				i.mellomnavn
					? `${i.fornavn} ${i.mellomnavn} ${i.etternavn}`
					: `${i.fornavn} ${i.etternavn}`,
				Formatters.kjonnToString(i.kjonn),
				Formatters.formatAlder(i.alder, i.doedsdato),
				_findBestillingId(gruppe, i.ident).toString()
			]
		})
	},

	// Viser under expand
	getDetailedData(state, personId) {
		const { gruppe, testbruker, bestillingStatuser } = state

		if (!testbruker.items || !testbruker.items.tpsf) return null

		const testIdent = gruppe.data[0].identer.find(testIdent => testIdent.ident === personId)
		const tpsfData = testbruker.items.tpsf.find(item => item.ident === personId)
		if (!tpsfData) return null

		const sigrunData = testbruker.items.sigrunstub && testbruker.items.sigrunstub[personId]
		const krrData = testbruker.items.krrstub && testbruker.items.krrstub[personId]
		const aaregData = testbruker.items.aareg && testbruker.items.aareg[personId]
		const pdlfData = testbruker.items.pdlforvalter && testbruker.items.pdlforvalter[personId]
		const arenaData =
			testbruker.items.arenaforvalteren && testbruker.items.arenaforvalteren[personId]
		const instData = testbruker.items.instdata && testbruker.items.instdata[personId]
		const udiData = testbruker.items.udistub && testbruker.items.udistub[personId]

		var bestillingId = _findBestillingId(gruppe, personId)

		const bestilling = getBestillingById(bestillingStatuser.data, bestillingId[0])

		const tpsfKriterier = JSON.parse(bestilling.tpsfKriterier)
		const bestKriterier = JSON.parse(bestilling.bestKriterier)

		let data = mapTpsfData(tpsfData, tpsfKriterier)

		if (aaregData) {
			data.push(mapAaregData(aaregData))
		}
		if (sigrunData && sigrunData.length > 0) {
			data.push(mapSigrunData(sigrunData))
		}
		if (krrData) {
			data.push(mapKrrData(krrData))
		}
		if (pdlfData) {
			data.push(...mapPdlData(pdlfData))
		}
		if (arenaData) {
			// Workaround for å hente servicebehov-type, inaktiveringsdato, AAP og AAP115
			// fra bestilling så lenge vi ikke kan få den fra arenaforvalteren
			var kvalifiseringsgruppe = bestKriterier.arenaforvalter.kvalifiseringsgruppe
			var inaktiveringDato = bestKriterier.arenaforvalter.inaktiveringDato
			var aap115 = bestKriterier.arenaforvalter.aap115
			var aap = bestKriterier.arenaforvalter.aap
			data.push(mapArenaData(arenaData, kvalifiseringsgruppe, inaktiveringDato, aap115, aap))
		}

		if (instData) {
			data.push(mapInstData(instData))
		}

		if (udiData) {
			const asylsøker = bestKriterier.udistub.soeknadOmBeskyttelseUnderBehandling
			data.push(mapUdiData(udiData, asylsøker))
			if (udiData.aliaser) {
				data.push(mapAliasData(udiData.aliaser))
			}
		}

		if (bestillingId.length > 1) {
			data.push(mapBestillingId(testIdent))
		}
		return data
	}
}

const _findBestillingId = (gruppe, personId) => {
	const identArray = gruppe.data[0].identer
	const personObj = identArray.find(item => item.ident === personId)
	return personObj ? personObj.bestillingId : ''
}

export default DataMapper
