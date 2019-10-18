import { createAction, handleActions } from 'redux-actions'
import _isNil from 'lodash/isNil'
import { DollyApi } from '~/service/Api'
import bestillingStatusMapper from './bestillingStatusMapper'
import success from '~/utils/SuccessAction'

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
		[success(getBestillinger)](state, action) {
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
