import { useContext } from 'react'
import { UseFormReturn } from 'react-hook-form'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from './BestillingsveilederContext'
import * as _ from 'lodash-es'

export const useStateModifierFns = (formMethods: UseFormReturn, setFormMutate: any) => {
	'use no memo' // Skip compilation for this component

	const { setValue, watch, resetField, getValues, reset } = formMethods
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const set = (path: string, value: any) => setValue(path, value)
	const has = (path: string) => watch(path) !== undefined
	const values = (path: string) => watch(path)
	const delMutate = () => setFormMutate?.(() => undefined)

	const del = (path: string | string[]) => {
		if (Array.isArray(path)) path.forEach((p) => resetField(p))
		else resetField(path)

		let newObj = _.omit(getValues(), path)
		let rootPath = Array.isArray(path) ? path[0].split('.')[0] : path.split('.')[0]
		if (
			(Array.isArray(path) && path[0].includes('pdldata.person')) ||
			(!Array.isArray(path) && path.includes('pdldata.person'))
		)
			rootPath = 'pdldata.person'
		if (_.isEmpty(_.get(newObj, rootPath))) newObj = _.omit(newObj, rootPath)
		reset(newObj)
	}

	const setMulti = (...arrays: [string, any][]) => {
		arrays.forEach(([path, val]) => setValue(path, val))
	}

	const allCheckedLabels = (attrs: Record<string, any>) =>
		Object.values(attrs)
			.filter((a: any) => a.checked) // getter recomputed each render
			.map((b: any) => b.label)

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
		// Build attrs with checked as a getter tied to form values
		const attrs =
			fn({
				set,
				setMulti,
				opts,
				del,
				delMutate,
				has,
				values,
				methods: formMethods,
			}) || {}

		const checked = allCheckedLabels(attrs)
		return {
			attrs,
			checked,
			batchAdd: (ignoreKeys?: string[]) => batchUpdate(attrs, ignoreKeys, 'add'),
			batchRemove: (ignoreKeys?: string[]) => batchUpdate(attrs, ignoreKeys, 'remove'),
		}
	}
}
