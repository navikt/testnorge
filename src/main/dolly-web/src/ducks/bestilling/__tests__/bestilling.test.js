import bestilling from '../index'
import { LOCATION_CHANGE } from 'connected-react-router'

describe('bestillingReducer', () => {
	const initialState = {
		antall: 1,
		page: 0,
		attributeIds: [],
		environments: [],
		identtype: '',
		values: {}
	}

	it('should set initial state', () => {
		expect(bestilling(undefined, {})).toEqual(initialState)
	})

	// it('should set initial state on request', () => {
	// 	const action = {
	// 		type: 'POST_OPEN_AM_REQUEST'
	// 	}
	// 	expect(openam({}, action)).toEqual(initialState)
	// })

	// it('should handle attribute toggling', () => {
	// 	const testdata = 'testdata'
	// 	const action = {
	// 		type: 'POST_OPEN_AM_SUCCESS',
	// 		payload: testdata
	// 	}

	// 	const res = {
	// 		response: testdata
	// 	}
	// 	expect(openam({}, action)).toEqual(res)
	// })
})
