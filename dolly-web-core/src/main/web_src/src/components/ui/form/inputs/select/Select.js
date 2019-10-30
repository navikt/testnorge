import React from 'react'
// import ReactSelect from 'react-select'
import { Select as ReactSelect } from 'react-select-virtualized'
import { useField } from 'formik'
import _get from 'lodash/get'
import cn from 'classnames'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { fieldError, SyntEvent } from '~/components/ui/form/formUtils'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'

import './Select.less'

export const Select = ({
	name,
	value,
	onChange,
	onBlur,
	className,
	classNamePrefix = 'select',
	isDisabled = false,
	isLoading = false,
	isSearchable = true,
	isClearable = true,
	placeholder = 'Velg..',
	options = []
}) => {
	return (
		<ReactSelect
			value={options.filter(o => o.value === value)}
			options={options}
			name={name}
			inputId={name}
			onChange={onChange}
			onBlur={onBlur}
			placeholder={placeholder}
			className={cn('basic-single', className)}
			classNamePrefix={classNamePrefix}
			isDisabled={isDisabled}
			isSearchable={isSearchable}
			isLoading={isLoading}
			isClearable={isClearable}
		/>
	)
}

export const SelectMedKodeverk = ({ kodeverk, ...rest }) => (
	<KodeverkConnector navn={kodeverk}>
		{kodeverkVerdier => (
			<Select
				{...rest}
				isLoading={!kodeverkVerdier}
				isDisabled={!kodeverkVerdier}
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

export const FormikSelect = props => {
	const [field, meta] = useField(props)
	const handleChange = selected =>
		field.onChange(SyntEvent(field.name, _get(selected, 'value', '')))

	const handleBlur = () => field.onBlur(SyntEvent(field.name))

	return (
		<DollySelect
			name={field.name}
			value={field.value}
			onChange={handleChange}
			onBlur={handleBlur}
			feil={fieldError(meta)}
			{...props}
		/>
	)
}
