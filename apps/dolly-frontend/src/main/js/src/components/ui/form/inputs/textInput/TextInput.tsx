import React from 'react'
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
	size?: 'small' | 'medium' | 'large' | 'xlarge' | 'xxlarge'
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
	style,
	'data-testid': dataTestId,
	...props
}: TextInputProps) => {
	const form = useFormContext()
	const {
		register,
		formState: { errors = {}, touchedFields = {}, isSubmitted = false, submitCount = 0 } = {},
	} = form || ({} as any)

	const { showError } = React.useContext(ShowErrorContext) || {}

	const { onChange, onBlur, ...restOfRegister } = name ? register(name) : ({} as any)

	const isTouched =
		(fieldName && _.has(touchedFields, fieldName)) || (name && _.has(touchedFields, name))

	const error =
		(name && errors[name]) || (fieldName && errors[fieldName]) || _.get(errors, `manual.${name}`)

	const hasSubmitted = isSubmitted || submitCount > 0
	const shouldShowError =
		(!!error && (showError || isTouched || hasSubmitted)) || !!props.manualError

	const handleBlur = React.useCallback(
		(e: React.FocusEvent<HTMLInputElement>) => {
			onBlur?.(e)
			props.onBlur?.(e)
			props.afterChange?.(e)
		},
		[onBlur, props.onBlur, props.afterChange],
	)

	const handleChange = React.useCallback(
		(e: React.ChangeEvent<HTMLInputElement>) => {
			onChange?.(e)
			props.onChange?.(e)
		},
		[onChange, props.onChange],
	)

	const inputClassNames = cn('skjemaelement__input', className, {
		'skjemaelement__input--harFeil': shouldShowError,
	})

	return (
		<>
			<input
				id={name}
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
				{...restOfRegister}
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
