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
	UPDATE_GRUPPER_ERROR: 'grupper/update-error',

	SETT_VISNING: 'grupper/sett-visning',
	START_OPPRETT_GRUPPE: 'grupper/start-opprett-gruppe',
	START_REDIGER_GRUPPE: 'grupper/start-rediger-gruppe',
	CANCEL_REDIGER_OG_OPPRETT: 'grupper/cancel-rediger-og-opprett'
}

const initialState = {
	fetching: false,
	items: null,
	error: null,
	visning: 'mine',
	editId: null,
	visOpprettGruppe: false
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
				items: [...state.items.push(action.gruppe)],
				visOpprettGruppe: false
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
				items: state.items.map((item, idx) => ({
					...item,
					...(item.id === action.gruppe.id && action.gruppe)
				})),
				editId: null
			}
		case types.UPDATE_GRUPPER_ERROR:
			return {
				...state,
				fetching: false,
				error: action.error
			}
		case types.SETT_VISNING:
			return {
				...state,
				visning: action.visning
			}
		case types.START_OPPRETT_GRUPPE:
			return {
				...state,
				visOpprettGruppe: true,
				editId: null
			}
		case types.START_REDIGER_GRUPPE:
			return {
				...state,
				editId: action.editId,
				visOpprettGruppe: false
			}
		case types.CANCEL_REDIGER_OG_OPPRETT:
			return {
				...state,
				visOpprettGruppe: false,
				editId: null
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

const updateGrupperSuccess = gruppe => ({
	type: types.UPDATE_GRUPPER_SUCCESS,
	gruppe
})

const updateGrupperError = error => ({
	type: types.UPDATE_GRUPPER_ERROR,
	error
})

export const settVisning = visning => ({ type: types.SETT_VISNING, visning })
export const startRedigerGruppe = editId => ({ type: types.START_REDIGER_GRUPPE, editId })
export const startOpprettGruppe = () => ({ type: types.START_OPPRETT_GRUPPE })
export const cancelRedigerOgOpprett = () => ({ type: types.CANCEL_REDIGER_OG_OPPRETT })

// THUNKS
export const getGrupper = () => async (dispatch, getState) => {
	const { visning } = getState().grupper
	try {
		// TODO: Use actual userID from login
		dispatch(getGrupperRequest())
		const response =
			visning !== 'mine' ? await DollyApi.getGrupper() : await DollyApi.getGruppeByUserId('Neymar')

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

export const updateGruppe = gruppe => async (dispatch, getState) => {
	try {
		dispatch(updateGrupperRequest())
		const response = await DollyApi.updateGruppe(gruppe.id, gruppe)
		dispatch(updateGrupperSuccess(response.data))
	} catch (error) {
		dispatch(updateGrupperError(error))
	}
}
