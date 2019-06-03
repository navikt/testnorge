import { DollyApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import { LOCATION_CHANGE } from 'connected-react-router'
import success from '~/utils/SuccessAction'
import _groupBy from 'lodash/groupBy'

export const postOpenAm = createAction('POST_OPEN_AM', async bestillingId => {
	const res = await DollyApi.postOpenAmBestilling(bestillingId)
	const data = res.data
	const payload = { bestillingId, data }
	return payload
})

export const removeNyOpenAmStatus = createAction('REMOVE_NY_OPENAM_STATUS')

const initialState = {
	responses: []
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[success(postOpenAm)](state, action) {
			const id = action.payload.bestillingId
			const data = action.payload.data
			const lukket = false
			return { ...state, responses: [...state.responses, { id, data, lukket }] }
		},
		[removeNyOpenAmStatus](state, action) {
			const res = state.responses.find(response => response.id === action.payload)
			const lukketRes = { ...res, lukket: true }
			let copy = JSON.parse(
				JSON.stringify(state.responses.filter(response => response.id !== action.payload))
			)
			return { ...state, responses: [...copy, lukketRes] }
		}
	},
	initialState
)

//thunk
export const sendToOpenAm = bestillingId => (dispatch, getState) => {
	return dispatch(postOpenAm(bestillingId))
}
