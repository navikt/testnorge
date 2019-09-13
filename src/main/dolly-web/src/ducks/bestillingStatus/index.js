import { createAction, handleActions } from 'redux-actions'
import { DollyApi } from '~/service/Api'
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
				.filter(bestilling => !bestilling.ferdig)
				.filter(bestilling => !state.ny.find(id => id == bestilling.id))
				.map(bestilling => bestilling.id)

			return {
				...state,
				data,
				ny: nyeBestillinger.length > 0 ? [...state.ny, ...nyeBestillinger] : state.ny
			}
		},
		[removeNyBestillingStatus](state, action) {
			return { ...state, ny: state.ny.filter(id => id !== action.payload) }
		}
	},
	initialState
)

// Selector for nye bestillinger
export const nyeBestillingerSelector = bestillinger => {
	if (!bestillinger.data) return null

	return bestillinger.ny.map(id => bestillinger.data.find(bestilling => bestilling.id === id))
}
