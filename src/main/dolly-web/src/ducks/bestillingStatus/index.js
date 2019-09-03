import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'
import { actions as bestillingActions } from '~/ducks/bestilling'

export const getBestillinger = createAction('GET_BESTILLINGER', async gruppeID => {
	return DollyApi.getBestillinger(gruppeID)
})

export const removeNyBestillingStatus = createAction('REMOVE_NY_BESTILLING_STATUS')

// ny-array holder oversikt over nye bestillinger i en session
const initialState = { ny: [] }

export const cancelBestilling = createAction('CANCEL_BESTILLING', async id => {
	return DollyApi.cancelBestilling(id)
})

export const gjenopprettBestilling = createAction('GJENOPPRETT_BESTILLING', async (id, envs) => {
	return DollyApi.gjenopprettBestilling(id, envs)
})

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

		// [success(bestillingActions.postBestilling)](state, action) {
		// 	return { ...state, ny: [...state.ny, action.payload.data.id] }
		// },

		// [success(gjenopprettBestilling)](state, action) {
		// 	return { ...state, ny: [...state.ny, action.payload.data.id] }
		// },

		// [success(cancelBestilling)](state, action) {
		// 	return { ...state, ny: state.ny.filter(id => id !== action.payload.id) }
		// }

		[removeNyBestillingStatus](state, action) {
			return { ...state, ny: state.ny.filter(id => id !== action.payload) }
		}
	},
	initialState
)
