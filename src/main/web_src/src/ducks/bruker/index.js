import { DollyApi, ProfilApi } from '~/service/Api'
import { createActions, combineActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'

export const {
	getCurrentBruker,
	getCurrentBrukerBilde,
	addFavorite,
	removeFavorite
} = createActions(
	{
		getCurrentBruker: ProfilApi.getProfil,
		getCurrentBrukerBilde: ProfilApi.getProfilBilde,
		addFavorite: DollyApi.addFavorite,
		removeFavorite: DollyApi.removeFavorite
	},
	{ prefix: 'bruker' }
)

const initialState = {
	brukerData: null,
	brukerBilde: null
}

const successActions = combineActions(
	onSuccess(getCurrentBruker),
	onSuccess(addFavorite),
	onSuccess(removeFavorite)
)

export default handleActions(
	{
		[successActions](state, action) {
			state.brukerData = action.payload.data
		},
		[onSuccess(getCurrentBrukerBilde)](state, action) {
			state.brukerBilde = action.payload.data
		}
	},
	initialState
)
