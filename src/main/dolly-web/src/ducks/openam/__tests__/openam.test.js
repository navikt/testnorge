import openam, { postOpenAm } from '../index'
import { LOCATION_CHANGE } from 'connected-react-router'

describe('openamReducer', () => {
	const initialState = {
		responses: []
	}

	it('should set initial state', () => {
		expect(openam(undefined, {})).toEqual(initialState)
	})

	// it('should set initial state on request', () => {
	// 	const action = {
	// 		type: 'POST_OPEN_AM_REQUEST'
	// 	}
	// 	expect(openam({}, action)).toEqual(initialState)
	// })

	// it('should handle success', () => {
	// 	const testdata = ['testdata']
	// 	const action = {
	// 		type: 'POST_OPEN_AM_SUCCESS',
	// 		payload: testdata
	// 	}

	// 	const res = {
	// 		responses: testdata
	// 	}
	// 	expect(openam({}, action)).toEqual(res)
	// })
})
