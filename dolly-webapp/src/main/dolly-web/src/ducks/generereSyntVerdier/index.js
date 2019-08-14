import { SyntApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

export const getInstValues = createAction(
	'GET_INST_VALUES',
	numberOfInstances => SyntApi.getInstValues(numberOfInstances),
	numberOfInstances => ({
		numberOfInstances
	})
)

const initialState = {}

export default handleActions(
	{
		[success(getInstValues)](state, action) {
			return { ...state, [action.meta.kodeverkNavn]: action.payload.data }
		}
	},
	initialState
)
