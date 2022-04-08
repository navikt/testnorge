import common, { redirectReducer } from '../index'
import { LOCATION_CHANGE } from 'redux-first-history'
import { onSuccess } from '~/ducks/utils/requestActions'
import { createGruppe, deleteGruppe } from '~/ducks/gruppe'
import { actions as bestillingActions } from '~/ducks/bestilling'

describe('commonReducer', () => {
	it('should return initial state', () => {
		const initialState = {
			redirectTo: null,
		}

		expect(common(undefined, {})).toEqual(initialState)
	})
})

describe('commonReducer - redirectReducer', () => {
	it('should return initial state', () => {
		const initialState = null
		expect(redirectReducer(undefined, {})).toEqual(initialState)
	})

	const testId = 1
	it('should handle success create group', () => {
		const action = {
			type: onSuccess(createGruppe),
			payload: { data: { id: testId } },
		}

		const res = `/gruppe/${testId}`
		expect(redirectReducer({}, action)).toEqual(res)
	})

	it('should handle abort bestilling', () => {
		const action = {
			type: bestillingActions.abortBestilling.toString(),
			payload: testId,
		}

		const res = `/gruppe/${testId}`

		expect(redirectReducer({}, action)).toEqual(res)
	})

	// it('should handle bestilling success', () => {
	// 	const action = {
	// 		type: onSuccess(bestillingActions.postBestilling),
	// 		meta: { gruppeId: testId }
	// 	}

	// 	const res = `/gruppe/${testId}`

	// 	expect(redirectReducer({}, action)).toEqual(res)
	// })

	it('should handle delete group success', () => {
		const action = {
			type: onSuccess(deleteGruppe),
		}
		const res = '/'
		expect(redirectReducer({}, action)).toEqual(res)
	})

	it('should handle LOCATION_CHANGE', () => {
		const action = {
			type: LOCATION_CHANGE,
		}

		expect(redirectReducer({}, action)).toEqual(null)
	})
})
