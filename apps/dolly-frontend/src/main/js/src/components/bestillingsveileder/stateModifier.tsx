import { useCallback, useContext } from 'react'
import { FieldValues, UseFormReturn } from 'react-hook-form'
import { BestillingsveilederContext, BestillingsveilederContextType } from './BestillingsveilederContext'

type AttrItem = {
	checked?: boolean
	label?: string
	add?: () => void
	remove?: () => void
	[k: string]: any
}
type Attrs = Record<string, AttrItem>

interface HelperArgs<TForm extends FieldValues> {
	set: (path: string, value: unknown) => void
	setMulti: (...entries: [string, unknown][]) => void
	opts: BestillingsveilederContextType | undefined
	del: (path: string | string[]) => void
	delMutate: () => void
	has: (path: string) => boolean
	values: (path: string) => unknown
	methods: UseFormReturn<TForm>
}

interface ReturnBatch {
	attrs: Attrs
	checked: string[]
	batchAdd: (ignoreKeys?: string[]) => void
	batchRemove: (ignoreKeys?: string[]) => void
}

export const useStateModifierFns = <TForm extends FieldValues = FieldValues>(
	formMethods: UseFormReturn<TForm>,
	setFormMutate?: (fn: (() => void) | undefined) => void,
) => {
	const { setValue, watch, resetField, getValues, reset } = formMethods
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType | undefined

	const set = useCallback(
		(path: string, value: unknown) => {
			setValue(path as any, value)
		},
		[setValue],
	)

	const has = useCallback((path: string) => watch(path) !== undefined, [watch])
	const values = useCallback((path: string) => watch(path), [watch])
	const delMutate = useCallback(() => setFormMutate?.(() => undefined), [setFormMutate])

	const setMulti = useCallback(
		(...entries: [string, unknown][]) => {
			entries.forEach(([p, v]) => setValue(p as any, v))
		},
		[setValue],
	)

	const del = useCallback(
		(path: string | string[]) => {
			const paths = Array.isArray(path) ? path : [path]
			paths.forEach((p) => resetField(p as any))
			const current = { ...getValues() } as Record<string, any>

			let rootPath = paths[0].split('.')[0]
			if (paths.some((p) => p.includes('pdldata.person'))) rootPath = 'pdldata.person'

			const rootVal = rootPath.split('.').reduce<any>((o, key) => o?.[key], current)
			const isEmptyObject = (v: any) =>
				v == null || (typeof v === 'object' && !Array.isArray(v) && Object.keys(v).length === 0)

			if (isEmptyObject(rootVal)) {
				delete (current as any)[rootPath]
			}
			reset(current as any)
		},
		[getValues, reset, resetField],
	)

	return useCallback(
		(fn: (helpers: HelperArgs<TForm>) => Attrs | undefined): ReturnBatch => {
			const helpers: HelperArgs<TForm> = {
				set,
				setMulti,
				opts,
				del,
				delMutate,
				has,
				values,
				methods: formMethods,
			}
			const attrs = fn(helpers) || {}
			const checked = Object.values(attrs)
				.filter((a) => a.checked)
				.map((a) => a.label!)
				.filter(Boolean)

			const batchUpdate = (ignore: string[] = [], key: 'add' | 'remove') => {
				delMutate()
				Object.entries(attrs)
					.filter(([name]) => !ignore.includes(name))
					.forEach(([, v]) => v[key]?.())
			}

			return {
				attrs,
				checked,
				batchAdd: (ignoreKeys) => batchUpdate(ignoreKeys, 'add'),
				batchRemove: (ignoreKeys) => batchUpdate(ignoreKeys, 'remove'),
			}
		},
		[set, setMulti, opts, del, delMutate, has, values, formMethods],
	)
}
