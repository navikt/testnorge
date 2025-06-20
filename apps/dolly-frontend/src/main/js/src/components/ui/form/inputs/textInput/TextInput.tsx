import React, { useCallback, useEffect, useRef, useState } from 'react'
import cn from 'classnames'
import styled from 'styled-components'
import { useFormContext } from 'react-hook-form'
import * as _ from 'lodash-es'

import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Icon from '@/components/ui/icon/Icon'
import { ShowErrorContext } from '@/components/bestillingsveileder/ShowErrorContext'
import FormFieldInput from '@/components/ui/form/inputs/textInput/FormFieldInput'

const StyledIcon = styled(Icon)`
	pointer-events: none;
	position: absolute;
	translate: 160px -30px;
`

type TextInputProps = {
	name: string
	label?: string
	placeholder?: string
	defaultValue?: string
	value?: string
	fieldName?: string
	type?: 'text' | 'number'
	className?: string
	icon?: string
	isDisabled?: boolean
	readOnly?: boolean
	autoFocus?: boolean
	manualError?: string
	visHvisAvhuket?: boolean
	style?: React.CSSProperties
	'data-testid'?: string
	useOnChange?: boolean
	useControlled?: boolean
	onChange?: (event: React.ChangeEvent<HTMLInputElement>) => void
	onBlur?: (event: React.FocusEvent<HTMLInputElement>) => void
	onClick?: (event: React.MouseEvent<HTMLInputElement>) => void
	onFocus?: (event: React.FocusEvent<HTMLInputElement>) => void
	onKeyDown?: (event: React.KeyboardEvent<HTMLInputElement>) => void
	onSubmit?: () => void
	afterChange?: (event: React.FocusEvent<HTMLInputElement>) => void
}

export const TextInput = ({
	placeholder = 'Ikke spesifisert',
	name,
	fieldName,
	className,
	icon,
	isDisabled,
	autoFocus,
	type = 'text',
	value,
	defaultValue,
	style,
	'data-testid': dataTestId,
	useControlled = false,
	...props
}: TextInputProps) => {
	const { register, formState, setValue, watch } = useFormContext() || {}
	const { showError } = React.useContext(ShowErrorContext) || {}

	const { onChange: registerOnChange, onBlur: registerOnBlur } =
		name && register ? register(name) : {}

	const initialValue = value ?? defaultValue ?? (name ? watch(name) || '' : '')
	const [fieldValue, setFieldValue] = useState(initialValue)
	const validateTimeoutRef = useRef<NodeJS.Timeout | null>(null)

	const formValue = name ? watch(name) : undefined

	const isTouched =
		formState?.touchedFields &&
		((fieldName && _.has(formState.touchedFields, fieldName)) ||
			(name && _.has(formState.touchedFields, name)))

	const error =
		formState?.errors &&
		(_.get(formState.errors, `manual.${name}`) ||
			_.get(formState.errors, name) ||
			(fieldName && _.get(formState.errors, fieldName)))

	const shouldShowError = (error && (showError || isTouched)) || !!props.manualError

	useEffect(() => {
		if (useControlled && formValue !== undefined) {
			setFieldValue(formValue || '')
		} else if (!useControlled) {
			const propValue = value
			if (propValue !== undefined) {
				setFieldValue(propValue || '')
			}
		}
	}, [value, formValue, useControlled])

	const handleChange = useCallback(
		(e: React.ChangeEvent<HTMLInputElement>) => {
			const newValue = e.target.value
			setFieldValue(newValue)

			// Update form without validation during typing
			if (name && setValue) {
				setValue(name, newValue, { shouldDirty: true, shouldValidate: false })
			}

			registerOnChange?.(e)
			props.onChange?.(e)

			if (validateTimeoutRef.current) {
				clearTimeout(validateTimeoutRef.current)
			}

			validateTimeoutRef.current = setTimeout(() => {
				if (name && setValue) {
					setValue(name, newValue, { shouldValidate: true })
				}
			}, 400) // Validate after inactivity
		},
		[name, setValue, registerOnChange, props.onChange],
	)

	const handleBlur = useCallback(
		(e: React.FocusEvent<HTMLInputElement>) => {
			if (validateTimeoutRef.current) {
				clearTimeout(validateTimeoutRef.current)
				validateTimeoutRef.current = null
			}

			registerOnBlur?.(e)
			props.onBlur?.(e)

			if (name && setValue) {
				setValue(name, fieldValue, { shouldValidate: true })
			}

			props.afterChange?.(e)
		},
		[name, setValue, fieldValue, registerOnBlur, props.onBlur, props.afterChange],
	)

	useEffect(() => {
		return () => {
			if (validateTimeoutRef.current) {
				clearTimeout(validateTimeoutRef.current)
			}
		}
	}, [])

	const inputClassNames = cn('skjemaelement__input', className, {
		'skjemaelement__input--harFeil': shouldShowError,
	})

	return (
		<>
			<input
				id={name}
				name={name}
				value={fieldValue || ''}
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

			{icon && (
				<div style={{ height: '0' }}>
					<StyledIcon fontSize="1.5rem" kind={icon} />
				</div>
			)}
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
