import environments from '../index'
import { _getEnvironmentsSortedByType } from '../'

describe('environmentsReducer', () => {
	it('should return initial state', () => {
		const initialState = {
			data: null
		}

		expect(environments(undefined, {})).toEqual(initialState)
	})

	it('should handle success', () => {
		const testdata = [1, 2, 3, 4]
		const action = {
			type: 'GET_ENVIRONMENTS_SUCCESS',
			payload: testdata
		}

		const res = {
			data: testdata
		}

		expect(environments({}, action)).toEqual(res)
	})
})

describe('environmentsReduder - _getEnvironmentsSortedByType', () => {
	it('should handle response and sort it by type', () => {
		const testdata = ['t1', 't2', 'q2', 'q1', 'u6']

		const res = {
			T: [
				{
					id: 't1',
					label: 'T1'
				},
				{
					id: 't2',
					label: 'T2'
				}
			],
			Q: [
				{
					id: 'q1',
					label: 'Q1'
				},
				{
					id: 'q2',
					label: 'Q2'
				}
			],
			U: [
				{
					id: 'u6',
					label: 'U6'
				}
			]
		}

		expect(_getEnvironmentsSortedByType(testdata)).toEqual(res)
	})
})
