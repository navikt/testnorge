import React from 'react'
import { useField } from 'formik'
import { createFilter, default as ReactSelect } from 'react-select'
import cn from 'classnames'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { fieldError, SyntEvent } from '~/components/ui/form/formUtils'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'
import './Select.less'
import MenuList from '~/components/ui/form/inputs/select/MenuList'
import Option from '~/components/ui/form/inputs/select/Option'

type SelectProps = {
	id?: string
	name: string
	value?: any
	className?: any
	classNamePrefix?: string
	isDisabled?: boolean
	isLoading?: boolean
	isSearchable?: boolean
	isClearable?: boolean
	placeholder?: string
	options?: any
	isMulti?: boolean
	styles?: any
	kodeverk?: string
	label?: string
	feil?: { feilmelding: string }
	info?: any
	visHvisAvhuket?: any
	afterChange?: any
	fastfield?: boolean
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
	placeholder = 'Velg..',
	options = [],
	isMulti = false,
	styles,
	...rest
}: SelectProps) => {
	let _value = isMulti
		? options.filter((o) => value?.some((el) => el === o.value))
		: options.filter((o) => o.value === value)

	return (
		<ReactSelect
			value={_value}
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
			styles={styles ? styles : { menuPortal: (base) => ({ ...base, zIndex: 99999 }) }}
			menuPortalTarget={document.getElementById('react-select-root')}
			{...rest}
		/>
	)
}

export const SelectMedKodeverk = ({ kodeverk, isLoading, ...rest }: SelectProps) => (
	<KodeverkConnector navn={kodeverk}>
		{(kodeverkVerdier) => (
			<Select {...rest} isLoading={!kodeverkVerdier || isLoading} options={kodeverkVerdier} />
		)}
	</KodeverkConnector>
)

export const DollySelect = (props: SelectProps) => (
	<InputWrapper {...props}>
		<Label
			containerClass="dollyselect"
			name={props.name}
			label={props.label}
			feil={props.feil}
			info={props.info}
		>
			{props.kodeverk ? <SelectMedKodeverk {...props} /> : <Select {...props} />}
		</Label>
	</InputWrapper>
)

const P_FormikSelect = ({ _fastfield, feil, ...props }: SelectProps) => {
	const [field, meta] = useField(props)
	const handleChange = (selected, meta) => {
		let value
		if (props.isMulti) {
			if (meta.action === 'select-option') {
				value = Array.isArray(field.value)
					? field.value.concat(meta.option.value)
					: [meta.option.value]
			}
			if (meta.action === 'remove-value') {
				// When removing last value, value is null
				value = selected ? selected.map((v) => v.value) : []
			}
		} else {
			value = selected && selected.value
		}
		field.onChange(SyntEvent(field.name, value))
		if (props.afterChange) props.afterChange(selected)
	}

	const handleBlur = () => field.onBlur(SyntEvent(field.name))

	return (
		<DollySelect
			name={field.name}
			value={field.value}
			onChange={handleChange}
			onBlur={handleBlur}
			feil={feil || fieldError(meta)}
			{...props}
		/>
	)
}

export const FormikSelect = ({ visHvisAvhuket = false, ...props }: SelectProps) => {
	const component = <P_FormikSelect {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
