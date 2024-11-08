import cn from 'classnames'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Icon from '@/components/ui/icon/Icon'
import styled from 'styled-components'
import React, { useContext } from 'react'
import { useFormContext } from 'react-hook-form'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import _ from 'lodash'
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
	useControlled?: boolean
	defaultValue?: string
	isDisabled?: boolean
	onKeyPress?: (val: any) => void
	autoFocus?: boolean
	fieldName?: string
	value?: any
	style?: any
	readOnly?: boolean
	onKeyDown?: any
	onSubmit?: Function
	customValidation?: Function
	onChange?: Function
	onPaste?: Function
	className?: string
	icon?: string
	datepickerOnclick?: Function
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

export const TextInput = React.forwardRef(
	(
		{
			placeholder = 'Ikke spesifisert',
			name,
			fieldName,
			className,
			icon,
			isDisabled,
			datepickerOnclick,
			...props
		}: TextInputProps,
		ref,
	) => {
		const {
			register,
			formState: { touchedFields },
			getFieldState,
		} = useFormContext()
		const { onChange, onBlur } = register(name)
		const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
		const isTouched = _.has(touchedFields, name) || _.has(touchedFields, fieldName)
		const feil = getFieldState(fieldName)?.error || getFieldState(name)?.error
		const visFeil = feil && (errorContext?.showError || isTouched)
		const css = cn('skjemaelement__input', className, {
			'skjemaelement__input--harFeil': visFeil,
		})

		return (
			<>
				<input
					disabled={isDisabled}
					id={name}
					className={css}
					placeholder={placeholder}
					{...props}
					ref={ref}
					onBlur={(e) => {
						onBlur(e)
						props.onBlur?.(e)
					}}
					onChange={(e) => {
						onChange(e)
						props.onChange?.(e)
					}}
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
	},
)

export const DollyTextInput = (props: TextInputProps) => (
	<InputWrapper {...props}>
		<Label name={props.name} label={props.label}>
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
