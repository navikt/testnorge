import { TpsfApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import { onSuccess } from '~/ducks/utils/requestActions'

export const getEnvironments = createAction('GET_ENVIRONMENTS', () =>
	TpsfApi.getTilgjengligeMiljoer()
)

const initialState = {
	data: null
}

export default handleActions(
	{
		[onSuccess(getEnvironments)](state, action) {
			const envs = _getEnvironmentsSortedByType(action.payload.data.environments)
			return { ...state, data: envs }
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
