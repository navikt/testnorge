import { DollyApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

export const getDollyApiConfig = createAction('GET_DOLLY_API_CONFIG', DollyApi.getConfig)

const initialState = {
	dollyApi: null
}

export default handleActions(
	{
		[success(getDollyApiConfig)](state, action) {
			return { ...state, dollyApi: action.payload.data }
		}
	},
	initialState
)
