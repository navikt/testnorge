import * as _ from 'lodash-es'
import { useDispatch } from 'react-redux'

export const stateModifierFns = (initial, setInitial, options = null, dispatch = null) => {
	if (dispatch == null) {
		dispatch = useDispatch()
	}
	const opts = options
	const set = (path, value) => setInitial(_.set(path, value, initial))
	const has = (path) => _.has(initial, path)
	const del = (path) => {
		let newObj = _.omit(initial, path)

		// Ingen tomme objekter guard
		let rootPath = Array.isArray(path) ? path[0].split('.')[0] : path.split('.')[0]
		if (path.includes('pdldata.person') || path[0].includes('pdldata.person'))
			rootPath = 'pdldata.person'
		if (_.isEmpty(_.get(newObj, rootPath))) newObj = _.omit(newObj, rootPath)

		setInitial(newObj)
	}
	const setMulti = (...arrays) => {
		const newInitial = arrays.reduce((acc, curr) => {
			const [path, val] = curr
			return _.set(path, val, acc)
		}, initial)
		setInitial(newInitial)
	}

	const allCheckedLabels = (attrs) =>
		Object.values(attrs)
			.filter((a) => a.checked)
			.map((b) => b.label)

	const batchUpdate = (attrs, fn, ignoreKeys = [], key = 'add') => {
		const state = Object.keys(attrs).reduce((acc, curr) => {
			// Handle ignored keys
			const ignores = Array.isArray(ignoreKeys) ? ignoreKeys : [ignoreKeys]
			if (ignores.includes(curr)) return acc

			const sm_local = stateModifierFns(acc, (newState) => (acc = newState), opts, dispatch)(fn)
			sm_local.attrs[curr][key]()
			return acc
		}, Object.assign({}, initial))

		setInitial(state)
	}

	return (fn) => {
		const attrs = fn({ set, setMulti, del, has, dispatch, opts, initial, setInitial }) || {}
		const checked = allCheckedLabels(attrs)
		return {
			attrs,
			checked,
			batchAdd: (ignoreKeys) => batchUpdate(attrs, fn, ignoreKeys, 'add'),
			batchRemove: (ignoreKeys) => batchUpdate(attrs, fn, ignoreKeys, 'remove'),
		}
	}
}
