import { DollyApi } from '~/service/Api'
import { createActions } from 'redux-actions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { LOCATION_CHANGE } from 'redux-first-history'

export const { navigerTilPerson } = createActions({
	navigerTilPerson: DollyApi.navigerTilPerson,
})

const initialState = {
	visPerson: null,
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			if (action.payload.action !== 'REPLACE') return initialState
		},
		[onSuccess(navigerTilPerson)](state, action) {
			state.visPerson = action.payload.data.identHovedperson
		},
	},
	initialState
)
