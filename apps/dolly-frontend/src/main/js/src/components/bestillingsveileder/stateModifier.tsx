import _, { isArray } from 'lodash'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { useContext } from 'react'
import { BestillingsveilederContext } from './BestillingsveilederContext'

export const useStateModifierFns = (formMethods: UseFormReturn) => {
	const opts = useContext(BestillingsveilederContext)
	const set = (path, value) => {
		formMethods.setValue(path, value)
	}
	const has = (path) => {
		return formMethods.watch(path) !== undefined
	}
	const values = (path) => {
		return formMethods.watch(path)
	}
	const del = (path) => {
		if (isArray(path)) {
			path.forEach((p) => {
				formMethods.resetField(p)
			})
		} else {
			formMethods.resetField(path)
		}
		let newObj = _.omit(formMethods.getValues(), path)

		// Ingen tomme objekter guard
		let rootPath = Array.isArray(path) ? path[0].split('.')[0] : path.split('.')[0]
		if (path.includes('pdldata.person') || path[0].includes('pdldata.person'))
			rootPath = 'pdldata.person'
		if (_.isEmpty(_.get(newObj, rootPath))) newObj = _.omit(newObj, rootPath)

		formMethods.reset(newObj)
	}
	const setMulti = (...arrays) => {
		arrays.forEach((curr) => {
			const [path, val] = curr
			formMethods.setValue(path, val)
		})
	}

	const allCheckedLabels = (attrs) =>
		Object.values(attrs)
			.filter((a) => a.checked)
			.map((b) => b.label)

	const batchUpdate = (attrs, ignoreKeys = [], key = 'add') => {
		Object.entries(attrs)
			.filter(([name, value]) => {
				return !ignoreKeys?.includes(name)
			})
			.forEach(([_name, value]: [name: string, value: any]) => {
				value[key]() // Call add or remove method
			})
	}

	return (
		fn: (arg0: {
			set: (path: any, value: any) => any
			setMulti: (...arrays: any[]) => void
			opts: any
			del: (path: any) => void
			has: (path: any) => boolean
			values: (path: any) => any
			methods: any
		}) => {},
	) => {
		const attrs = fn({ set, setMulti, opts, del, has, values, methods: formMethods }) || {}
		const checked = allCheckedLabels(attrs)
		return {
			attrs,
			checked,
			batchAdd: (ignoreKeys: never[] | undefined) => batchUpdate(attrs, ignoreKeys, 'add'),
			batchRemove: (ignoreKeys: never[] | undefined) => batchUpdate(attrs, ignoreKeys, 'remove'),
		}
	}
}
