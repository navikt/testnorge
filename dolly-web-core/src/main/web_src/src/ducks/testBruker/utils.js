import _set from 'lodash/set'
import DataFormatter from '~/utils/DataFormatter'
import { getIdentByIdSelector } from '~/ducks/gruppe'
import { getBestillingById } from '~/ducks/bestillingStatus'

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
	const { bestillingStatuser } = state
	const person = getIdentByIdSelector(state, ident)

	if (!person) return null

	const { environments } = getBestillingById(bestillingStatuser.data, person.bestillingId[0])

	return environments
}

export const mapSigrunSekvensnummer = (inntektData, sekvensData) => {
	return inntektData.map(i => {
		const sekvens = sekvensData.find(s => s.gjelderPeriode === i.inntektsaar)
		const sekvensnummer = sekvens && sekvens.sekvensnummer.toString()
		return { ...i, sekvensnummer }
	})
}
