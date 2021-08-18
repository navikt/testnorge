import _has from 'lodash/has'
import _set from 'lodash/fp/set'
import _omit from 'lodash/omit'
import _isEmpty from 'lodash/isEmpty'

export const stateModifierFns = (initial, setInitial, options = null) => {
	const opts = options
	const set = (path, value) => setInitial(_set(path, value, initial))
	const has = path => _has(initial, path)
	const del = path => {
		let newObj = _omit(initial, path)

		// Ingen tomme objekter guard
		const rootPath = Array.isArray(path) ? path[0].split('.')[0] : path.split('.')[0]
		if (_isEmpty(newObj[rootPath])) newObj = _omit(newObj, rootPath)

		setInitial(newObj)
	}
	const setMulti = (...arrays) => {
		const newInitial = arrays.reduce((acc, curr) => {
			const [path, val] = curr
			return _set(path, val, acc)
		}, initial)
		setInitial(newInitial)
	}

	const allCheckedLabels = attrs =>
		Object.values(attrs)
			.filter(a => a.checked)
			.map(b => b.label)

	const batchUpdate = (attrs, fn, ignoreKeys = [], key = 'add') => {
		const state = Object.keys(attrs).reduce((acc, curr) => {
			// Handle ignored keys
			const ignores = Array.isArray(ignoreKeys) ? ignoreKeys : [ignoreKeys]
			if (ignores.includes(curr)) return acc

			const sm_local = stateModifierFns(acc, newState => (acc = newState), opts)(fn)
			sm_local.attrs[curr][key]()
			return acc
		}, Object.assign({}, initial))

		setInitial(state)
	}

	return fn => {
		const attrs = fn({ set, setMulti, del, has, opts, initial, setInitial }) || {}
		const checked = allCheckedLabels(attrs)
		return {
			attrs,
			checked,
			batchAdd: ignoreKeys => batchUpdate(attrs, fn, ignoreKeys, 'add'),
			batchRemove: ignoreKeys => batchUpdate(attrs, fn, ignoreKeys, 'remove')
		}
	}
}
