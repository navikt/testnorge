import { useContext } from 'react'
import { UseFormReturn } from 'react-hook-form'
import { BestillingsveilederContext } from './BestillingsveilederContext'
import * as _ from 'lodash-es'

export interface BestillingsveilederContextType {
	is?: {
		nyPerson?: boolean
		nyOrganisasjon?: boolean
		nyStandardOrganisasjon?: boolean
		nyOrganisasjonFraMal?: boolean
		leggTil?: boolean
		kopi?: boolean
	}
}

export interface AttributeMethod {
	(): void
}

export interface Attribute {
	label: string
	checked: boolean
	add: AttributeMethod
	remove: AttributeMethod
	[key: string]: any
}

export interface AttributeRecord {
	[key: string]: Attribute
}

export const useStateModifierFns = (formMethods: UseFormReturn, setFormMutate: any) => {
	const { setValue, watch, resetField, getValues, reset } = formMethods
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
		if (
			path.includes('pdldata.person') ||
			(Array.isArray(path) && path[0]?.includes('pdldata.person'))
		)
			rootPath = 'pdldata.person'
		if (_.isEmpty(_.get(newObj, rootPath))) newObj = _.omit(newObj, rootPath)
		reset(newObj)
	}

	const setMulti = (...arrays: [string, any][]) => {
		arrays.forEach(([path, val]) => setValue(path, val))
	}

	const allCheckedLabels = (attrs: AttributeRecord) =>
		Object.values(attrs)
			.filter((a) => a.checked)
			.map((b) => b.label)

	const batchUpdate = (
		attrs: AttributeRecord,
		ignoreKeys: string[] = [],
		key: 'add' | 'remove',
	) => {
		delMutate()

		const updateOperation = async () => {
			try {
				// Process each attribute sequentially
				for (const attrName of Object.keys(attrs)) {
					if (ignoreKeys.includes(attrName)) continue

					const attr = attrs[attrName]
					if (!attr || typeof attr[key] !== 'function') continue

					// Execute the operation
					await Promise.resolve(attr[key]())
				}

				// Trigger re-render
				setValue('_triggerRender', Date.now(), { shouldDirty: false })
				return { status: 'OK' }
			} catch (error) {
				console.error(`Error in batch ${key} operation:`, error)
				return { status: 'ERROR', error }
			}
		}

		setFormMutate(() => updateOperation)
		return updateOperation
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
		const checked = allCheckedLabels(attrs as AttributeRecord)

		return {
			attrs,
			checked,
			batchAdd: (ignoreKeys?: string[]) => batchUpdate(attrs as AttributeRecord, ignoreKeys, 'add'),
			batchRemove: (ignoreKeys?: string[]) =>
				batchUpdate(attrs as AttributeRecord, ignoreKeys, 'remove'),
		}
	}
}
