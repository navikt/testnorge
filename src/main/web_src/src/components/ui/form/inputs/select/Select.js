import React from 'react'
// import ReactSelect from 'react-select'
import { Select as ReactSelect } from 'react-select-virtualized'
import { FormikField } from '~/components/ui/form/FormikField'
import cn from 'classnames'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { fieldError, SyntEvent } from '~/components/ui/form/formUtils'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'

import './Select.less'

export const Select = ({
	id,
	name,
	value,
	onChange,
	onBlur,
	className,
	optionHeight,
	classNamePrefix = 'select',
	disabled = false,
	isLoading = false,
	isSearchable = true,
	isClearable = true,
	placeholder = 'Velg..',
	options = [],
	isMulti = false,
	styles
}) => {
	let _value = options.filter(o => o.value === value)

	/**
	 * CUSTOM MULTI LOGIC
	 * react-select-virtualized støtter foreløpig ikke multi-select
	 * så denne biten må gjøres litt manuelt
	 */
	if (isMulti) {
		_value = Array.isArray(value) ? options.filter(o => value.includes(o.value)) : []
	}

	return (
		<ReactSelect
			value={_value}
			options={options}
			name={name}
			inputId={id || name}
			onChange={onChange}
			onBlur={onBlur}
			placeholder={placeholder}
			className={cn('basic-single', className)}
			classNamePrefix={classNamePrefix}
			isDisabled={disabled}
			isSearchable={isSearchable}
			isLoading={isLoading}
			isClearable={isClearable}
			isMulti={isMulti}
			styles={styles ? styles : { menuPortal: base => ({ ...base, zIndex: 99999 }) }}
			menuPortalTarget={document.getElementById('react-select-root')}
			optionHeight={optionHeight}
		/>
	)
}

export const SelectMedKodeverk = ({ kodeverk, ...rest }) => (
	<KodeverkConnector navn={kodeverk}>
		{kodeverkVerdier => (
			<Select
				{...rest}
				isLoading={!kodeverkVerdier}
				disabled={!kodeverkVerdier}
				options={kodeverkVerdier}
			/>
		)}
	</KodeverkConnector>
)

export const DollySelect = props => (
	<InputWrapper {...props}>
		<Label containerClass="dollyselect" name={props.name} label={props.label} feil={props.feil}>
			{props.kodeverk ? <SelectMedKodeverk {...props} /> : <Select {...props} />}
		</Label>
	</InputWrapper>
)

const P_FormikSelect = ({ fastfield, feil, ...props }) => (
	<FormikField name={props.name} fastfield={fastfield}>
		{({ field, form, meta }) => {
			const handleChange = (selected, meta) => {
				let value
				if (props.isMulti) {
					if (meta.action === 'set-value') {
						value = Array.isArray(field.value)
							? field.value.concat(selected.value)
							: [selected.value]
					}
					if (meta.action === 'remove-value') {
						// When removing last value, value is null
						value = selected ? selected.map(v => v.value) : []
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
		}}
	</FormikField>
)

export const FormikSelect = ({ visHvisAvhuket = false, ...props }) => {
	const component = <P_FormikSelect {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
