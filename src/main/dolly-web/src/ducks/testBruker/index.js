import { TpsfApi, SigrunApi, KrrApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction } from 'redux-actions'
import success from '~/utils/SuccessAction'
import _get from 'lodash/get'

export const UPDATE_TESTBRUKER = createAction('UPDATE_TESTBRUKER', TpsfApi.updateTestbruker)

const initialState = {
	items: {
		tpsf: null,
		sigrun: null,
		krr: null
	}
}

export const GET_TPSF_TESTBRUKERE = createAction('GET_TPSF_TESTBRUKERE', identArray => {
	return TpsfApi.getTestbrukere(identArray)
})

export const GET_SIGRUN_TESTBRUKER = createAction(
	'GET_SIGRUN_TESTBRUKER',
	ident => {
		return SigrunApi.getTestbruker(ident)
	},
	ident => ({
		ident
	})
)

export const GET_KRR_TESTBRUKER = createAction(
	'GET_KRR_TESTBRUKER',
	async ident => {
		try {
			const res = await KrrApi.getTestbruker(ident)
			console.log(res)
		} catch (err) {
			if (err.response && err.response.status === 404) {
				console.log(err.response.data.melding)
				return []
			}
			return err
		}
	},
	ident => ({
		ident
	})
)

export default function testbrukerReducer(state = initialState, action) {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case `${GET_TPSF_TESTBRUKERE}_SUCCESS`:
			return { ...state, items: { ...state.items, tpsf: action.payload.data } }
		case success(GET_SIGRUN_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					sigrun: { ...state.items.sigrun, [action.meta.ident]: action.payload.data }
				}
			}
		case success(GET_KRR_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					krr: { ...state.items.krr, [action.meta.ident]: action.payload.data }
				}
			}
		case `${UPDATE_TESTBRUKER}_SUCCESS`:
			return state
		default:
			return state
	}
}

// Selector
export const sokSelector = (items, searchStr) => {
	if (!items) return null
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item => {
		return item.some(v => v.toLowerCase().includes(query))
	})
}

// thunks
