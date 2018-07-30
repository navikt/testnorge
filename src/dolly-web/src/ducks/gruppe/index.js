import { DollyApi } from '~/service/Api'
import { getTpsfBruker } from '../testBruker'

export const types = {
	GET_GRUPPE_REQUEST: 'gruppe/get-request',
	GET_GRUPPE_SUCCESS: 'gruppe/get-success',
	GET_GRUPPE_ERROR: 'gruppe/get-error'
}

const initialState = {
	fetching: false,
	data: null
}

export default function gruppeReducer(state = initialState, action) {
	switch (action.type) {
		case types.GET_GRUPPE_REQUEST:
			return { ...state, fetching: true }
		case types.GET_GRUPPE_SUCCESS:
			return { ...state, fetching: false, data: action.data }
		case types.GET_GRUPPE_ERROR:
			return { ...state, fetching: false, error: action.error }
		default:
			return state
	}
}

const getGruppeRequest = () => ({
	type: types.GET_GRUPPE_REQUEST
})

const getGruppeSuccess = data => ({
	type: types.GET_GRUPPE_SUCCESS,
	data
})

const getGruppeError = error => ({
	type: types.GET_GRUPPE_ERROR,
	error
})

export const getGruppe = groupId => async dispatch => {
	try {
		dispatch(getGruppeRequest())
		const res = await DollyApi.getGruppeById(groupId)
		dispatch(getGruppeSuccess(res.data))
		if (res.data.testidenter.length > 0) {
			const brukerListe = res.data.testidenter.map(ident => ident.ident)
			return dispatch(getTpsfBruker(brukerListe))
		}
	} catch (error) {
		dispatch(getGruppeError(error))
	}
}
