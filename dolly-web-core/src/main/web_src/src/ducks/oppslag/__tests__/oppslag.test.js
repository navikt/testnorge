import oppslagReducer, { oppslagLabelSelector } from '../index'
import { onSuccess } from '~/ducks/utils/requestActions'

describe('oppslagReducer', () => {
	it('should return state with initial state', () => {
		const initialState = {}
		expect(oppslagReducer(undefined, {})).toEqual(initialState)
	})

	// it('should handle success action', () => {
	// 	const testdata = 'test'
	// 	const action = {
	// 		type: onSuccess('GET_KODEVERK'),
	// 		payload: { data: testdata },
	// 		meta: { kodeverkNavn: testdata }
	// 	}

	// 	const res = {
	// 		[testdata]: testdata
	// 	}

	// 	expect(oppslagReducer({}, action)).toEqual(res)
	// })
})

// describe('oppslagLabelSelector', () => {
// 	const testnavn = 'test'
// 	const testkodeverk = {
// 		value: testnavn,
// 		label: 'label'
// 	}

// 	const state = {
// 		kodeverk: { [testnavn]: { koder: [testkodeverk] } }
// 	}

// 	expect(oppslagLabelSelector(state, testnavn, testnavn)).toEqual(testkodeverk)
// })
