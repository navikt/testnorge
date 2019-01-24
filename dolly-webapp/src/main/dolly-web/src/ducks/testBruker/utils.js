import _set from 'lodash/set'
import DataFormatter from '~/utils/DataFormatter'

export const mapValuesFromDataSource = (values, attributtListe, dataSource) => {
	const filteredAttributtListe = attributtListe.filter(item => item.dataSource === dataSource)
	return filteredAttributtListe.reduce((prev, curr) => {
		let currentValue = values[curr.id]
		if (curr.inputType === 'date') currentValue = DataFormatter.parseDate(currentValue)
		return _set(prev, curr.editPath || curr.path || curr.id, currentValue)
	}, {})
}

export const mapIdentAndEnvironementForTps = (state, ident) => {
	return {
		identer: [ident],
		miljoer: _findEnvironmentsForIdent(state, ident)
	}
}

const _findEnvironmentsForIdent = (state, ident) => {
	const { gruppe, bestillingStatuser } = state
	if (!gruppe.data) return null

	const identArray = gruppe.data[0].testidenter
	const personObj = identArray.find(item => item.ident === ident)
	if (!personObj) return null

	const bestillingObj = bestillingStatuser.data.find(
		bestilling => bestilling.id === personObj.bestillingId
	)
	return bestillingObj.environments
}
