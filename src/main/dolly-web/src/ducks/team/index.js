import { DollyApi } from '~/service/Api'
import { getGrupper } from '../grupper'
import { LOCATION_CHANGE } from 'connected-react-router'

export const types = {
	GET_TEAM_REQUEST: 'team/get-request',
	GET_TEAM_SUCCESS: 'team/get-success',
	GET_TEAM_ERROR: 'team/get-error',

	ADD_MEMBER_REQUEST: 'team/add-member-request',
	ADD_MEMBER_SUCCESS: 'team/add-member-success',
	ADD_MEMBER_ERROR: 'team/add-member-error',

	REMOVE_MEMBER_REQUEST: 'team/remove-member-request',
	REMOVE_MEMBER_SUCCESS: 'team/remove-member-success',
	REMOVE_MEMBER_ERROR: 'team/remove-member-error'
}

const initialState = {
	fetching: false,
	data: null
}

export default function teamReducer(state = initialState, action) {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case types.GET_TEAM_REQUEST:
		case types.ADD_MEMBER_REQUEST:
		case types.REMOVE_MEMBER_REQUEST:
			return { ...state, fetching: true }
		case types.GET_TEAM_SUCCESS:
		case types.ADD_MEMBER_SUCCESS:
		case types.REMOVE_MEMBER_SUCCESS:
			return { ...state, fetching: false, data: action.data }
		case types.GET_TEAM_ERROR:
		case types.ADD_MEMBER_ERROR:
		case types.REMOVE_MEMBER_ERROR:
			return { ...state, fetching: false, error: action.error }
		default:
			return state
	}
}

const getTeamRequest = () => ({ type: types.GET_TEAM_REQUEST })
const getTeamSuccess = data => ({ type: types.GET_TEAM_SUCCESS, data })
const getTeamError = error => ({ type: types.GET_TEAM_ERROR, error })

const addMemberRequest = () => ({ type: types.ADD_MEMBER_REQUEST })
const addMemberSuccess = data => ({ type: types.ADD_MEMBER_SUCCESS, data })
const addMemberError = error => ({ type: types.ADD_MEMBER_ERROR, error })

const removeMemberRequest = () => ({ type: types.REMOVE_MEMBER_REQUEST })
const removeMemberSuccess = data => ({ type: types.REMOVE_MEMBER_SUCCESS, data })
const removeMemberError = error => ({ type: types.REMOVE_MEMBER_ERROR, error })

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

export const addMember = (teamId, userArray) => async dispatch => {
	try {
		dispatch(addMemberRequest())
		const res = await DollyApi.addTeamMedlemmer(teamId, userArray)
		dispatch(addMemberSuccess(res.data))
	} catch (error) {
		dispatch(addMemberError(error))
	}
}

export const removeMember = (teamId, userArray) => async dispatch => {
	try {
		dispatch(removeMemberRequest())
		const res = await DollyApi.removeTeamMedlemmer(teamId, userArray)
		dispatch(removeMemberSuccess(res.data))
	} catch (error) {
		dispatch(removeMemberError(error))
	}
}
