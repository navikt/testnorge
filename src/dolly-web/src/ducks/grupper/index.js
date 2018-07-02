import { DollyApi } from '~/service/Api'

export const types = {
	GET_GRUPPER_REQUEST: 'grupper/get-request',
	GET_GRUPPER_SUCCESS: 'grupper/get-success',
	GET_GRUPPER_ERROR: 'grupper/get-error',

	CREATE_GRUPPER_REQUEST: 'grupper/create-request',
	CREATE_GRUPPER_SUCCESS: 'grupper/create-success',
	CREATE_GRUPPER_ERROR: 'grupper/create-error',

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
		case types.CREATE_GRUPPER_REQUEST:
			return {
				...state,
				fetching: true
			}
		case types.CREATE_GRUPPER_SUCCESS:
			return {
				...state,
				fetching: false,
				items: [...state.items.push(action.gruppe)]
			}
		case types.CREATE_GRUPPER_ERROR:
			return {
				...state,
				fetching: false,
				error: action.error
			}
		case types.UPDATE_GRUPPER_REQUEST:
			return {
				...state,
				fetching: true
			}
		case types.UPDATE_GRUPPER_SUCCESS:
			return {
				...state,
				fetching: false,
				items: state.items.map((item, idx) => {
					if (index !== action.index) {
						return item
					}
					return {
						...item,
						...action.grupper
					}
				})
			}
		case types.UPDATE_GRUPPER_ERROR:
			return {
				...state,
				fetching: false,
				error: action.error
			}
		default:
			return state
	}
}

const getGrupperRequest = () => ({
	type: types.GET_GRUPPER_REQUEST
})

const getGrupperSuccess = grupper => ({
	type: types.GET_GRUPPER_SUCCESS,
	grupper
})

const getGrupperError = error => ({
	type: types.GET_GRUPPER_ERROR,
	error
})

const createGrupperRequest = () => ({
	type: types.CREATE_GRUPPER_REQUEST
})

const createGrupperSuccess = gruppe => ({
	type: types.CREATE_GRUPPER_SUCCESS,
	gruppe
})

const createGrupperError = error => ({
	type: types.CREATE_GRUPPER_ERROR,
	error
})

const updateGrupperRequest = () => ({
	type: types.UPDATE_GRUPPER_REQUEST
})

const updateGrupperSuccess = (index, gruppe) => ({
	type: types.UPDATE_GRUPPER_SUCCESS,
	index,
	gruppe
})

const updateGrupperError = error => ({
	type: types.UPDATE_GRUPPER_ERROR,
	error
})

// THUNKS

export const getGrupper = visning => async dispatch => {
	try {
		// TODO: Use actual userID from login
		dispatch(getGrupperRequest())
		const response =
			visning === 'mine' ? await DollyApi.getGrupper() : await DollyApi.getGruppeByUserId('Neymar')
		return dispatch(getGrupperSuccess(response.data))
	} catch (error) {
		return dispatch(getGrupperError(error))
	}
}

export const createGruppe = nyGruppe => async dispatch => {
	try {
		dispatch(createGrupperRequest())
		const response = await DollyApi.createGruppe(nyGruppe)
		dispatch(createGrupperSuccess(response.data))
	} catch (error) {
		dispatch(createGrupperError(error))
	}
}

export const updateGruppe = (index, gruppe) => async (dispatch, getState) => {
	try {
		dispatch(updateGrupperRequest())
		const response = await DollyApi.updateGruppe(gruppe.id, gruppe)
		dispatch(updateGrupperSuccess(index, response.data))
	} catch (error) {
		dispatch(updateGrupperError(error))
	}
}
