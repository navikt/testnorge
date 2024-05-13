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

const StyledIcon = styled(Icon)`
	pointer-events: none;
	position: absolute;
	top: 7px;
	right: 7px;
`

export const TextInput = React.forwardRef(
	(
		{
			placeholder = 'Ikke spesifisert',
			isDatepicker = false,
			name,
			fieldName,
			className,
			icon,
			isDisabled,
			...props
		}: {
			name: string
			fieldName: string
			className?: string
			icon?: string
			placeholder?: string
			isDisabled?: boolean
			isDatepicker?: boolean
			onChange?: any
		},
		ref,
	) => {
		const {
			register,
			formState: { touchedFields },
			getFieldState,
		} = useFormContext()
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
					{...(name && !isDatepicker && register?.(name))}
					{...props}
				/>
				{icon && <StyledIcon fontSize={'1.5rem'} kind={icon} />}
			</>
		)
	},
)

export const DollyTextInput = (props: {
	fieldName?: string
	name?: string
	label?: string
	value?: any
	style?: any
	size?: string
	type?: string
	readOnly?: boolean
	onKeyDown?: any
	useOnChange?: boolean
	isDisabled?: boolean
	onBlur?: Function
	onSubmit?: Function
	onChange?: Function
	onPaste?: Function
	onKeyPress?: Function
	placeholder?: string
}) => (
	<InputWrapper {...props}>
		<Label name={props.name} label={props.label}>
			<TextInput {...props} />
		</Label>
	</InputWrapper>
)

export const FormTextInput = ({
	visHvisAvhuket = true,
	...props
}: {
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
}) =>
	visHvisAvhuket ? (
		<Vis attributt={props.name}>
			<FormFieldInput {...props} />
		</Vis>
	) : (
		<FormFieldInput {...props} />
	)
