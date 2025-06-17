import React, { useEffect, useState } from 'react'
import cn from 'classnames'
import styled from 'styled-components'
import { FieldError, useFormContext } from 'react-hook-form'
import { Button } from '@navikt/ds-react'
import * as _ from 'lodash-es'

import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Icon from '@/components/ui/icon/Icon'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'
import FormFieldInput from '@/components/ui/form/inputs/textInput/FormFieldInput'

type TextInputProps = {
	name: string
	label?: string
	placeholder?: string
	defaultValue?: string
	value?: string
	fieldName?: string
	type?: 'text' | 'password' | 'email' | 'number' | 'tel'
	className?: string
	icon?: string
	isDisabled?: boolean
	isDatepicker?: boolean
	readOnly?: boolean
	autoFocus?: boolean
	manualError?: string
	visHvisAvhuket?: boolean
	style?: React.CSSProperties
	'data-testid'?: string
	useOnChange?: boolean
	useControlled?: boolean
	input?: string

	onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void
	onBlur?: (event: React.FocusEvent<HTMLInputElement>) => void
	onClick?: (event: React.MouseEvent<HTMLInputElement>) => void
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void
	onKeyDown?: (event: React.KeyboardEvent<HTMLInputElement>) => void
	onPaste?: (event: React.ClipboardEvent<HTMLInputElement>) => void
	onSubmit?: () => void
	afterChange?: (event: React.FocusEvent<HTMLInputElement>) => void
	datepickerOnclick?: React.MouseEventHandler<HTMLButtonElement>
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
	type = 'text',
	value,
	input,
	style,
	'data-testid': dataTestId,
	...props
}: TextInputProps) => {
	const {
		register,
		formState: { touchedFields, errors },
		setValue,
		watch,
		getFieldState,
	} = useFormContext() || {}
	const { showError } = React.useContext(ShowErrorContext) || {}

	const { onChange: registerOnChange, onBlur: registerOnBlur } =
		name && register ? register(name) : {}

	const initialValue =
		input !== undefined ? input : value !== undefined ? value : watch ? watch(name) || '' : ''
	const [fieldValue, setFieldValue] = useState(initialValue)

	const isTouched =
		touchedFields &&
		((fieldName && _.has(touchedFields, fieldName)) || (name && _.has(touchedFields, name)))

	const getError = (): FieldError | undefined => {
		if (!getFieldState) return undefined

		return (
			getFieldState(`manual.${name}`)?.error ||
			getFieldState(name)?.error ||
			(fieldName ? getFieldState(fieldName)?.error : undefined) ||
			(errors && name ? errors[name as any] : undefined)
		)
	}

	const error = getError()
	const shouldShowError = (error && (showError || isTouched)) || !!props.manualError

	useEffect(() => {
		const newValue = input !== undefined ? input : value
		if (newValue !== undefined && newValue !== fieldValue) {
			setFieldValue(newValue)
		}
	}, [input, value])

	const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		const newValue = e.target.value
		setFieldValue(newValue)

		if (name && setValue) {
			setValue(name, newValue, { shouldValidate: true, shouldDirty: true })
		}

		if (registerOnChange) registerOnChange(e)
		if (props.onChange) props.onChange(e)
	}

	const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
		if (registerOnBlur) registerOnBlur(e)
		if (props.onBlur) props.onBlur(e)
		if (props.afterChange) props.afterChange(e)
	}

	const inputClassNames = cn('skjemaelement__input', className, {
		'skjemaelement__input--harFeil': shouldShowError,
	})

	return (
		<>
			<input
				id={name}
				name={name}
				value={fieldValue}
				type={type}
				disabled={isDisabled}
				autoFocus={autoFocus}
				className={inputClassNames}
				placeholder={placeholder}
				onChange={handleChange}
				onBlur={handleBlur}
				onClick={props.onClick}
				onFocus={props.onFocus}
				onKeyDown={props.onKeyDown}
				style={style}
				data-testid={dataTestId}
				readOnly={props.readOnly}
			/>

			{icon &&
				(datepickerOnclick ? (
					<StyledButton variant="tertiary" onClick={datepickerOnclick}>
						<Icon kind={icon} style={{ color: 'black' }} fontSize="1.5rem" />
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
		<Label name={props.name} label={props.label || ''} manualError={props.manualError}>
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
