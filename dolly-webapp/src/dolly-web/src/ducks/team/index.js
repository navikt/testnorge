import { DollyApi } from '~/service/Api'

export const types = {
	REQUEST_TEAMS: 'REQUEST_TEAMS',
	REQUEST_TEAMS_SUCCESS: 'REQUEST_TEAM_SUCCESS',
	REQUEST_TEAMS_ERROR: 'REQUEST_TEAMS_ERROR',
	CREATE_TEAM_SUCCESS: 'CREATE_TEAM_SUCCESS',
	SET_TEAM_VISNING: 'SETT_TEAM_VISNING',
	CREATE_OR_UPDATE_TEAM_REQUEST: 'CREATE_OR_UPDATE_TEAM_REQUEST',
	CREATE_TEAM_SUCCESS: 'CREATE_TEAM_SUCCESS',
	UPDATE_TEAM_SUCCESS: 'UPDATE_TEAM_SUCCESS'
}

const initialState = {
	fetching: false,
	createOrUpdateFetching: false,
	items: [],
	visning: 'mine'
}

export default function teamReducer(state = initialState, action) {
	switch (action.type) {
		case types.REQUEST_TEAMS:
			return {
				...state,
				fetching: true
			}
		case types.REQUEST_TEAMS_SUCCESS:
			return {
				...state,
				fetching: false,
				items: action.teams
			}
		case types.REQUEST_TEAMS_ERROR:
			return {
				...state,
				fetching: false,
				error: action.error
			}
		case types.CREATE_OR_UPDATE_TEAM_REQUEST:
			return {
				...state,
				createOrUpdateFetching: true
			}
		case types.CREATE_TEAM_SUCCESS:
			return {
				...state,
				createOrUpdateFetching: false,
				items: [...state.items, action.team]
			}
		case types.SET_TEAM_VISNING:
			return {
				...state,
				visning: action.visning
			}

		default:
			return state
	}
}

const requestTeams = () => ({
	type: types.REQUEST_TEAMS
})

const requestTeamsSuccess = teams => ({
	type: types.REQUEST_TEAMS_SUCCESS,
	teams
})

const requestTeamsError = error => ({
	type: types.REQUEST_TEAMS_ERROR
})

const createOrUpdateTeamRequest = () => ({
	type: types.CREATE_OR_UPDATE_TEAM_REQUEST
})

const createTeamSuccess = team => ({
	type: types.CREATE_TEAM_SUCCESS,
	team
})

const updateTeamSuccess = (team, index) => ({
	type: types.UPDATE_TEAM_SUCCESS,
	team
})

export const setTeamVisning = visning => ({
	type: types.SET_TEAM_VISNING,
	visning
})

export const fetchTeams = () => async (dispatch, getState) => {
	const { bruker, team } = getState()
	const currentBrukerId = bruker.brukerData.navIdent
	const currentVisning = team.visning
	try {
		dispatch(requestTeams())

		const response =
			currentVisning === 'mine'
				? await DollyApi.getTeamsByUserId(currentBrukerId)
				: await DollyApi.getTeams()

		return dispatch(requestTeamsSuccess(response.data))
	} catch (error) {
		dispatch(requestTeamsError)
	}
}

export const createTeam = data => async dispatch => {
	try {
		dispatch(createOrUpdateTeamRequest())
		const response = await DollyApi.createTeam(data)
		dispatch(createTeamSuccess(response.data))
	} catch (error) {
		console.log(error)
	}
}
