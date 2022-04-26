import { DollyApi } from '~/service/Api'
import { createActions } from 'redux-actions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { handleActions } from '~/ducks/utils/immerHandleActions'

export const { navigerTilPerson } = createActions({
	navigerTilPerson: DollyApi.navigerTilPerson,
})

const initialState = {
	visPerson: null,
}

export default handleActions(
	{
		[onSuccess(navigerTilPerson)](state, action) {
			state.visPerson = action.payload.data.identHovedperson
		},
	},
	initialState
)
