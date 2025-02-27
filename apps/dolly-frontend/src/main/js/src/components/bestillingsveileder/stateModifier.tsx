import { useContext } from 'react'
import { UseFormReturn } from 'react-hook-form'
import { BestillingsveilederContext } from './BestillingsveilederContext'
import * as _ from 'lodash-es'

export const useStateModifierFns = (formMethods: UseFormReturn, setFormMutate: any) => {
	const { setValue, watch, resetField, getValues, reset, formState } = formMethods
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const set = (path: string, value: any) => setValue(path, value)
	const has = (path: string) => watch(path) !== undefined
	const values = (path: string) => watch(path)
	const delMutate = () => setFormMutate?.(() => undefined)

	const del = (path: string | string[]) => {
		if (Array.isArray(path)) {
			path.forEach((p) => resetField(p))
		} else {
			resetField(path)
		}
		let newObj = _.omit(getValues(), path)

		// Ingen tomme objekter guard
		let rootPath = Array.isArray(path) ? path[0].split('.')[0] : path.split('.')[0]
		if (path.includes('pdldata.person') || path[0].includes('pdldata.person'))
			rootPath = 'pdldata.person'
		if (_.isEmpty(_.get(newObj, rootPath))) newObj = _.omit(newObj, rootPath)
		reset(newObj)
	}

	const setMulti = (...arrays: [string, any][]) => {
		arrays.forEach(([path, val]) => setValue(path, val))
	}

	const allCheckedLabels = (attrs: Record<string, any>) =>
		Object.values(attrs)
			.filter((a) => a.checked)
			.map((b) => b.label)

	const batchUpdate = (
		attrs: Record<string, any>,
		ignoreKeys: string[] = [],
		key: 'add' | 'remove',
	) => {
		delMutate()
		Object.entries(attrs)
			.filter(([name]) => !ignoreKeys.includes(name))
			.forEach(([, value]) => value[key]())
	}

	return (
		fn: (args: {
			set: (path: string, value: any) => void
			setMulti: (...arrays: [string, any][]) => void
			opts: any
			del: (path: string | string[]) => void
			delMutate: () => void
			has: (path: string) => boolean
			values: (path: string) => any
			methods: UseFormReturn
		}) => Record<string, any>,
	) => {
		const attrs =
			fn({ set, setMulti, opts, del, delMutate, has, values, methods: formMethods }) || {}
		const checked = allCheckedLabels(attrs)
		return {
			attrs,
			checked,
			batchAdd: (ignoreKeys?: string[]) => batchUpdate(attrs, ignoreKeys, 'add'),
			batchRemove: (ignoreKeys?: string[]) => batchUpdate(attrs, ignoreKeys, 'remove'),
		}
	}
}
