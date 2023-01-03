import errors, { applicationErrorSelector, createErrorMessageSelector } from '../index'

describe('errorsReducer', () => {
	it('should set a request to true', () => {
		const errortext = 'testerror'
		const action = {
			type: 'DUMMY_ACTION_FAILURE',
			payload: { message: errortext },
		}

		const res = {
			DUMMY_ACTION: errortext,
		}

		expect(errors({}, action)).toEqual(res)
	})
})

describe('loadingReducer-selector', () => {
	const actions = ['DUMMY_1', 'DUMMY_2']

	const errortext = 'errormsg'

	const state = {
		errors: {
			DUMMY_1: errortext,
			DUMMY_2: errortext,
		},
	}

	const errorActions = createErrorMessageSelector(actions)

	expect(errorActions(state)).toEqual(errortext)
})

describe('applicationErrorSelector', () => {
	const errortext = 'errormsg'
	const state = {
		errors: {
			DUMMY_1: errortext,
			DUMMY_2: errortext,
		},
	}
	it('should return something', () => {
		expect(applicationErrorSelector(state)).toEqual(errortext)
	})
})
