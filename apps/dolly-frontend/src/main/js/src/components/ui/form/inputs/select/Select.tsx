import { createFilter, default as ReactSelect } from 'react-select'
import cn from 'classnames'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { SyntEvent } from '@/components/ui/form/formUtils'
import './Select.less'
import MenuList from '@/components/ui/form/inputs/select/MenuList'
import Option from '@/components/ui/form/inputs/select/Option'
import * as _ from 'lodash-es'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { useController, useFormContext } from 'react-hook-form'
import React, { createRef, useContext, useEffect, useMemo, useState } from 'react'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'

const LARGE_LIST_THRESHOLD = 500
const MAX_DISPLAYED_OPTIONS = 200
const defaultFilter = createFilter({ ignoreAccents: false })

type SelectProps = {
	id?: string
	'data-testid'?: string
	name: string
	fieldName?: string
	value?: any
	defaultValue?: any
	className?: any
	classNamePrefix?: string
	onChange?: any
	onBlur?: any
	autoFocus?: boolean
	isDisabled?: boolean
	isLoading?: boolean
	isSearchable?: boolean
	isClearable?: boolean
	placeholder?: string
	options?: any
	optionHeight?: number
	isMulti?: boolean
	styles?: any
	kodeverk?: string
	label?: string
	feil?: any
	size?: string
	info?: any
	normalFontPlaceholder?: boolean
	visHvisAvhuket?: any
	afterChange?: any
	isInDialog?: boolean
}

export const Select = ({
	id,
	name,
	value,
	className,
	classNamePrefix = 'select',
	defaultValue,
	isDisabled = false,
	isLoading = false,
	isSearchable = true,
	isClearable = true,
	placeholder = 'Velg ...',
	options = [],
	isMulti = false,
	styles,
	onChange,
	isInDialog = false,
	normalFontPlaceholder = false,
	autoFocus = false,
	...rest
}: SelectProps) => {
	const formMethods = useFormContext()
	const ref = createRef()
	const [inputValue, setInputValue] = useState('')
	const isLargeList = options.length > LARGE_LIST_THRESHOLD
	const val = formMethods?.watch(name)

	const selectedValues = useMemo(() => {
		if (!isLargeList) return new Set<string>()
		const current = isMulti ? val : val != null ? [val] : []
		const fromProp = isMulti ? (value ?? []) : value != null ? [value] : []
		return new Set<string>([...current, ...fromProp].map(String))
	}, [val, value, isMulti, isLargeList])

	const displayedOptions = useMemo(() => {
		if (!isLargeList) return options

		if (!inputValue) {
			const truncated = options.slice(0, MAX_DISPLAYED_OPTIONS)
			if (selectedValues.size === 0) return truncated

			const truncatedSet = new Set(truncated.map((o) => String(o.value)))
			const missing = options.filter(
				(o) => selectedValues.has(String(o.value)) && !truncatedSet.has(String(o.value)),
			)
			return missing.length > 0 ? [...missing, ...truncated] : truncated
		}

		const needle = inputValue.toLowerCase()
		const matches = []
		for (const option of options) {
			if (
				option.label?.toLowerCase().includes(needle) ||
				option.value?.toString().toLowerCase().includes(needle)
			) {
				matches.push(option)
				if (matches.length >= MAX_DISPLAYED_OPTIONS) break
			}
		}
		return matches
	}, [options, inputValue, isLargeList, selectedValues])

	useEffect(() => {
		if (autoFocus) {
			ref?.current?.focus?.()
		}
	}, [autoFocus])

	useEffect(() => {
		if (defaultValue && options.some((option) => option.value === defaultValue)) {
			formMethods?.setValue(name, defaultValue)
		}
	}, [])

	let formValue = isMulti
		? options?.filter?.((o) => val?.some((el) => el === o?.value))
		: options?.filter?.((o) => o?.value === val)

	let propValue = isMulti
		? options?.filter?.((o) => value?.some((el) => el === o?.value))
		: options?.filter?.((o) => o?.value === value)

	if (!onChange) {
		onChange = (selected, meta) => {
			formMethods?.setValue(name, selected?.value)
			formMethods?.trigger(name)
		}
	}

	const getPlaceholder = () => {
		if (isLoading) {
			return 'Laster ...'
		} else if (options?.length === 0) {
			return 'Ingen tilgjengelige verdier'
		}
		if (isLargeList) {
			return 'Skriv for å søke ...'
		}
		return placeholder
	}
	const placeholderText = getPlaceholder()

	return (
		<span data-testid={rest['data-testid']}>
			<ReactSelect
				value={!_.isEmpty(formValue) ? formValue : propValue}
				defaultValue={propValue}
				autoFocus={autoFocus}
				options={displayedOptions}
				name={name}
				inputId={id || name}
				placeholder={placeholderText}
				filterOption={isLargeList ? null : defaultFilter}
				onInputChange={isLargeList ? setInputValue : undefined}
				className={cn('basic-single', className)}
				classNamePrefix={classNamePrefix}
				components={{
					MenuList,
					Option,
				}}
				isDisabled={isDisabled}
				isSearchable={isSearchable}
				isLoading={isLoading}
				isClearable={isClearable}
				isMulti={isMulti}
				theme={(theme) => ({
					...theme,
					colors: {
						...theme.colors,
						neutral50: normalFontPlaceholder ? 'unset' : '',
					},
				})}
				onChange={onChange}
				styles={styles ? styles : { menuPortal: (base) => ({ ...base, zIndex: 99999 }) }}
				// Naar vi bruker modal fra Aksel maa vi referere til modalens className for at dropdowns ikke skal forsvinne bak modalen
				menuPortalTarget={
					isInDialog
						? (document.getElementsByClassName('aksel-modal')[0] as HTMLElement)
						: document.getElementById('root')
				}
				menuPosition={isInDialog ? 'fixed' : undefined}
				ref={ref}
				{...rest}
			/>
		</span>
	)
}

