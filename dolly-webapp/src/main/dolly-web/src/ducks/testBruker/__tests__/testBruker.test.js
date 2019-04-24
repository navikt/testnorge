import testBruker from '../'
import { sokSelector } from '../'

describe('testBrukerReducer', () => {
	const initialState = {
		items: {
			tpsf: null,
			sigrunstub: null,
			krrstub: null,
			aareg: null,
		}
	}
	it('should return initial state', () => {
		expect(testBruker(undefined, {})).toEqual(initialState)
	})

	it('should return initial state on LOCATION_CHANGE', () => {
		const action = {
			type: '@@router/LOCATION_CHANGE'
		}

		expect(testBruker({}, action)).toEqual(initialState)
	})

	it('should add tpsf items on success', () => {
		const testdata = 'test'
		const action = {
			type: 'GET_TPSF_TESTBRUKERE_SUCCESS',
			payload: { data: testdata }
		}

		const res = {
			items: {
				tpsf: testdata
			}
		}

		expect(testBruker({}, action)).toEqual(res)
	})

	it('should add sigrunStub items on success', () => {
		const testdata = 'test'
		const testident = 'a'
		const action = {
			type: 'GET_SIGRUN_TESTBRUKER_SUCCESS',
			payload: { data: testdata },
			meta: { ident: testident }
		}

		const res = {
			items: { tpsf: null, sigrunstub: { [testident]: testdata }, krrstub: null, aareg: null }
		}

		expect(testBruker(initialState, action)).toEqual(res)
	})

	it('should add krr items on success', () => {
		const testident = 'a'
		const testdata = [testident]

		const action = {
			type: 'GET_KRR_TESTBRUKER_SUCCESS',
			payload: { data: testdata },
			meta: { ident: testident }
		}

		const res = {
			items: {
				tpsf: null,
				sigrunstub: null,
				krrstub: {
					[testident]: testident
				},
				aareg: null
			}
		}

		expect(testBruker(initialState, action)).toEqual(res)
	})

	it('should update testbruker', () => {
		const action = {
			type: 'UPDATE_TESTBRUKER_SUCCESS'
		}
		console.log('update testbruker not correctly implemented')
		expect(testBruker({}, {})).toEqual({})
	})
})

describe('testBruker-sokSelector', () => {
	const testItems = [['1,2'], ['3,4']]
	it('should return subset of items based on search', () => {
		const testsearch = '1'

		expect(sokSelector(testItems, testsearch)).toEqual([['1,2']])
	})

	it('should return null if no items', () => {
		expect(sokSelector(null, null)).toEqual(null)
	})

	it('should return all items on empty search', () => {
		expect(sokSelector(testItems, '')).toEqual(testItems)
	})
})
