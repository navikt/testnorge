import { LOCATION_CHANGE } from 'connected-react-router'
import { createActions, combineActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import _find from 'lodash/find'
import { DollyApi } from '~/service/Api'
import { createLoadingSelector } from '~/ducks/loading'
import { onSuccess } from '~/ducks/utils/requestActions'
import { handleActions } from '~/ducks/utils/immerHandleActions'

export const actions = createActions(
	{
		getById: DollyApi.getGruppeById,
		getAlle: DollyApi.getGrupper,
		getByUserId: DollyApi.getGruppeByUserId,
		create: DollyApi.createGruppe,
		update: DollyApi.updateGruppe,
		remove: [
			DollyApi.deleteGruppe,
			gruppeId => ({
				gruppeId
			})
		],
		updateBeskrivelse: DollyApi.updateBeskrivelse
	},
	{
		prefix: 'gruppe' // String used to prefix each type
	}
)

const initialState = {
	ident: {},
	byId: {},
	mineIds: []
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(actions.getById)](state, action) {
			const gruppe = action.payload.data
			state.ident = gruppe.identer.reduce((acc, curr) => {
				acc[curr.ident] = { ...curr, gruppeId: gruppe.id }
				return acc
			}, {})
			state.byId[gruppe.id] = gruppe
		},
		[onSuccess(actions.getAlle)](state, action) {
			action.payload.data.forEach(gruppe => {
				state.byId[gruppe.id] = gruppe
			})
		},
		[onSuccess(actions.getByUserId)](state, action) {
			state.mineIds = action.payload.data.map(v => v.id)
			action.payload.data.forEach(gruppe => {
				state.byId[gruppe.id] = gruppe
			})
		},
		[onSuccess(actions.update)](state, action) {
			state.byId[action.payload.data.id] = action.payload.data
		},
		[onSuccess(actions.updateBeskrivelse)](state, action) {
			const { ident, beskrivelse } = action.payload.data
			state.ident[ident].beskrivelse = beskrivelse
		},
		[onSuccess(actions.remove)](state, action) {
			delete state.byId[action.meta.gruppeId]
			state.mineIds = state.mineIds.filter(v => v !== action.meta.gruppeId)
		}
	},
	initialState
)

// Thunk
export const fetchMineGrupper = () => async (dispatch, getState) => {
	const { brukerId } = getState().bruker.brukerData
	return dispatch(actions.getByUserId(brukerId))
}

// Selector
export const loadingGrupper = createLoadingSelector([
	actions.getById,
	actions.getAlle,
	actions.getByUserId
])

export const selectGruppeById = (state, gruppeId) => state.gruppe.byId[gruppeId]
export const selectIdentById = (state, ident) => state.gruppe.ident[ident]

export const sokSelectorGruppeOversikt = state => {
	const { search, gruppe } = state
	let items = Object.values(gruppe.byId)

	if (!items) return null
	if (!search) return items

	const query = search.toLowerCase()
	return items.filter(item => {
		const searchValues = [
			_get(item, 'id'),
			_get(item, 'navn'),
			_get(item, 'hensikt'),
			_get(item, 'identer', []).length
		]
			.filter(v => !_isNil(v))
			.map(v => v.toString().toLowerCase())

		return searchValues.some(v => v.includes(query))
	})
}
