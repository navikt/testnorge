import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import Formatters from '~/utils/DataFormatter'
import success from '~/utils/SuccessAction'
import { actions as bestillingActions } from '~/ducks/bestilling'

export const getBestillinger = createAction('GET_BESTILLINGER', async gruppeID => {
	let res = await DollyApi.getBestillinger(gruppeID)
	return res
})

export const removeNyBestillingStatus = createAction('REMOVE_NY_BESTILLING_STATUS')

// ny-array holder oversikt over nye bestillinger i en session
const initialState = { ny: [1862, 1861, 1860] }

export const cancelBestilling = createAction('CANCEL_BESTILLING', async id => {
	let res = await DollyApi.cancelBestilling(id)
	return res
})

export default handleActions(
	{
		[success(getBestillinger)](state, action) {
			const { data } = action.payload
			return { ...state, data }
		},

		[success(bestillingActions.postBestilling)](state, action) {
			return { ...state, ny: [...state.ny, action.payload.data.id] }
		},

		// [success(cancelBestilling)](state, action) {
		// 	return { ...state, ny: state.ny.filter(id => id !== action.payload.id) }
		// }

		[removeNyBestillingStatus](state, action) {
			return { ...state, ny: state.ny.filter(id => id !== action.payload) }
		}
	},
	initialState
)

// Selector + mapper
export const sokSelector = (items, searchStr) => {
	if (!items) return null
	const mappedItems = mapItems(items)

	if (!searchStr) return mappedItems

	const query = searchStr.toLowerCase()
	return mappedItems.filter(item => {
		const searchValues = [
			_get(item, 'id'),
			_get(item, 'antallIdenter'),
			_get(item, 'sistOppdatert'),
			_get(item, 'environments'),
			_get(item, 'ferdig')
		]
			.filter(v => !_isNil(v))
			.map(v => v.toString().toLowerCase())

		return searchValues.some(v => v.includes(query))
	})
}

// Selector
export const miljoStatusSelector = bestillingStatus => {
	if (!bestillingStatus) return null

	const id = bestillingStatus.id
	let envs = bestillingStatus.environments.slice(0) // Clone array for å unngå mutering
	let successEnvs = []
	let failedEnvs = []
	let errorMsgs = []

	if (bestillingStatus.bestillingProgress && bestillingStatus.bestillingProgress.length != 0) {
		envs.forEach(env => {
			bestillingStatus.bestillingProgress.forEach(person => {
				if (!person.tpsfSuccessEnv) {
					// TODO: Bestilling failed 100% fra Tpsf. Implement retry-funksjonalitet når maler er støttet
					failedEnvs = envs
				} else if (!person.tpsfSuccessEnv.includes(env)) {
					!failedEnvs.includes(env) && failedEnvs.push(env)
				}
			})
		})

		envs.forEach(env => {
			!failedEnvs.includes(env) && successEnvs.push(env)
		})

		// Registre miljø status
		// Plasseres i egen for-each for visuel plassering og mer lesbar kode
		bestillingStatus.bestillingProgress.forEach(person => {
			if (person.krrstubStatus) {
				person.krrstubStatus == 'OK'
					? !successEnvs.includes('Krr-stub') && successEnvs.push('Krr-stub')
					: !failedEnvs.includes('Krr-stub') && failedEnvs.push('Krr-stub')
			}

			if (person.sigrunstubStatus) {
				person.sigrunstubStatus == 'OK'
					? !successEnvs.includes('Sigrun-stub') && successEnvs.push('Sigrun-stub')
					: !failedEnvs.includes('Sigrun-stub') && failedEnvs.push('Sigrun-stub')
			}

			// Feilmelding fra tps
			person.feil && errorMsgs.push('Ident ' + person.ident + ': ' + person.feil)
		})
	}

	return { id, successEnvs, failedEnvs, errorMsgs }
}

const mapItems = items => {
	if (!items) return null
	return items.map(item => {
		return {
			...item,
			id: item.id.toString(),
			antallIdenter: item.antallIdenter.toString(),
			sistOppdatert: Formatters.formatDate(item.sistOppdatert),
			environments: Formatters.arrayToString(item.environments),
			ferdig: item.stoppet ? 'Stoppet' : item.ferdig ? 'Ferdig' : 'Pågår'
		}
	})
}
