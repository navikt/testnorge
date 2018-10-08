import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

export const getBestillingStatus = createAction(
	'GET_BESTILLING_STATUS',
	DollyApi.getBestillingStatus
)

const initialState = {}

export default handleActions(
	{
		[success(getBestillingStatus)](state, action) {
			return { ...state, [action.payload.data.id]: action.payload.data }
		}
	},
	initialState
)
