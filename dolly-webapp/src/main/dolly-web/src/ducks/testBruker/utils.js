import _set from 'lodash/set'
import DataFormatter from '~/utils/DataFormatter'

export const mapValuesFromDataSource = (values, attributtListe, dataSource) => {
	let temp = false
	const filteredAttributtListe = attributtListe.filter(item => {
		temp = false
		item.items.filter(item => {
			item.items.filter(item => {
				if (item.dataSource === dataSource) {
					temp = true
					return temp
				}
			})
			return temp
		})
		return temp
	})

	let verdier = {}
	filteredAttributtListe.forEach(item => {
		item.items.forEach(item => {
			item.items.forEach(item => {
				let currentValue = values[item.id]
				if (item.inputType === 'date') currentValue = DataFormatter.parseDate(currentValue)
				_set(verdier, item.editpath || item.path || item.id, currentValue)
			})
		})
	})
	return verdier
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

	const bestillingObj = bestillingStatuser.data.find(bestilling => {
		return personObj.bestillingId.some(id => bestilling.id === id)
	})

	return bestillingObj.environments
}

export const mapSigrunSekvensnummer = (inntektData, sekvensData) => {
	return inntektData.map(i => {
		const sekvens = sekvensData.find(s => s.gjelderPeriode === i.inntektsaar)
		const sekvensnummer = sekvens && sekvens.sekvensnummer.toString()
		return { ...i, sekvensnummer }
	})
}
