import { SyntApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

export const getInst2Values = createAction(
	'GET_INST2_VALUES',
	numberOfInstances => SyntApi.getInst2Values(numberOfInstances),
	numberOfInstances => ({
		numberOfInstances
	})
)

const initialState = {}

export default handleActions(
	{
		[success(getInst2Values)](state, action) {
			return { ...state, [action.meta.kodeverkNavn]: action.payload.data }
		}
	},
	initialState
)
