import _has from 'lodash/has'
import _set from 'lodash/fp/set'
import _omit from 'lodash/omit'
import _isEmpty from 'lodash/isEmpty'
import _get from "lodash/get";
import _isNil from "lodash/isNil";
import { namePaths, idPaths } from './utils'

export const stateModifierFns = (initial, setInitial) => {
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

			const sm_local = stateModifierFns(acc, newState => (acc = newState))(fn)
			sm_local.attrs[curr][key]()
			return acc
		}, Object.assign({}, initial))

		setInitial(state)
	}

	return fn => {
		const attrs = fn({ set, setMulti, del, has, initial, setInitial }) || {}
		const checked = allCheckedLabels(attrs)
		return {
			attrs,
			checked,
			batchAdd: ignoreKeys => batchUpdate(attrs, fn, ignoreKeys, 'add'),
			batchRemove: ignoreKeys => batchUpdate(attrs, fn, ignoreKeys, 'remove')
		}
	}
}


export const stateModifierSeparateNames = (initial, setFieldValue) => {
	for (let i = 0; i < namePaths.length; i++) {
		const path = namePaths[i]
		const fullName = _get(initial, `${path}`)
		if (!_isNil(fullName)) {
			const deltNavn = (fullName + '').split(" ")

			setFieldValue(`${path}`, {
				fornavn: deltNavn[0],
				mellomnavn: deltNavn.length === 3 ? deltNavn[1] : '',
				etternavn: deltNavn[deltNavn.length - 1]
			})
		}
	}
}

export const stateModifierCombineNames = (initial, setFieldValue) => {
	for (let i = 0; i < namePaths.length; i++) {
		const path = namePaths[i]
		const fornavn = _get(initial, `${path}.fornavn`)
		if (!_isNil(fornavn)) {
			const mellomnavn = _get(initial, `${path}.mellomnavn`)
			const etternavn = " " + _get(initial, `${path}.etternavn`)

			setFieldValue(`${path}`,
				fornavn + (mellomnavn !=='' ? " " + mellomnavn : '') + etternavn)
		}
	}
}

export const stateModifierSeparateId = (initial, setFieldValue) => {
	for (let i = 0; i < idPaths.length; i++) {
		const path = idPaths[i]
		const fnrOgNavn = _get(initial, `${path}`)
		if (!_isNil(fnrOgNavn)) {
			const deltTekst = (fnrOgNavn + '').split(" ")
			setFieldValue(`${path}`, deltTekst[deltTekst.length - 1])
		}
	}
}
