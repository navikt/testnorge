import cn from 'classnames'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Icon from '@/components/ui/icon/Icon'
import styled from 'styled-components'
import React, { useContext, useEffect, useState } from 'react'
import { useFormContext } from 'react-hook-form'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import * as _ from 'lodash-es'
import FormFieldInput from '@/components/ui/form/inputs/textInput/FormFieldInput'
import { Button } from '@navikt/ds-react'

type TextInputProps = {
	placeholder?: string
	visHvisAvhuket?: boolean
	name: string
	label?: string
	size?: string
	type?: string
	useOnChange?: boolean
	onBlur?: (val: any) => void
	afterChange?: (val: any) => void
	onClick?: (val: any) => void
	onFocus?: (val: any) => void
	useControlled?: boolean
	defaultValue?: string
	isDisabled?: boolean
	onKeyPress?: (val: any) => void
	autoFocus?: boolean
	fieldName?: string
	value?: any
	input?: string
	style?: any
	readOnly?: boolean
	onKeyDown?: any
	'data-testid'?: string
	onSubmit?: Function
	onChange?: (val: any) => void
	onPaste?: Function
	className?: string
	icon?: string
	isDatepicker?: boolean
	title?: string
	datepickerOnclick?: Function
	manualError?: string
}

const StyledIcon = styled(Icon)`
	pointer-events: none;
	position: absolute;
	translate: 160px -30px;
`

const StyledButton = styled(Button)`
	&&& {
		svg {
			translate: -1px 1px;
		}

		position: absolute;
		height: 37px;
		translate: -31px 1px;
		padding: 5px 3px 0 3px;
		margin: 0;
	}
`

export const TextInput = ({
	placeholder = 'Ikke spesifisert',
	name,
	fieldName,
	className,
	icon,
	isDisabled,
	datepickerOnclick,
	autoFocus,
	...props
}: TextInputProps) => {
	const {
		register,
		formState: { touchedFields, errors },
		setValue,
		watch,
		getFieldState,
	} = useFormContext()
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	const { onChange, onBlur } = name ? register(name) : {}
	const input = props.input || props.value
	const [fieldValue, setFieldValue] = useState(props.input || watch(name) || '')

	const isTouched = _.has(touchedFields, name) || _.has(touchedFields, fieldName)
	const feil =
		getFieldState(`manual.${name}`)?.error ||
		getFieldState(name)?.error ||
		getFieldState(fieldName)?.error ||
		errors[name]
	const visFeil = (feil && (errorContext?.showError || isTouched)) || props.manualError

	const css = cn('skjemaelement__input', className, {
		'skjemaelement__input--harFeil': visFeil,
	})

	useEffect(() => {
		if (input !== undefined && input !== fieldValue) {
			setFieldValue(input)
		}
	}, [input])

	return (
		<>
			<input
				value={fieldValue || ''}
				autoFocus={autoFocus}
				disabled={isDisabled}
				type={props.type || 'text'}
				id={name}
				name={name}
				className={css}
				placeholder={placeholder}
				onBlur={(e) => {
					onBlur?.(e)
					props.onBlur?.(e)
					props.afterChange?.(e)
				}}
				onChange={(e) => {
					setValue(name, e.target.value)
					setFieldValue(e.target.value)
					onChange?.(e)
					props.onChange?.(e)
				}}
				data-testid={props['data-testid']}
				onClick={props.onClick}
				onFocus={props.onFocus}
				onKeyDown={props.onKeyDown}
				style={props.style}
			/>
			{icon &&
				(datepickerOnclick ? (
					<StyledButton variant="tertiary" onClick={datepickerOnclick}>
						<Icon kind={icon} style={{ color: 'black' }} fontSize={'1.5rem'} />
					</StyledButton>
				) : (
					<div style={{ height: '0' }}>
						<StyledIcon fontSize="1.5rem" kind={icon} />
					</div>
				))}
		</>
	)
}

export const DollyTextInput = (props: TextInputProps) => (
	<InputWrapper {...props}>
		<Label name={props.name} label={props.label} manualError={props.manualError}>
			<TextInput {...props} />
		</Label>
	</InputWrapper>
)

export const FormTextInput = ({ visHvisAvhuket = true, ...props }: TextInputProps) =>
	visHvisAvhuket ? (
		<Vis attributt={props.name}>
			<FormFieldInput {...props} />
		</Vis>
	) : (
		<FormFieldInput {...props} />
	)
