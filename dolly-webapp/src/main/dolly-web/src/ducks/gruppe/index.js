import { DollyApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction, handleActions } from 'redux-actions'

export const getGruppe = createAction('GET_GRUPPE', groupId => DollyApi.getGruppeById(groupId))
export const showCreateOrEditGroup = createAction('TOGGLE_SHOW_CREATE_OR_EDIT')
export const closeCreateOrEdit = createAction('CANCEL_CREATE_OR_EDIT')

const initialState = {
	createOrUpdateId: null, // null = ingen, -1 = opprett ny gruppe, '45235' (ex: 425323) = rediger
	data: null
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[`${getGruppe}_SUCCESS`](state, action) {
			return { ...state, data: action.payload.data }
		},
		[showCreateOrEditGroup](state, action) {
			return { ...state, createOrUpdateId: action.payload }
		},
		[closeCreateOrEdit](state, action) {
			return { ...state, createOrUpdateId: null }
		}
	},
	initialState
)
