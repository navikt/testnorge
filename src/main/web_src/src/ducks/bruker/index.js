import { DollyApi, ProfilApi } from '~/service/Api'
import { combineActions, createActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'

export const {
	getCurrentBruker,
	getCurrentBrukerProfil,
	getCurrentBrukerBilde,
	addFavorite,
	removeFavorite
} = createActions(
	{
		getCurrentBruker: DollyApi.getCurrentBruker,
		getCurrentBrukerProfil: ProfilApi.getProfil,
		getCurrentBrukerBilde: ProfilApi.getProfilBilde,
		addFavorite: DollyApi.addFavorite,
		removeFavorite: DollyApi.removeFavorite
	},
	{ prefix: 'bruker' }
)

const initialState = {
	brukerData: null,
	brukerProfil: null,
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
		[onSuccess(getCurrentBrukerProfil)](state, action) {
			state.brukerProfil = action.payload ? action.payload.data : null
		},
		[onSuccess(getCurrentBrukerBilde)](state, action) {
			state.brukerBilde = action.payload ? action.payload.data : null
		}
	},
	initialState
)