export const SelectMedKodeverk = ({ kodeverk, label, isLoading, ...rest }: SelectProps) => {
	const [numRetries, setNumRetries] = React.useState(0)
	const { kodeverk: kodeverkResult, loading, error, mutate } = useKodeverk(kodeverk)

	useEffect(() => {
		if (!loading && kodeverkResult?.length === 0 && numRetries < 3) {
			setTimeout(() => {
				setNumRetries(numRetries + 1)
				console.warn('Retry loading kodeverk: ' + kodeverk)
				mutate()
			}, 1000)
		}
	}, [loading, kodeverkResult])

	if (loading) {
		return <Select {...rest} isLoading={true} />
	}

	if (error) {
		return <Select {...rest} feil={error} />
	}

	const getSortedKodeverk = (kodeverkVerdier) => {
		if (label === 'Bostedskommune') {
			const kodeverkClone = _.cloneDeep(kodeverkVerdier)
			const ukjentIndex = kodeverkClone?.findIndex((kode) => kode.value === '9999')
			const ukjentBosted = kodeverkClone?.splice(ukjentIndex, 1)[0]
			kodeverkClone?.splice(0, 0, ukjentBosted)
			return kodeverkClone
		}
		return kodeverkVerdier
	}

	return (
		<Select
			{...rest}
			isLoading={!kodeverkResult || isLoading}
			options={getSortedKodeverk(kodeverkResult)}
		/>
	)
}

export const DollySelect = (props: SelectProps) => (
	<InputWrapper {...props}>
		<Label
			containerClass="dollyselect"
			fieldName={props.fieldName}
			name={props.name}
			label={props.label}
			info={props.info}
		>
			{props.kodeverk ? <SelectMedKodeverk {...props} /> : <Select {...props} />}
		</Label>
	</InputWrapper>
)

const P_FormSelect = ({ feil, ...props }: SelectProps) => {
	const { field } = useController(props)
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const formMethods = useFormContext()
	const touchedFields = formMethods?.formState.touchedFields
	const isTouched = _.has(touchedFields, props.name)
	const handleChange = (selected, meta) => {
		let value
		if (props.isMulti) {
			if (meta.action === 'select-option') {
				value = Array.isArray(field.value)
					? field.value.concat(meta.option.value)
					: [meta.option.value]
			}
			if (meta.action === 'remove-value') {
				value = selected ? selected.map((v) => v.value) : []
			}
		} else {
			value = selected && selected.value
		}
		field.onChange(SyntEvent(field.name, value))
		if (props.afterChange) props.afterChange(selected)
		formMethods.trigger(props.name)
	}

	const handleBlur = () => field?.onBlur?.(SyntEvent(field.name))

	return (
		<DollySelect
			name={field.name}
			onChange={handleChange}
			onBlur={handleBlur}
			feil={
				((errorContext?.showError || isTouched) &&
					(feil || formMethods?.getFieldState(`manual.${props.name}`)?.error)) ||
				formMethods?.getFieldState(props.name)?.error ||
				formMethods?.getFieldState(props.fieldName)?.error
			}
			{...props}
		/>
	)
}

export const FormSelect = ({ visHvisAvhuket = false, vis = true, ...props }: SelectProps) => {
	const component = <P_FormSelect {...props} />
	return vis && (visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component)
}
