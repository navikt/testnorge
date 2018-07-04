import { DollyApi } from '~/service/Api'
import DollyCheckbox from '~/components/fields/Checkbox/Checkbox'

export const types = {
	REQUEST_CURRENT_BRUKER: 'REQUEST_CURRENT_BRUKER',
	REQUEST_CURRENT_BRUKER_SUCCESS: 'REQUEST_CURRENT_BRUKER_SUCCESS',
	REQUEST_CURRENT_BRUKER_ERROR: 'REQUEST_CURRENT_BRUKER_ERROR'
}

const initialState = {
	brukerData: null,
	fetching: false
}

export default function brukerReducer(state = initialState, action) {
	switch (action.type) {
		case types.REQUEST_CURRENT_BRUKER:
			return { ...state, fetching: true }
		case types.REQUEST_CURRENT_BRUKER_SUCCESS:
			return { ...state, brukerData: action.bruker }
		case types.REQUEST_CURRENT_BRUKER_ERROR:
			return { ...state, error: action.error }
		default:
			return state
	}
}

const requestCurrentBruker = () => ({
	type: types.REQUEST_CURRENT_BRUKER
})

const requestCurrentBrukerSuccess = bruker => ({
	type: types.REQUEST_CURRENT_BRUKER_SUCCESS,
	bruker
})

const requestCurrentBrukerError = error => ({
	type: types.REQUEST_CURRENT_BRUKER_ERROR,
	error
})

export const fetchCurrentBruker = () => async dispatch => {
	try {
		dispatch(requestCurrentBruker())
		const response = await DollyApi.getCurrentBruker()
		dispatch(requestCurrentBrukerSuccess(response.data))
	} catch (error) {
		dispatch(requestCurrentBrukerError(error))
	}
}
