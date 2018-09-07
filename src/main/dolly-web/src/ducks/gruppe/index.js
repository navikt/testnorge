import { DollyApi } from '~/service/Api'
import { getTpsfBruker } from '../testBruker'
import { LOCATION_CHANGE } from 'connected-react-router'

export const types = {
	GET_GRUPPE_REQUEST: 'gruppe/get-request',
	GET_GRUPPE_SUCCESS: 'gruppe/get-success',
	GET_GRUPPE_ERROR: 'gruppe/get-error',

	TOGGLE_CREATE_OR_UPDATE: 'gruppe/toggle-create-or-update'
}

const initialState = {
	fetching: false,
	createOrUpdateId: null, // null = ingen, -1 = opprett ny gruppe, '45235' (ex: 425323) = rediger
	data: null
}

export default function gruppeReducer(state = initialState, action) {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case types.GET_GRUPPE_REQUEST:
			return { ...state, fetching: true }
		case types.GET_GRUPPE_SUCCESS:
			return { ...state, fetching: false, data: action.data }
		case types.GET_GRUPPE_ERROR:
			return { ...state, fetching: false, error: action.error }
		case types.TOGGLE_CREATE_OR_UPDATE:
			return { ...state, createOrUpdateId: action.editId }
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

export const showCreateOrEditGroup = editId => ({
	type: types.TOGGLE_CREATE_OR_UPDATE,
	editId
})

export const closeCreateOrEdit = () => ({ type: types.TOGGLE_CREATE_OR_UPDATE, editId: null })

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

		// If 404 -> redirect to homepage
		if (error.response && error.response.status === 404) {
			window.location.replace('/')
		}
	}
}
