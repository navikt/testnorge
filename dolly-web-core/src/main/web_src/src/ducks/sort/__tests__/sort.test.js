import sort from '../index'

describe('sortReducer', () => {
	const initialState = {
		id: 'id',
		order: 'desc'
	}
	it('should return initial state', () => {
		expect(sort(undefined, {})).toEqual(initialState)
	})

	it('should handle a success action', () => {
		const test = 'test'
		const action = {
			type: 'SORT/SET',
			payload: test
		}

		expect(sort({}, action)).toEqual(test)
	})

	it('should return initial state on LOCATION CHANGE', () => {
		const action = {
			type: '@@router/LOCATION_CHANGE'
		}
		expect(sort({}, action)).toEqual(initialState)
	})
})
