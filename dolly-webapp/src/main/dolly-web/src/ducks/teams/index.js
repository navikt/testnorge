import { DollyApi } from '~/service/Api'
import history from '~/history'
import { createActions, handleActions, combineActions } from 'redux-actions'

export const actions = createActions(
	{
		API: {
			GET: () => DollyApi.getTeams(),
			GET_BY_USER_ID: currentUserId => DollyApi.getTeamsByUserId(currentUserId),
			CREATE: newTeam => DollyApi.createTeam(newTeam),
			UPDATE: (teamId, data) => DollyApi.updateTeam(teamId, data),
			DELETE: [teamId => DollyApi.deleteTeam(teamId), teamId => ({ teamId })]
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
		[combineActions(`${actions.api.get}_SUCCESS`, `${actions.api.getByUserId}_SUCCESS`)]: (
			state,
			action
		) => ({
			...state,
			items: action.payload.data
		}),
		[`${actions.api.create}_SUCCESS`]: (state, action) => ({
			...state,
			visOpprettTeam: false,
			items: [...state.items, action.payload.data]
		}),
		[`${actions.api.update}_SUCCESS`]: (state, action) => ({
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
		[`${actions.api.delete}_SUCCESS`]: (state, action) => ({
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
