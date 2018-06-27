import axios from 'axios'
import Endpoints from '~/service/ContentApiEndpoints'

export const types = {
	GET_GRUPPER_REQUEST: 'grupper/get-request',
	GET_GRUPPER_SUCCESS: 'grupper/get-success',
	GET_GRUPPER_ERROR: 'grupper/get-error',

	CREATE_GRUPPER_REQUEST: 'grupper/create-request',
	CREATE_GRUPPER_SUCCESS: 'grupper/create-success',
	CREATE_GRUPPER: 'grupper/create-error',

	UPDATE_GRUPPER_REQUEST: 'grupper/update-request',
	UPDATE_GRUPPER_SUCCESS: 'grupper/update-success',
	UPDATE_GRUPPER_ERROR: 'grupper/update-error'
}

const initialState = {
	fetching: false,
	items: null,
	error: null
}

export default (state = initialState, action) => {
	switch (action.type) {
		case types.GET_GRUPPER_REQUEST:
			return {
				...state,
				fetching: true
			}
		case types.GET_GRUPPER_SUCCESS:
			return {
				...state,
				fetching: false,
				items: action.grupper
			}
		case types.GET_GRUPPER_ERROR:
			return {
				...initialState,
				error: action.error
			}
		default:
			return state
	}
}

const getGrupperRequest = url => ({
	type: types.GET_GRUPPER_REQUEST,
	url
})

const getGrupperSuccess = grupper => ({
	type: types.GET_GRUPPER_SUCCESS,
	grupper
})

const getGrupperError = error => ({
	type: types.GET_GRUPPER_ERROR,
	error
})

const createGrupperRequest = url => ({
	type: types.CREATE_GRUPPER_REQUEST,
	url
})

const createGrupperSuccess = gruppe => ({
	type: types.CREATE_GRUPPER_SUCCESS,
	gruppe
})

const createGrupperError = error => ({
	type: types.CREATE_GRUPPER_ERROR,
	error
})

const updateGrupperRequest = url => ({
	type: types.UPDATE_GRUPPER_REQUEST,
	url
})

const updateGrupperSuccess = grupper => ({
	type: types.UPDATE_GRUPPER_SUCCESS,
	grupper
})

const updateGrupperError = error => ({
	type: types.UPDATE_GRUPPER_ERROR,
	error
})

// THUNKS

export const getGrupper = visning => async dispatch => {
	try {
		// TODO: Use actual userID from login
		const url = visning === 'mine' ? Endpoints.getGruppeByUser('Neymar') : Endpoints.getGrupper()
		dispatch(getGrupperRequest(url))
		const response = await axios.get(url)
		return dispatch(getGrupperSuccess(response.data))
	} catch (error) {
		return dispatch(getGrupperError(error))
	}
}

export const createGruppe = nyGruppe => async dispatch => {
	try {
		const url = Endpoints.postGruppe()
		dispatch(createGrupperRequest(url))
		const response = await axios.get(url, nyGruppe)
		dispatch(createGrupperSuccess(response.data))
	} catch (error) {
		dispatch(createGrupperError(error))
	}
}

export const updateGruppe = gruppe => async dispatch => {
	try {
		const url = Endpoints.putGruppe(gruppe.id)
		dispatch(updateGrupperRequest(url))
		const response = await axios.post(url, gruppe)
		dispatch(updateGruppeSuccess(response.data))
	} catch (error) {
		dispatch(updateGrupperError(error))
	}
}
