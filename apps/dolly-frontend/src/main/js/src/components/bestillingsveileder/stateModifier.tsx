import { useDispatch } from 'react-redux'
import { isArray } from 'lodash'

export const stateModifierFns = (methods, opts = null) => {
	const dispatch = useDispatch()
	const set = (path, value) => {
		return methods.setValue(path, value)
	}
	const has = (path) => {
		return methods.watch(path) !== undefined
		// return methods.getValues(path) !== undefined
	}
	const del = (path) => {
		// methods.resetField(path)
		if (isArray(path)) {
			path.forEach((p) => {
				methods.setValue(p, undefined)
				methods.resetField(p)
			})
		} else {
			methods.watch(path)
			methods.setValue(path, undefined)
			methods.resetField(path)
		}
		// methods.setValue(path, undefined)
		// methods.resetField(path)
		// let newObj = _.omit(initial, path)
		//
		// // Ingen tomme objekter guard
		// let rootPath = Array.isArray(path) ? path[0].split('.')[0] : path.split('.')[0]
		// if (path.includes('pdldata.person') || path[0].includes('pdldata.person'))
		// 	rootPath = 'pdldata.person'
		// if (_.isEmpty(_.get(newObj, rootPath))) newObj = _.omit(newObj, rootPath)
		//
		// setInitial(newObj)
	}
	const setMulti = (...arrays) => {
		arrays.forEach((curr) => {
			const [path, val] = curr
			methods.setValue(path, val)
		})
		// methods.setValue(newInitial)
	}

	const allCheckedLabels = (attrs) =>
		Object.values(attrs)
			.filter((a) => a.checked)
			.map((b) => b.label)

	const batchUpdate = (attrs, fn, ignoreKeys = [], key = 'add') => {
		const state = Object.keys(attrs).reduce(
			(acc, curr) => {
				// Handle ignored keys
				const ignores = Array.isArray(ignoreKeys) ? ignoreKeys : [ignoreKeys]
				if (ignores.includes(curr)) return acc

				const sm_local = stateModifierFns(acc, (newState) => (acc = newState), opts, dispatch)(fn)
				sm_local.attrs[curr][key]()
				return acc
			},
			Object.assign({}, methods.getValues()),
		)

		methods.setValue(state)
	}

	return (
		fn: (arg0: {
			set: (path: any, value: any) => any
			setMulti: (...arrays: any[]) => void
			del: (path: any) => void
			has: (path: any) => boolean
			dispatch: null
			opts: null
			methods: any
		}) => {},
	) => {
		const attrs = fn({ set, setMulti, del, has, dispatch, opts, methods }) || {}
		const checked = allCheckedLabels(attrs)
		return {
			attrs,
			checked,
			batchAdd: (ignoreKeys: never[] | undefined) => batchUpdate(attrs, fn, ignoreKeys, 'add'),
			batchRemove: (ignoreKeys: never[] | undefined) =>
				batchUpdate(attrs, fn, ignoreKeys, 'remove'),
		}
	}
}
