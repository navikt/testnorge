import brukerReducer from '../index'

describe('brukerReducer', () => {
	it('should return initialState', () => {
		const initState = {
			brukerData: null,
		}
		expect(brukerReducer(undefined, {})).toEqual(initState)
	})

	it('should return correct state after GET_CURRENT_BRUKER_SUCCESS', () => {
		const testdata = 'test'
		const action = {
			type: 'GET_CURRENT_BRUKER_SUCCESS',
			payload: { data: testdata },
		}

		const res = {
			brukerData: testdata,
		}

		expect(brukerReducer(undefined, action)).toEqual(res)
	})
})
