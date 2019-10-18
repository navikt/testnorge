import bestilling from '../index'
import { LOCATION_CHANGE } from 'connected-react-router'
import { combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

describe('bestillingReducer', () => {
	const initialState = {
		antall: 1,
		page: 0,
		attributeIds: [],
		currentMal: '',
		environments: [],
		identtype: '',
		malBestillingNavn: '',
		maler: [],
		values: {},
		identOpprettesFra: 'nyIdent',
		eksisterendeIdentListe: [],
		ugyldigIdentListe: []
	}

	it('should set initial state', () => {
		expect(bestilling(undefined, {})).toEqual(initialState)
	})

	it('should set next page', () => {
		const action = {
			type: 'NEXT_PAGE'
		}

		const res = { ...initialState, page: 1 }
		expect(bestilling(initialState, action)).toEqual(res)
	})

	it('should set prev page', () => {
		const action = {
			type: 'PREV_PAGE'
		}

		const res = { ...initialState, page: -1 }
		expect(bestilling(initialState, action)).toEqual(res)
	})

	it('should toggle attribute', () => {
		const action = {
			type: 'TOGGLE_ATTRIBUTE',
			payload: 'statborgerskap'
		}

		const res = { ...initialState, attributeIds: ['statborgerskap'] }
		expect(bestilling(initialState, action)).toEqual(res)
	})

	it('should uncheck all attributes', () => {
		const action = {
			type: 'UNCHECK_ALL_ATTRIBUTES'
		}
		const state = { ...initialState, attributeIds: ['statborgerskap'] }

		expect(bestilling(state, action)).toEqual(initialState)
	})

	it('should start bestilling', () => {
		const action = {
			type: 'START_BESTILLING',
			payload: {
				antall: 1,
				identtype: 'FNR'
			}
		}

		const res = { ...initialState, identtype: 'FNR', antall: 1, page: 1 }
		expect(bestilling(initialState, action)).toEqual(res)
	})

	it('should set environments', () => {
		const action = {
			type: 'SET_ENVIRONMENTS',
			payload: { values: ['t0'], goBack: true }
		}
		const res = { ...initialState, environments: ['t0'], page: -1 }

		expect(bestilling(initialState, action)).toEqual(res)
	})

	it('should set values', () => {
		const action = {
			type: 'SET_VALUES',
			payload: { values: { statsborgerskap: 'ALB' }, goBack: true }
		}
		const res = { ...initialState, values: { statsborgerskap: 'ALB' }, page: -1 }

		expect(bestilling(initialState, action)).toEqual(res)
	})
})
