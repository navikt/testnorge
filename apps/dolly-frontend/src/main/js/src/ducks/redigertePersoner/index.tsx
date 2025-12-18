import { handleActions } from '@/ducks/utils/immerHandleActions'
import { LOCATION_CHANGE } from 'redux-first-history'

const initialState = {
	pdlforvalter: {},
}

export default handleActions(
	{
		[LOCATION_CHANGE]() {
			return initialState
		},
	},
	initialState,
)
