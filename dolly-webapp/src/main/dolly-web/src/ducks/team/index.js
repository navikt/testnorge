import { DollyApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import { createActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

export const actions = createActions({
	TEAM: {
		GET: teamId => DollyApi.getTeamById(teamId),
		ADD_MEMBER: (teamId, userArray) => DollyApi.addTeamMedlemmer(teamId, userArray),
		REMOVE_MEMBER: (teamId, userArray) => DollyApi.removeTeamMedlemmer(teamId, userArray)
	}
})

const initialState = {
	data: null
}

export default function teamReducer(state = initialState, action) {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case success(actions.team.get):
		case success(actions.team.addMember):
		case success(actions.team.removeMember):
			return { ...state, data: action.payload.data }
		default:
			return state
	}
}
