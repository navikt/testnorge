import { VarslingerApi } from '~/service/Api'
import { createActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'

export const { getVarslinger, getVarslingerBruker, updateVarslingerBruker } = createActions(
	{
		getVarslinger: VarslingerApi.getVarslinger,
		getVarslingerBruker: VarslingerApi.getVarslingerBruker,
		updateVarslingerBruker: VarslingerApi.updateVarslingerBruker
	},
	{ prefix: 'varslinger' }
)

const initialState = {
	varslingerData: [],
	varslingerBrukerData: []
}

export default handleActions(
	{
		[onSuccess(getVarslinger)](state, action) {
			state.varslingerData = action.payload ? action.payload.data : null
		},
		[onSuccess(getVarslingerBruker)](state, action) {
			state.varslingerBrukerData = action.payload ? action.payload.data : null
		}
	},
	initialState
)
