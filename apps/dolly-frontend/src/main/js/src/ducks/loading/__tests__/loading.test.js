import loading, { createLoadingSelector } from '../index'

describe('loadingReducer', () => {
	it('should set a request to true', () => {
		const action = {
			type: 'DUMMY_ACTION_REQUEST',
		}

		const res = {
			DUMMY_ACTION: true,
		}

		expect(loading({}, action)).toEqual(res)
	})
})

describe('loadingReducer-selector', () => {
	const actions = ['DUMMY_1', 'DUMMY_2']
	const singleAction = 'DUMMY_3'

	const state = {
		loading: {
			DUMMY_1: true,
			DUMMY_2: true,
			DUMMY_3: true,
		},
	}

	const loadingActions = createLoadingSelector(actions)
	const loadingSingleAction = createLoadingSelector(singleAction)

	expect(loadingActions(state)).toBeTruthy()
	expect(loadingSingleAction(state)).toBeTruthy()
})
