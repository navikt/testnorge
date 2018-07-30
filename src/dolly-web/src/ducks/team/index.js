import { DollyApi } from '~/service/Api'
import { getGrupper } from '../grupper'

export const types = {
	GET_TEAM_REQUEST: 'team/get-request',
	GET_TEAM_SUCCESS: 'team/get-success',
	GET_TEAM_ERROR: 'team/get-error'
}

const initialState = {
	fetching: false,
	data: null
}

export default function teamReducer(state = initialState, action) {
	switch (action.type) {
		case types.GET_TEAM_REQUEST:
			return { ...state, fetching: true }
		case types.GET_TEAM_SUCCESS:
			return { ...state, fetching: false, data: action.data }
		case types.GET_TEAM_ERROR:
			return { ...state, fetching: false, error: action.error }
		default:
			return state
	}
}

const getTeamRequest = () => ({
	type: types.GET_TEAM_REQUEST
})

const getTeamSuccess = data => ({
	type: types.GET_TEAM_SUCCESS,
	data
})

const getTeamError = error => ({
	type: types.GET_TEAM_ERROR,
	error
})

export const getTeam = teamId => async dispatch => {
	try {
		dispatch(getTeamRequest())
		const res = await DollyApi.getTeamById(teamId)
		dispatch(getTeamSuccess(res.data))
		dispatch(getGrupper({ teamId }))
	} catch (error) {
		dispatch(getTeamError(error))
	}
}
