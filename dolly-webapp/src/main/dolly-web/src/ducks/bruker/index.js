import { DollyApi } from '~/service/Api'
import Cookie from '~/utils/Cookie'

export const types = {
	REQUEST_CURRENT_BRUKER: 'bruker/get-request',
	REQUEST_CURRENT_BRUKER_SUCCESS: 'bruker/get-success',
	REQUEST_CURRENT_BRUKER_ERROR: 'bruker/get-error',
	SET_BRUKER_DATA: 'bruker/set-bruker-data'
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
			return { ...state, fetching: false, brukerData: action.bruker }
		case types.REQUEST_CURRENT_BRUKER_ERROR:
			return { ...state, fetching: false, error: action.error }.brukerData
		case types.SET_BRUKER_DATA:
			return { ...state, brukerData: action.brukerData }
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

const setBrukerData = brukerData => ({
	type: types.SET_BRUKER_DATA,
	brukerData
})

export const fetchCurrentBruker = () => async dispatch => {
	try {
		dispatch(requestCurrentBruker())
		const response = await DollyApi.getCurrentBruker()
		const brukerData = response.data
		if (Cookie.hasItem(brukerData.navIdent)) {
			return dispatch(requestCurrentBrukerSuccess(brukerData))
		}
		brukerData.showSplashscreen = true
		return dispatch(requestCurrentBrukerSuccess(brukerData))
	} catch (error) {
		dispatch(requestCurrentBrukerError(error))
	}
}

export const setSplashscreenAccepted = () => (dispatch, getState) => {
	const { bruker } = getState()
	const brukerData = bruker.brukerData
	Cookie.setItem(brukerData.navIdent, true, Infinity)
	brukerData.showSplashscreen = false
	return dispatch(setBrukerData(brukerData))
}
