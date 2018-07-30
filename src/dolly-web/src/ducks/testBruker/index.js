import { TpsfApi } from '~/service/Api'
import TpsfTransformer from './tpsfTransformer'

export const types = {
	REQUEST_TPSF_BRUKER: 'testbruker/get_tpsf_bruker',
	REQUEST_TPSF_BRUKER_SUCCESS: 'testbruker/get_tpsf_bruker_success',
	REQUEST_TPSF_BRUKER_ERROR: 'testbruker/get_tpsf_bruker_error'
}

const initialState = {
	items: null,
	fetching: false
}

export default function testbrukerReducer(state = initialState, action) {
	switch (action.type) {
		case types.REQUEST_TPSF_BRUKER:
			return { ...state, fetching: true }
		case types.REQUEST_TPSF_BRUKER_SUCCESS:
			return { ...state, fetching: false, items: action.brukere }
		case types.REQUEST_TPSF_BRUKER_ERROR:
			return { ...state, fetching: false, error: action.error }
		default:
			return state
	}
}

const requestTpsfBruker = () => ({
	type: types.REQUEST_TPSF_BRUKER
})

const requestTpsfBrukerSuccess = brukere => ({
	type: types.REQUEST_TPSF_BRUKER_SUCCESS,
	brukere
})

const requestTpsfBrukerError = error => ({
	type: types.REQUEST_TPSF_BRUKER_ERROR,
	error
})

export const getTpsfBruker = userArray => async dispatch => {
	try {
		dispatch(requestTpsfBruker())
		const result = await TpsfApi.getTestbrukere(userArray)
		dispatch(requestTpsfBrukerSuccess(TpsfTransformer(result.data)))
	} catch (err) {
		return dispatch(requestTpsfBrukerError(err))
	}
}
