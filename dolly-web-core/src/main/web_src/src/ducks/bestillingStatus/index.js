import { createAction, handleActions } from 'redux-actions'
import _isNil from 'lodash/isNil'
import _mapValues from 'lodash/mapValues'
import _uniq from 'lodash/uniq'
import { DollyApi } from '~/service/Api'
import bestillingStatusMapper from './bestillingStatusMapper'
import { onSuccess } from '~/ducks/utils/requestActions'

export const getBestillinger = createAction('GET_BESTILLINGER', gruppeID =>
	DollyApi.getBestillinger(gruppeID)
)

export const removeNyBestillingStatus = createAction('REMOVE_NY_BESTILLING_STATUS')
export const cancelBestilling = createAction('CANCEL_BESTILLING', id =>
	DollyApi.cancelBestilling(id)
)
export const gjenopprettBestilling = createAction('GJENOPPRETT_BESTILLING', (id, envs) =>
	DollyApi.gjenopprettBestilling(id, envs)
)

// ny-array holder oversikt over nye bestillinger i en session
const initialState = { ny: [], data: [] }

export default handleActions(
	{
		[onSuccess(getBestillinger)](state, action) {
			const { data } = action.payload
			const nyeBestillinger = data
				.filter(bestilling => !bestilling.ferdig && !state.ny.includes(bestilling.id))
				.map(bestilling => bestilling.id)

			return {
				...state,
				data: bestillingStatusMapper(data),
				ny: nyeBestillinger.length > 0 ? [...state.ny, ...nyeBestillinger] : state.ny
			}
		},
		[removeNyBestillingStatus](state, action) {
			return { ...state, ny: state.ny.filter(id => id !== action.payload) }
		}
	},
	initialState
)

/**
 * Finner bestilling som matcher ID
 * @param {array} bestillingStatuser
 * @param {string|number} id
 */
export const getBestillingById = (bestillinger, id) => {
	if (!bestillinger) return null
	return bestillinger.find(bestilling => bestilling.id === parseInt(id))
}

// Henter bestilling objecter basert på nye bestillinger (Array av ID'er)
export const nyeBestillingerSelector = bestillinger => {
	if (!bestillinger.data) return null

	// Filter() -> Fjerner non-truthy values hvis find funksjon feiler
	return bestillinger.ny.map(id => getBestillingById(bestillinger.data, id)).filter(x => Boolean(x))
}

// Filtrer bestillinger basert på søkestreng
export const sokSelector = (items, searchStr) => {
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
