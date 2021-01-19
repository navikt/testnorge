import { DollyApi } from '~/service/Api'
import { createActions } from 'redux-actions'
import { LOCATION_CHANGE } from 'connected-react-router'
import { onSuccess } from '~/ducks/utils/requestActions'
import { handleActions } from '~/ducks/utils/immerHandleActions'

export const { hentFraPdl } = createActions({
	hentFraPdl: DollyApi.getPersonFraPdl
})

const initialState = {
	visPerson: null
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			if (action.payload.action !== 'REPLACE') return initialState
		},
		[onSuccess(hentFraPdl)](state, action) {
			state.visPerson = action.payload.data
		}
	},
	initialState
)
