import bestillingStatus from '../index'

describe('bestillingStatusReducer', () => {
	it('should return initial state', () => {
		expect(bestillingStatus(undefined, {})).toEqual({ ny: [], data: [] })
	})
})
