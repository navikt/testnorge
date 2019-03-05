import { DollyApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import { LOCATION_CHANGE } from 'connected-react-router'
import success from '~/utils/SuccessAction'
import _groupBy from 'lodash/groupBy'

// export const postOpenAm = createAction('POST_OPEN_AM', async (groupObj, bestillinger) => {
// 	const { testidenter } = groupObj
// 	const testidenterSortedByBestillingId = _groupBy(testidenter, 'bestillingId')
// 	const bestillingIdListe = Object.keys(testidenterSortedByBestillingId)

// 	const promiseArray = bestillingIdListe.map(x => {
// 		const currentBestilling = bestillinger.find(y => parseInt(y.id) === parseInt(x))
// 		const currentTestidenter = testidenterSortedByBestillingId[x]

// 		return DollyApi.postOpenAm({
// 			identer: currentTestidenter.map(ident => ident.ident),
// 			miljoer: currentBestilling.environments
// 		})
// 	})

// 	const resArr = await Promise.all(promiseArray)
// 	const res = await DollyApi.putOpenAmGroupStatus(groupObj.id)
// 	return resArr.map((res, idx) => {
// 		const bestillingId = bestillingIdListe[idx]
// 		return {
// 			bestillingId,
// 			testidenter: testidenterSortedByBestillingId[bestillingId].map(ident => ident.ident),
// 			status: res.data
// 		}
// 	})
// })

export const postOpenAm = createAction('POST_OPEN_AM', async bestillingId => {
	// console.log('postOpenAm2 skjer 1 :', bestillingId)
	const res = await DollyApi.postOpenAmBestilling(bestillingId)
	// console.log('postOpenAm2 skjer 2 :', res)

	const data = res.data
	const payload = { bestillingId, data }

	return payload
	// return {bestillingsId, res.data}

	// return {
	// 	res,
	// 	bestillingId,
	// 	testidenter: testidenterSortedByBestillingId[bestillingId].map(ident => ident.ident),
	// 	status: res.data
	// }
})

export const removeNyOpenAmStatus = createAction('REMOVE_NY_OPENAM_STATUS')

const initialState = {
	responses: []
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[success(postOpenAm)](state, action) {
			const id = action.payload.bestillingId
			const data = action.payload.data
			return { ...state, responses: [...state.responses, { id, data }] }
		},
		[removeNyOpenAmStatus](state, action) {
			console.log('lukk')
			return { ...state, hei: '2' }
		}
	},
	initialState
)

//thunk
export const sendToOpenAm = bestillingId => (dispatch, getState) => {
	const { gruppe, bestillingStatuser } = getState()

	// console.log('sendToOpenAm skjer :', bestillingId)

	return dispatch(postOpenAm(bestillingId))
}
