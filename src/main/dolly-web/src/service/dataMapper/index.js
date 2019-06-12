import { createHeader as c, mapBestillingId } from './Utils'
import Formatters from '~/utils/DataFormatter'
import { mapTpsfData } from './mapTpsDataToIdent'
import { mapPdlData } from './mapPdlDataToIdent'
import { mapKrrData, mapSigrunData, mapAaregData } from './mapRegistreDataToIdent'

// * Mapper testperson-data for å vise under testpersonliste
const DataMapper = {
	getHeaders() {
		return [
			c('Ident', '15'),
			c('Type', '15'),
			c('Navn', '30'),
			c('Kjønn', '20'),
			c('Alder', '10'),
			c('Bestilling-ID', '10')
		]
	},

	// Testbrukersliste
	getData(state) {
		/*
        Gruppe: Dolly
        Testbruker: TPSF
        */

		const { gruppe, testbruker } = state

		if (!testbruker.items.tpsf) return null

		return testbruker.items.tpsf.map(i => {
			return [
				i.ident,
				i.identtype,
				`${i.fornavn} ${i.etternavn}`,
				Formatters.kjonnToString(i.kjonn),
				Formatters.formatAlder(i.alder, i.doedsdato),
				_findBestillingId(gruppe, i.ident).toString()
			]
		})
	},

	// Viser under expand
	getDetailedData(state, ownProps) {
		const { gruppe, testbruker } = state

		const { personId } = ownProps
		if (!testbruker.items || !testbruker.items.tpsf) return null

		const testIdent = gruppe.data[0].testidenter.find(testIdent => testIdent.ident === personId)
		const tpsfData = testbruker.items.tpsf.find(item => item.ident === personId)
		if (!tpsfData) return null

		let data = mapTpsfData(tpsfData, testIdent)

		const sigrunData = testbruker.items.sigrunstub && testbruker.items.sigrunstub[personId]
		const krrData = testbruker.items.krrstub && testbruker.items.krrstub[personId]
		const aaregData = testbruker.items.aareg && testbruker.items.aareg[personId]
		const pdlfData = testbruker.items.pdlforvalter && testbruker.items.pdlforvalter[personId]
		var bestillingId = _findBestillingId(gruppe, personId)

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
			data.push(mapPdlData(pdlfData))
		}

		if (bestillingId.length > 1) {
			data.push(mapBestillingId(testIdent))
		}
		return data
	}
}

const _findBestillingId = (gruppe, personId) => {
	const identArray = gruppe.data[0].testidenter
	const personObj = identArray.find(item => item.ident === personId)
	return personObj.bestillingId || ''
}

export default DataMapper
