import { createFilter, default as ReactSelect } from 'react-select'
import cn from 'classnames'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { SyntEvent } from '@/components/ui/form/formUtils'
import './Select.less'
import MenuList from '@/components/ui/form/inputs/select/MenuList'
import Option from '@/components/ui/form/inputs/select/Option'
import _ from 'lodash'
import { useKodeverk } from '@/utils/hooks/useKodeverk'
import { useController, useFormContext } from 'react-hook-form'
import { useContext } from 'react'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'

type SelectProps = {
	id?: string
	'data-testid'?: string
	name: string
	fieldName?: string
	value?: any
	className?: any
	classNamePrefix?: string
	onChange?: any
	onBlur?: any
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
	...rest
}: SelectProps) => {
	const formMethods = useFormContext()
	const val = formMethods?.watch(name)
	let formValue = isMulti
		? options?.filter?.((o) => val?.some((el) => el === o?.value))
		: options?.filter?.((o) => {
				return o?.value === val
			})

	let propValue = isMulti
		? options?.filter?.((o) => value?.some((el) => el === o?.value))
		: options?.filter?.((o) => {
				return o?.value === value
			})

	if (!onChange) {
		onChange = (selected, meta) => {
			formMethods?.setValue(name, selected?.value)
			formMethods?.trigger(name)
		}
	}

	return (
		<span data-testid={rest['data-testid']}>
			<ReactSelect
				value={!_.isEmpty(formValue) ? formValue : propValue}
				options={options}
				name={name}
				inputId={id || name}
				placeholder={placeholder}
				filterOption={createFilter({ ignoreAccents: false })}
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
						? (document.getElementsByClassName('navds-modal')[0] as HTMLElement)
						: document.getElementById('root')
				}
				menuPosition={isInDialog ? 'fixed' : undefined}
				{...rest}
			/>
		</span>
	)
}

export const SelectMedKodeverk = ({ kodeverk, label, isLoading, ...rest }: SelectProps) => {
	const { kodeverk: kodeverkResult, loading } = useKodeverk(kodeverk)
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
