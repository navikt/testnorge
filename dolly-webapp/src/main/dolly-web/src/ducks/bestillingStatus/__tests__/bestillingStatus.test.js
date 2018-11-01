import bestillingStatus from '../index'
import { sokSelector } from '../index'

describe('bestillingStatusReducer', () => {
	it('should return initial state', () => {
		expect(bestillingStatus(undefined, {})).toEqual({})
	})

	it('should handle a success action', () => {
		const testdata = { id: 1, data: 'test' }
		const action = {
			type: 'GET_BESTILLING_STATUS_SUCCESS',
			payload: { data: testdata }
		}

		const res = {
			[testdata.id]: testdata
		}

		expect(bestillingStatus({}, action)).toEqual(res)
	})
})

describe('bestillingStatusReducer-sokSelector', () => {
	const testdate = new Date('2000-01-01')
	const testarr = ['1', '2']

	const testitems = [
		{
			id: 1,
			antallIdenter: 1,
			sistOppdatert: testdate,
			environments: testarr,
			ferdig: true
		},
		{
			id: 2,
			antallIdenter: 2,
			sistOppdatert: testdate,
			environments: testarr,
			ferdig: false
		}
	]

	const res = [
		{
			id: '1',
			antallIdenter: '1',
			sistOppdatert: '01.01.2000',
			environments: '1, 2',
			ferdig: 'Ferdig'
		}
	]

	expect(sokSelector(testitems, 'ferdig')).toEqual(res)
	expect(sokSelector(null)).toEqual(null)
})
