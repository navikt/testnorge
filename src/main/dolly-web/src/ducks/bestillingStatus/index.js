import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

export const getBestillinger = createAction('GET_BESTILLINGER', async gruppeID =>
	DollyApi.getBestillinger(gruppeID)
)

export const removeNyBestillingStatus = createAction('REMOVE_NY_BESTILLING_STATUS')
export const cancelBestilling = createAction('CANCEL_BESTILLING', async id =>
	DollyApi.cancelBestilling(id)
)
export const gjenopprettBestilling = createAction('GJENOPPRETT_BESTILLING', async (id, envs) =>
	DollyApi.gjenopprettBestilling(id, envs)
)

// ny-array holder oversikt over nye bestillinger i en session
const initialState = { ny: [] }

export default handleActions(
	{
		[success(getBestillinger)](state, action) {
			const { data } = action.payload
			const nyeBestillinger = data.filter(bestilling => {
				if (!bestilling.ferdig) return true
			})
			let idListe = []
			nyeBestillinger.forEach(bestilling => {
				if (!state.ny.find(id => id == bestilling.id)) idListe.push(bestilling.id)
			})
			return {
				...state,
				data,
				ny: idListe.length > 0 ? [...state.ny, ...idListe] : state.ny
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
