import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import Formatters from '~/utils/DataFormatter'
import success from '~/utils/SuccessAction'

export const getBestillingStatus = createAction(
	'GET_BESTILLING_STATUS',
	DollyApi.getBestillingStatus
)
const SET_BESTILLING_STATUS = 'SET_BESTILLING_STATUS'

const initialState = {}

export const cancelBestilling = createAction('CANCEL_BESTILLING', DollyApi.cancelBestilling)

export default handleActions(
	{
		[success(getBestillingStatus)](state, action) {
			return { ...state, [action.payload.data.id]: action.payload.data }
		},

		[SET_BESTILLING_STATUS](state, action) {
			return { ...state, [action.bestillingId]: action.data }
		}
	},
	initialState
)

// SET BESTILLING STATUS
export const setBestillingStatus = (bestillingId, data) => ({
	type: SET_BESTILLING_STATUS,
	bestillingId,
	data
})

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

	if (bestillingStatus.personStatus.length != 0) {
		envs.forEach(env => {
			bestillingStatus.personStatus.forEach(person => {
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
		bestillingStatus.personStatus.forEach(person => {
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
		})
	}

	return { id, successEnvs, failedEnvs }
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
