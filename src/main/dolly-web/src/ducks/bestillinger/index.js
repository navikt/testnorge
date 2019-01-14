import { DollyApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { createAction, handleActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

export const getBestillinger = createAction('GET_BESTILLINGER', DollyApi.getBestillinger)

const initialState = {}

export default handleActions(
	{
		[success(getBestillinger)](state, action) {
			const { data } = action.payload

			return { ...state, data }
		}
	},
	initialState
)
