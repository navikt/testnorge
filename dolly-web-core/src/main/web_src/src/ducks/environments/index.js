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
		sortedByType[key] = envs.sort((a, b) => {
			return a.label.substring(1) - b.label.substring(1)
		})
	})
	return sortedByType
}
