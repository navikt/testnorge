import kodeverkReducer, { kodeverkLabelSelector } from '../index'
import success from '~/utils/SuccessAction'

describe('kodeverkReducer', () => {
	it('should return state with initial state', () => {
		const initialState = {}
		expect(kodeverkReducer(undefined, {})).toEqual(initialState)
	})

	it('should handle success action', () => {
		const testdata = 'test'
		const action = {
			type: success('GET_KODEVERK'),
			payload: { data: testdata },
			meta: { kodeverkNavn: testdata }
		}

		const res = {
			[testdata]: testdata
		}

		expect(kodeverkReducer({}, action)).toEqual(res)
	})
})

describe('kodeverkLabelSelector', () => {
	const testnavn = 'test'
	const testkodeverk = {
		value: testnavn,
		label: 'label'
	}

	const state = {
		kodeverk: { [testnavn]: { koder: [testkodeverk] } }
	}

	expect(kodeverkLabelSelector(state, testnavn, testnavn)).toEqual(testkodeverk)
})
