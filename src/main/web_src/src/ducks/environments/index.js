import { createActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { MiljoeApi } from '~/service/Api'

export const { getEnvironments } = createActions(
	{
		getEnvironments: MiljoeApi.getAktiveMiljoer
	},
	{ prefix: 'env' }
)

const initialState = {
	data: null
}

export default handleActions(
	{
		[onSuccess(getEnvironments)](state, action) {
			state.data = _getEnvironmentsSortedByType(action.payload.data)
		}
	},
	initialState
)

export const _getEnvironmentsSortedByType = envArray => {
	const sortedByType = envArray.reduce((prev, curr) => {
		const label = curr.toUpperCase()
		const envType = label.charAt(0)
		if (prev[envType]) {
			prev[envType].push({ id: curr, label })
		} else {
			prev[envType] = [{ id: curr, label }]
		}
		return prev
	}, {})

	Object.keys(sortedByType).forEach(key => {
		const envs = sortedByType[key]
		const sorterteNummer = envs.map(env => env.id.match(/.{1,3}/g))
		const sorterteEnvs = sorterteNummer
			.map(v => v[0])
			.sort(function(a, b) {
				const prev = parseInt(a.substring(1))
				const current = parseInt(b.substring(1))
				if (prev > current) return 1
				if (prev < current || isNaN(current)) return -1
				return 0
			})

		sorterteEnvs.map((current, idx) => (envs[idx].id = sorterteEnvs[idx]))
		sorterteEnvs.map((current, idx) => (envs[idx].label = sorterteEnvs[idx].toUpperCase()))
	})
	return sortedByType
}
