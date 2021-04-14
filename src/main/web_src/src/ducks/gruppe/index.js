import { LOCATION_CHANGE } from 'connected-react-router'
import { createActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import _omit from 'lodash/omit'
import { DollyApi } from '~/service/Api'
import { createLoadingSelector } from '~/ducks/loading'
import { onSuccess } from '~/ducks/utils/requestActions'
import { handleActions } from '~/ducks/utils/immerHandleActions'

export const actions = createActions(
	{
		getById: DollyApi.getGruppeByIdPaginert,
		getAlle: DollyApi.getGrupperPaginert,
		getByUserId: DollyApi.getGruppeByUserId,
		create: DollyApi.createGruppe,
		update: DollyApi.updateGruppe,
		laas: DollyApi.updateGruppeLaas,
		remove: [
			DollyApi.deleteGruppe,
			gruppeId => ({
				gruppeId
			})
		],
		updateIdentIbruk: DollyApi.updateIdentIbruk,
		updateBeskrivelse: DollyApi.updateIdentBeskrivelse,
		importZIdent: DollyApi.importZIdent
	},
	{
		prefix: 'gruppe' // String used to prefix each type
	}
)

const initialState = {
	ident: {},
	byId: {},
	gruppeInfo: {},
	mineIds: [],
	importerteZIdenter: null
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(actions.getById)](state, action) {
			const gruppe = action.payload.data
			state.gruppeInfo = action.payload.data
			state.ident =
				gruppe.identer &&
				gruppe.identer.reduce((acc, curr) => {
					acc[curr.ident] = { ...curr, gruppeId: gruppe.id }
					return acc
				}, {})
			state.byId[gruppe.id] = _omit(gruppe, 'identer')
		},
		[onSuccess(actions.getAlle)](state, action) {
			state.byId = []
			state.gruppeInfo = action.payload.data
			action.payload.data.contents.forEach(gruppe => {
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
		[onSuccess(actions.laas)](state, action) {
			state.byId[action.payload.data.id] = action.payload.data
		},
		[onSuccess(actions.updateIdentIbruk)](state, action) {
			const { ident, ibruk } = action.payload.data
			state.ident[ident].ibruk = ibruk
		},
		[onSuccess(actions.updateBeskrivelse)](state, action) {
			const { ident, beskrivelse } = action.payload.data
			state.ident[ident].beskrivelse = beskrivelse
		},
		[onSuccess(actions.remove)](state, action) {
			delete state.byId[action.meta.gruppeId]
			state.mineIds = state.mineIds.filter(v => v !== action.meta.gruppeId)
		},
		[onSuccess(actions.importZIdent)](state, action) {
			state.importerteZIdenter = action.payload.data
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
	const items = Object.values(gruppe.byId)

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
