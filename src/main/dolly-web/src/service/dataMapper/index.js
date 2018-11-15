import { createHeader as c } from './Utils'
import Formatters from '~/utils/DataFormatter'
import { mapTpsfData, mapSigrunData, mapKrrData } from './mapDetailedData'

const DataMapper = {
	getHeaders() {
		return [
			c('ID', '15'),
			c('ID-type', '15'),
			c('Navn', '30'),
			c('Kjønn', '20'),
			c('Alder', '10'),
			c('Bestilling-ID', '10')
		]
	},
	getData(state) {
		/*
        Gruppe: Dolly
        Testbruker: TPSF
        */

		const { gruppe, testbruker } = state

		// TODO: Refactor, testbrukerIsFetched
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
	getDetailedData(state, ownProps) {
		const { gruppe, testbruker } = state
		const { personId } = ownProps
		if (!testbruker.items || !testbruker.items.tpsf || !testbruker.items.sigrun) return null

		const bestillingId = _findBestillingId(gruppe, ownProps.personId)
		const bestillingObj = gruppe.data[0].bestillinger.find(
			bestilling => bestilling.id === bestillingId
		)
		const tpsfData = testbruker.items.tpsf.find(item => item.ident === personId)
		let data = mapTpsfData(tpsfData, bestillingObj)
		const sigrunData = testbruker.items.sigrun[personId]
		const krrData = testbruker.items.krr && testbruker.items.krr[personId]
		if (sigrunData) {
			data.push(mapSigrunData(sigrunData))
		}
		if (krrData) {
			data.push(mapKrrData(krrData))
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
