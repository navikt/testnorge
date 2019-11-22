import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import { onSuccess } from '~/ducks/utils/requestActions'

export const getCurrentBruker = createAction('GET_CURRENT_BRUKER', DollyApi.getCurrentBruker)
export const addFavorite = createAction('ADD_FAVORITE', DollyApi.addFavorite)
export const removeFavorite = createAction('REMOVE_FAVORITE', DollyApi.removeFavorite)

const initialState = {
	brukerData: null
}

const successActions = combineActions(
	onSuccess(getCurrentBruker),
	onSuccess(addFavorite),
	onSuccess(removeFavorite)
)

export default handleActions(
	{
		[successActions](state, action) {
			return { ...state, brukerData: action.payload.data }
		}
	},
	initialState
)
