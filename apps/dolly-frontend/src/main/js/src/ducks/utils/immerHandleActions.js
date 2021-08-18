import produce from 'immer'
import { handleActions as raHandleActions } from 'redux-actions'

export const handleActions = (actions, state) =>
	raHandleActions(
		Object.keys(actions).reduce((acc, key) => {
			acc[key] = produce(actions[key])
			return acc
		}, {}),
		state
	)
