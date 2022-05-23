import { DollyApi } from '~/service/Api'
import { combineActions, createActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'

export const { addFavorite, removeFavorite } = createActions(
	{
		addFavorite: DollyApi.addFavorite,
		removeFavorite: DollyApi.removeFavorite,
	},
	{ prefix: 'bruker' }
)

const initialState = {
	brukerData: null,
}

const successActions = combineActions(onSuccess(addFavorite), onSuccess(removeFavorite))

export default handleActions(
	{
		[successActions](state, action) {
			state.brukerData = action.payload.data
		},
	},
	initialState
)
