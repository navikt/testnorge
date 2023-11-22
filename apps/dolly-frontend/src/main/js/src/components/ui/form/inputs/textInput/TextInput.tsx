import cn from 'classnames'
import { Label } from '@/components/ui/form/inputs/label/Label'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Icon from '@/components/ui/icon/Icon'
import FormikFieldInput from '@/components/ui/form/inputs/textInput/FormikFieldInput'
import styled from 'styled-components'
import React from 'react'
import { useFormContext } from 'react-hook-form'

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
			className,
			icon,
			isDisabled,
			...props
		}: {
			name?: string
			className?: string
			icon?: string
			placeholder?: string
			feil?: any
			isDisabled?: boolean
		},
		ref,
	) => {
		const { register } = useFormContext()
		const css = cn('skjemaelement__input', className, {
			'skjemaelement__input--harFeil': props.feil,
		})

		return (
			<>
				<input
					ref={ref}
					disabled={isDisabled}
					id={props.name}
					className={css}
					placeholder={placeholder}
					{...props}
					{...register(props.name)}
				/>
				{icon && <StyledIcon fontSize={'1.5rem'} kind={icon} />}
			</>
		)
	},
)

export const DollyTextInput = (props: {
	name: string
	label?: string
	feil?: any
	value?: any
	size?: string
	type?: string
	readOnly?: boolean
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
		<Label name={props.name} label={props.label} feil={props.feil}>
			<TextInput {...props} />
		</Label>
	</InputWrapper>
)

export const FormikTextInput = ({
	visHvisAvhuket = true,
	...props
}: {
	visHvisAvhuket?: boolean
	name: string
	label?: string
	size?: string
	type?: string
	useOnChange?: boolean
	useControlled?: boolean
	defaultValue?: string
	isDisabled?: boolean
	onKeyPress?: Function
	autoFocus?: boolean
	feil?: { feilmelding: string }
}) => {
	return visHvisAvhuket ? (
		<Vis attributt={props.name}>
			<FormikFieldInput {...props} />
		</Vis>
	) : (
		<FormikFieldInput {...props} />
	)
}
