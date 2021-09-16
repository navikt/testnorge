import search from '../index'

describe('searchReducer', () => {
	it('should return initial state', () => {
		expect(search(undefined, {})).toEqual('')
	})

	it('it should reset search to initial state', () => {
		const action = {
			type: 'SEARCH/RESET_SEARCH',
		}
		expect(search('prevVal', action)).toEqual('')
	})

	it('it should set search to text', () => {
		const testText = 'test'
		const action = {
			type: 'SEARCH/SET_TEXT',
			payload: testText,
		}

		expect(search('', action)).toEqual(testText)
	})
})
