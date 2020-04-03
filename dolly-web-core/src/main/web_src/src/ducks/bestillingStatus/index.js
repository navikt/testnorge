import { LOCATION_CHANGE } from 'connected-react-router'
import { createActions } from 'redux-actions'
import _isNil from 'lodash/isNil'
import _mapValues from 'lodash/mapValues'
import _uniq from 'lodash/uniq'
import { DollyApi } from '~/service/Api'
import bestillingStatusMapper from './bestillingStatusMapper'
import { onSuccess } from '~/ducks/utils/requestActions'
import { handleActions } from '~/ducks/utils/immerHandleActions'

export const {
	getBestillinger,
	cancelBestilling,
	gjenopprettBestilling,
	removeNyBestillingStatus
} = createActions(
	{
		getBestillinger: DollyApi.getBestillinger,
		cancelBestilling: DollyApi.cancelBestilling,
		gjenopprettBestilling: DollyApi.gjenopprettBestilling
	},
	'removeNyBestillingStatus',
	{ prefix: 'bestillingStatus' }
)

// ny-array holder oversikt over nye bestillinger i en session
const initialState = { ny: [], byId: {} }

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(getBestillinger)](state, action) {
			const { data } = action.payload
			const nyeBestillinger = data
				.filter(bestilling => !bestilling.ferdig && !state.ny.includes(bestilling.id))
				.map(bestilling => bestilling.id)

			if (nyeBestillinger.length > 0) {
				state.ny = state.ny.concat(nyeBestillinger)
			}

			bestillingStatusMapper(data).map(best => {
				state.byId[best.id] = best
			})
		},
		[removeNyBestillingStatus](state, action) {
			state.ny = state.ny.filter(id => id !== action.payload)
		}
	},
	initialState
)

/**
 * Finner bestilling som matcher ID
 * @param {array} bestillingStatuser
 * @param {string|number} id
 */
export const getBestillingById = (state, id) => state.bestillingStatuser.byId[id]

// Henter bestilling objecter basert på nye bestillinger (Array av ID'er)
export const nyeBestillingerSelector = state => {
	// Filter() -> Fjerner non-truthy values hvis find funksjon feiler
	return state.bestillingStatuser.ny.map(id => getBestillingById(state, id)).filter(x => Boolean(x))
}

// Henter alle bestillinger som er gjort på en ident
export const getAlleBestillingerPrIdent = (state, personId) => {
	const bestillingId = state.gruppe.ident[personId].bestillingId
	const bestillingInfo = []
	bestillingId.forEach(id => {
		bestillingInfo.push(state.bestillingStatuser.byId[id])
	})
	return bestillingInfo
}

// Filtrer bestillinger basert på søkestreng
export const sokSelector = (state, searchStr) => {
	const items = Object.values(state.bestillingStatuser.byId)
	if (!searchStr || !items) return items

	return items.filter(({ listedata }) => {
		const searchValues = listedata.filter(v => !_isNil(v)).map(v => v.toString().toLowerCase())
		return searchValues.some(v => v.includes(searchStr.toLowerCase()))
	})
}

// Object med system som key, og OK-miljøer som value
// StatusArray = state.bestillingStatus[0].status
export const successMiljoSelector = statusArray => {
	const success_list = statusArray.reduce((acc, curr) => {
		const statuser = curr.statuser.filter(v => v.melding === 'OK')

		if (statuser.length) {
			// Dette er statuser som er OK
			const detaljert = statuser[0].detaljert
			const envs = detaljert && detaljert.map(v => v.miljo)

			if (acc[curr.id]) {
				acc[curr.id] = acc[curr.id].concat(envs)
			} else {
				acc[curr.id] = envs
			}
		}

		return acc
	}, {})

	// Filtrer og sorter miljøer
	return _mapValues(success_list, v => _uniq(v).sort())
}
