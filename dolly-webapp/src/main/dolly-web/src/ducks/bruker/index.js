import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

export const getCurrentBruker = createAction('GET_CURRENT_BRUKER', DollyApi.getCurrentBruker)
export const addFavorite = createAction('ADD_FAVORITE', DollyApi.addFavorite)
export const removeFavorite = createAction('REMOVE_FAVORITE', DollyApi.removeFavorite)

const initialState = {
	brukerData: null
}

const successActions = combineActions(
	success(getCurrentBruker),
	success(addFavorite),
	success(removeFavorite)
)

export default handleActions(
	{
		[successActions](state, action) {
			return { ...state, brukerData: action.payload.data }
		}
	},
	initialState
)
