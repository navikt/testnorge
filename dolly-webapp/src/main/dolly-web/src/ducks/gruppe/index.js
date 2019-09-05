import { DollyApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { createAction, handleActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'
import _find from 'lodash/find'

// GET
export const getGruppe = createAction('GET_GRUPPE', DollyApi.getGruppeById)
export const getGrupper = createAction('GET_GRUPPER', DollyApi.getGrupper)
export const getGrupperByTeamId = createAction('GET_GRUPPER_BY_TEAM_ID', DollyApi.getGruppeByTeamId)
export const getGrupperByUserId = createAction('GET_GRUPPER_BY_USER_ID', DollyApi.getGruppeByUserId)

// CRUD
export const createGruppe = createAction('CREATE_GRUPPE', DollyApi.createGruppe)
export const updateGruppe = createAction('UPDATE_GRUPPE', DollyApi.updateGruppe)
export const deleteGruppe = createAction('DELETE_GRUPPE', DollyApi.deleteGruppe, gruppeId => ({
	gruppeId
}))
export const createTeam = createAction('CREATE_TEAM', DollyApi.createTeam)

// UI
export const showCreateOrEditGroup = createAction('TOGGLE_SHOW_CREATE_OR_EDIT')
export const closeCreateOrEdit = createAction('CANCEL_CREATE_OR_EDIT')
export const settVisning = createAction('SETT_VISNING')

const initialState = {
	data: null,
	createOrUpdateId: null, // null = ingen, -1 = opprett ny gruppe, '45235' (ex: 425323) = rediger
	visning: 'mine',
	teamId: null
}

const getSuccess = combineActions(
	success(getGruppe),
	success(getGrupper),
	success(getGrupperByTeamId),
	success(getGrupperByUserId)
)

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[getSuccess](state, action) {
			const { data } = action.payload
			return { ...state, data: Array.isArray(data) ? data : [data] }
		},
		[success(updateGruppe)](state, action) {
			return {
				...state,
				createOrUpdateId: null,
				data: state.data.map((item, idx) => ({
					...item,
					...(item.id === action.payload.data.id && action.payload.data)
				}))
			}
		},
		[success(deleteGruppe)](state, action) {
			return {
				...state,
				data: state.data.filter(item => item.id !== action.meta.gruppeId)
			}
		},
		[success(createTeam)](state, action) {
			return {
				...state,
				teamId: action.payload.data.id.toString()
			}
		},
		[showCreateOrEditGroup](state, action) {
			return { ...state, createOrUpdateId: action.payload }
		},
		[closeCreateOrEdit](state, action) {
			return { ...state, createOrUpdateId: null }
		},
		[settVisning](state, action) {
			return { ...state, visning: action.payload }
		}
	},
	initialState
)

// THunk
export const listGrupper = ({ teamId = null } = {}) => async (dispatch, getState) => {
	const state = getState()

	const { visning } = state.gruppe
	const { navIdent } = state.bruker.brukerData

	if (teamId) {
		return dispatch(getGrupperByTeamId(teamId))
	} else if (visning === 'mine') {
		return dispatch(getGrupperByUserId(navIdent))
	} else {
		return dispatch(getGrupper())
	}
}

// Selector
export const sokSelectorOversikt = (items, searchStr) => {
	if (!items) return null
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item => {
		const searchValues = [
			_get(item, 'id'),
			_get(item, 'navn'),
			_get(item, 'hensikt'),
			_get(item, 'team.navn'),
			_get(item, 'testidenter', []).length
		]
			.filter(v => !_isNil(v))
			.map(v => v.toString().toLowerCase())

		return searchValues.some(v => v.includes(query))
	})
}
