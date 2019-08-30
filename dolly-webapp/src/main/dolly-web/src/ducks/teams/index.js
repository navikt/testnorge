import { DollyApi } from '~/service/Api'
import { createActions, handleActions, combineActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { LOCATION_CHANGE } from 'connected-react-router'
import success from '~/utils/SuccessAction'

export const actions = createActions(
	{
		API: {
			GET: () => DollyApi.getTeams(),
			GET_BY_ID: teamId => DollyApi.getTeamById(teamId),
			GET_BY_USER_ID: currentUserId => DollyApi.getTeamsByUserId(currentUserId),
			CREATE: newTeam => DollyApi.createTeam(newTeam),
			UPDATE: (teamId, data) => DollyApi.updateTeam(teamId, data),
			DELETE: [teamId => DollyApi.deleteTeam(teamId), teamId => ({ teamId })],
			ADD_TEAM_MEMBER: (teamId, userArray) => DollyApi.addTeamMedlemmer(teamId, userArray),
			REMOVE_TEAM_MEMBER: (teamId, user) => DollyApi.removeTeamMedlemmer(teamId, user)
		},
		UI: {
			SET_TEAM_VISNING: visning => ({ visning }),
			START_CREATE_TEAM: undefined,
			START_EDIT_TEAM: teamId => ({ teamId }),
			CLOSE_CREATE_EDIT_TEAM: undefined
		}
	},
	{ prefix: 'teams' }
)

const initialState = {
	items: [],
	visning: 'mine',
	visOpprettTeam: false,
	editTeamId: null
}

export default handleActions(
	{
		[LOCATION_CHANGE]: () => initialState,
		[combineActions(success(actions.api.get), success(actions.api.getByUserId))]: (
			state,
			action
		) => ({
			...state,
			items: action.payload.data
		}),
		[combineActions(
			success(actions.api.getById),
			success(actions.api.addTeamMember),
			success(actions.api.removeTeamMember)
		)]: (state, action) => ({
			...state,
			items: [action.payload.data]
		}),
		[success(actions.api.create)]: (state, action) => ({
			...state,
			visOpprettTeam: false,
			items: [...state.items, action.payload.data]
		}),
		[success(actions.api.update)]: (state, action) => ({
			...state,
			editTeamId: null,
			items: state.items.map(item => {
				if (item.id !== action.payload.data.id) return item
				return {
					...item,
					...action.payload.data
				}
			})
		}),
		[success(actions.api.delete)]: (state, action) => ({
			...state,
			items: state.items.filter(item => item.id !== action.meta.teamId)
		}),
		[actions.ui.setTeamVisning]: (state, action) => ({
			...state,
			visning: action.payload.visning
		}),
		[actions.ui.startCreateTeam]: (state, action) => ({
			...state,
			visOpprettTeam: true,
			editTeamId: null
		}),
		[actions.ui.startEditTeam]: (state, action) => ({
			...state,
			visOpprettTeam: false,
			editTeamId: action.payload.teamId
		}),
		[actions.ui.closeCreateEditTeam]: (state, action) => ({
			...state,
			visOpprettTeam: false,
			editTeamId: null
		})
	},
	initialState
)

// THUNKS
export const fetchTeams = () => async (dispatch, getState) => {
	const { bruker, teams } = getState()
	const currentBrukerId = bruker.brukerData.navIdent
	const currentVisning = teams.visning

	const reqAction =
		currentVisning === 'mine' ? actions.api.getByUserId(currentBrukerId) : actions.api.get()

	return dispatch(reqAction)
}

// Selector
export const sokSelector = (items, searchStr) => {
	if (!items) return null
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item => {
		const searchValues = [
			_get(item, 'id'),
			_get(item, 'navn'),
			_get(item, 'beskrivelse'),
			_get(item, 'eierNavIdent'),
			_get(item, 'medlemmer', []).length
		]
			.filter(v => !_isNil(v))
			.map(v => v.toString().toLowerCase())

		return searchValues.some(v => v.includes(query))
	})
}
