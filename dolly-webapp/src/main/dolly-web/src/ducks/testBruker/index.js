import { TpsfApi, SigrunApi, KrrApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction } from 'redux-actions'
import success from '~/utils/SuccessAction'
import DataFormatter from '~/utils/DataFormatter'
import _get from 'lodash/get'
import _set from 'lodash/set'
import _merge from 'lodash/merge'
import { array } from 'yup'

const initialState = {
	items: {
		tpsf: null,
		sigrunstub: null,
		krrstub: null
	}
}

const actionTypes = {
	UPDATE_TESTBRUKER_REQUEST: 'UPDATE_TESTBRUKER_REQUEST',
	UPDATE_TESTBRUKER_SUCCESS: 'UPDATE_TESTBRUKER_SUCCESS',
	UPDATE_TESTBRUKER_ERROR: 'UPDATE_TESTBRUKER_ERROR'
}

const updateTestbrukerRequest = () => ({ type: actionTypes.UPDATE_TESTBRUKER_REQUEST })
const updateTestbrukerSuccess = () => ({ type: actionTypes.UPDATE_TESTBRUKER_SUCCESS })
const updateTestbrukerError = () => ({ type: actionTypes.UPDATE_TESTBRUKER_ERROR })

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
			return res
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
		case success(GET_TPSF_TESTBRUKERE):
			return { ...state, items: { ...state.items, tpsf: action.payload.data } }
		case success(GET_SIGRUN_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					sigrunstub: { ...state.items.sigrunstub, [action.meta.ident]: action.payload.data }
				}
			}
		case success(GET_KRR_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					krrstub: { ...state.items.krrstub, [action.meta.ident]: action.payload.data }
				}
			}
		case actionTypes.UPDATE_TESTBRUKER_SUCCESS:
			return state
		default:
			return state
	}
}

// Thunk
export const updateTestbruker = (newValues, attributtListe, ident) => async (
	dispatch,
	getState
) => {
	try {
		dispatch(updateTestbrukerRequest())
		const state = getState()
		const { testbruker } = state

		//TPSF
		const tpsfAttributtListe = attributtListe.filter(item => item.dataSource === 'TPSF')
		const tpsfMappedValues = tpsfAttributtListe.reduce((prev, curr) => {
			let currentValue = newValues[curr.id]
			if (curr.inputType === 'date') currentValue = DataFormatter.parseDate(currentValue)
			return _set(prev, curr.editPath || curr.path || curr.id, currentValue)
		}, {})

		const tpsData = {
			identer: [ident],
			miljoer: findEnvironmentsForIdent(state, ident)
		}
		// const tpsfRes = await TpsfApi.updateTestbruker(
		// 	_merge(testbruker.items.tpsf[0], tpsfMappedValues)
		// )
		// if (tpsfRes.status === 200) {
		// 	const tpsRes = await TpsfApi.sendToTps(tpsData)
		// }

		// SIGRUN
		const sigrunstubAtributtListe = attributtListe.filter(item => item.dataSource === 'SIGRUN')
		const sigrunstubMappedValues = sigrunstubAtributtListe.map(attribute => {
			const currentValues = newValues[attribute.id]
			const items = attribute.items
			if (items) {
				const promises = currentValues.map(valueObject => {
					const headerObject = items.reduce(
						(prev, curr) => {
							return _set(prev, curr.editPath || curr.path || curr.id, valueObject[curr.id])
						},
						{ personidentifikator: ident }
					)
					return SigrunApi.updateTestbruker(headerObject)
				})
				return Promise.all(promises)
			}
			return null
		})

		const sigrunstubRes = await sigrunstubMappedValues

		dispatch(updateTestbrukerSuccess())
	} catch (error) {
		console.log(error)
		dispatch(updateTestbrukerError())
	}
}

// Selectors
export const sokSelector = (items, searchStr) => {
	if (!items) return null
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item => {
		return item.some(v => v.toLowerCase().includes(query))
	})
}

export const findEnvironmentsForIdent = (state, ident) => {
	const { gruppe } = state
	if (!gruppe.data) return null

	const identArray = gruppe.data[0].testidenter
	const personObj = identArray.find(item => item.ident === ident)
	if (!personObj) return null

	const bestillingObj = gruppe.data[0].bestillinger.find(
		bestilling => bestilling.id === personObj.bestillingId
	)
	return bestillingObj.environments
}
