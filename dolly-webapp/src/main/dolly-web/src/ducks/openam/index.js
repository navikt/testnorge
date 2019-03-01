import { DollyApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import { LOCATION_CHANGE } from 'connected-react-router'
import success from '~/utils/SuccessAction'
import _groupBy from 'lodash/groupBy'

export const postOpenAm = createAction('POST_OPEN_AM', async (groupObj, bestillinger) => {
	const { testidenter } = groupObj
	const testidenterSortedByBestillingId = _groupBy(testidenter, 'bestillingId')
	const bestillingIdListe = Object.keys(testidenterSortedByBestillingId)

	const promiseArray = bestillingIdListe.map(x => {
		const currentBestilling = bestillinger.find(y => parseInt(y.id) === parseInt(x))
		const currentTestidenter = testidenterSortedByBestillingId[x]

		return DollyApi.postOpenAm({
			identer: currentTestidenter.map(ident => ident.ident),
			miljoer: currentBestilling.environments
		})
	})

	const resArr = await Promise.all(promiseArray)
	const res = await DollyApi.putOpenAmGroupStatus(groupObj.id)
	return resArr.map((res, idx) => {
		const bestillingId = bestillingIdListe[idx]
		return {
			bestillingId,
			testidenter: testidenterSortedByBestillingId[bestillingId].map(ident => ident.ident),
			status: res.data
		}
	})
})

export const postOpenAm2 = createAction('POST_OPEN_AM', async bestillingId => {
	// console.log('postOpenAm2 skjer 1 :', bestillingId)
	const res = DollyApi.postOpenAmBestilling(bestillingId)
	// console.log('postOpenAm2 skjer 2 :', res)
	return res
	// return {
	// 	res,
	// 	bestillingId,
	// 	testidenter: testidenterSortedByBestillingId[bestillingId].map(ident => ident.ident),
	// 	status: res.data
	// }
})

const initialState = {
	response: null
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		// [`${postOpenAm}_REQUEST`](state, action) {
		// 	return initialState
		// },
		[`${postOpenAm2}_REQUEST`](state, action) {
			return initialState
		},
		[success(postOpenAm2)](state, action) {
			return { ...state, response: action.payload }
		}
	},
	initialState
)

//thunk
export const sendToOpenAm = bestillingId => (dispatch, getState) => {
	const { gruppe, bestillingStatuser } = getState()

	// console.log('sendToOpenAm skjer :', bestillingId)

	return dispatch(postOpenAm2(bestillingId))
}
