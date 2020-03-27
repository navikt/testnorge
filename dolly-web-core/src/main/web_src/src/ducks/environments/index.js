import { TpsfApi } from '~/service/Api'
import { createActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'

export const { getEnvironments } = createActions(
	{
		getEnvironments: TpsfApi.getTilgjengligeMiljoer
	},
	{ prefix: 'env' }
)

const initialState = {
	data: null
}

export default handleActions(
	{
		[onSuccess(getEnvironments)](state, action) {
			state.data = _getEnvironmentsSortedByType(action.payload.data.environments)
		}
	},
	initialState
)

export const _getEnvironmentsSortedByType = envArray => {
	let sortedByType = envArray.reduce((prev, curr) => {
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
		const sorterteNummer = envs.map(env => env.id.match(/.{1,1}/g))
		const sorterteEnvs = sorterteNummer.map(v => v[0] + v[1]).sort()

		sorterteEnvs.map((current, idx) => {
			return (envs[idx].id = sorterteEnvs[idx])
		})

		sorterteEnvs.map((current, idx) => (envs[idx].id = sorterteEnvs[idx]))
	})
	return sortedByType
}
