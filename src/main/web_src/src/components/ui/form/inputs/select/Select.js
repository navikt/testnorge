import React from 'react'
import { createFilter, default as ReactSelect } from 'react-select'
import { FormikField } from '~/components/ui/form/FormikField'
import cn from 'classnames'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { fieldError, SyntEvent } from '~/components/ui/form/formUtils'
import KodeverkConnector from '~/components/kodeverk/KodeverkConnector'

import './Select.less'
import MenuList from '~/components/ui/form/inputs/select/MenuList'
import Option from '~/components/ui/form/inputs/select/Option'

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
	let _value = isMulti
		? options.filter(o => value?.some(el => el === o.value))
		: options.filter(o => o.value === value)

	return (
		<ReactSelect
			value={_value}
			options={options}
			name={name}
			inputId={id || name}
			filterOption={createFilter({ ignoreAccents: false })}
			onChange={onChange}
			onBlur={onBlur}
			placeholder={placeholder}
			className={cn('basic-single', className)}
			classNamePrefix={classNamePrefix}
			components={{
				MenuList,
				Option
			}}
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
		{({ field, meta }) => {
			const handleChange = (selected, metaData) => {
				const value = props.isMulti ? handleMultiValue(metaData, selected) : selected?.value

				field.onChange(SyntEvent(field.name, value))

				if (props.afterChange) props.afterChange(selected)
			}

			const handleMultiValue = (metaData, selected) => {
				let value
				if (metaData.action === 'select-option') {
					value = Array.isArray(field.value)
						? field.value.concat(metaData.option.value)
						: [metaData.option.value]
				}
				if (metaData.action === 'remove-value') {
					// When removing last value, value is null
					value = selected ? selected.map(v => v.value) : []
				}
				return value
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
