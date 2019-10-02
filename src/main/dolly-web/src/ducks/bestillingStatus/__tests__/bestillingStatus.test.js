import bestillingStatus from '../index'
//import { sokSelector, miljoStatusSelector } from '../index'
import { sokSelector } from '~/ducks/bestillingStatus/utils'

describe('bestillingStatusReducer', () => {
	it('should return initial state', () => {
		expect(bestillingStatus(undefined, {})).toEqual({ ny: [], data: [] })
	})

	// it('should handle a success action', () => {
	// 	const testdata = { id: 1, data: 'test' }
	// 	const action = {
	// 		type: 'GET_BESTILLING_STATUS_SUCCESS',
	// 		payload: { data: testdata }
	// 	}

	// 	const res = {
	// 		[testdata.id]: testdata
	// 	}

	// 	expect(bestillingStatus({}, action)).toEqual(res)
	// })
})

//FLYTTET FRA DUCK TIL EGEN FIL
// describe('bestillingStatusReducer-sokSelector', () => {
// 	const testdate = new Date('2000-01-01')
// 	const testarr = ['1', '2']

// 	const testitems = [
// 		{
// 			id: 1,
// 			antallIdenter: 1,
// 			sistOppdatert: testdate,
// 			environments: testarr,
// 			ferdig: true
// 		},
// 		{
// 			id: 2,
// 			antallIdenter: 2,
// 			sistOppdatert: testdate,
// 			environments: testarr,
// 			ferdig: false
// 		}
// 	]

// 	const res = [
// 		{
// 			id: '1',
// 			antallIdenter: '1',
// 			sistOppdatert: '01.01.2000',
// 			environments: '1, 2',
// 			ferdig: 'Ferdig'
// 		}
// 	]

// 	expect(sokSelector(testitems, 'ferdig')).toEqual(res)
// 	expect(sokSelector(null)).toEqual(null)
// })

//FJERNET FRA DUCK

// describe('bestillingStatusReducer-miljoStatusSelector', () => {
// 	const testBestillingStatus = {
// 		id: 1,
// 		environments: ['t0', 't1'],
// 		personStatus: [{ id: 0, tpsfSuccessEnv: ['t0'] }, { id: 1, tpsfSuccessEnv: ['t0'] }]
// 	}

// 	const res = {
// 		id: 1,
// 		successEnvs: ['t0'],
// 		failedEnvs: ['t1']
// 	}

// 	const testBestillingStatus2 = {
// 		id: 2,
// 		environments: ['t0', 't1', 't2'],
// 		personStatus: [{ id: 0, tpsfSuccessEnv: ['t0'] }, { id: 1, tpsfSuccessEnv: ['t0', 't2'] }]
// 	}

// 	const res2 = {
// 		id: 2,
// 		successEnvs: ['t0'],
// 		failedEnvs: ['t1', 't2']
// 	}

// 	const testBestillingStatus3 = {
// 		id: 3,
// 		environments: ['t0', 't1', 't2', 'q0', 'u6'],
// 		personStatus: [
// 			{ id: 0, tpsfSuccessEnv: ['t0', 't2', 'q0', 'u6'] },
// 			{ id: 1, tpsfSuccessEnv: ['t0', 't2', 'q0'] },
// 			{ id: 3, tpsfSuccessEnv: ['t2', 'q0', 'u6'] }
// 		]
// 	}

// 	const res3 = {
// 		id: 3,
// 		successEnvs: ['t2', 'q0'],
// 		failedEnvs: ['t0', 't1', 'u6']
// 	}

// 	expect(miljoStatusSelector(testBestillingStatus)).toEqual(res)
// 	expect(miljoStatusSelector(testBestillingStatus2)).toEqual(res2)
// 	expect(miljoStatusSelector(testBestillingStatus3)).toEqual(res3)
// 	expect(miljoStatusSelector(null)).toEqual(null)
// })
