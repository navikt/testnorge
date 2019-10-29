import React from 'react'
import { useField } from 'formik'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { fieldError } from '~/components/ui/form/formUtils'
import Icon from '~/components/ui/icon/Icon'

export const TextInput = React.forwardRef(
	({ placeholder = 'Ikke spesifisert', className, icon, ...props }, ref) => {
		return (
			<React.Fragment>
				<input
					ref={ref}
					id={props.name}
					className="skjemaelement__input"
					placeholder={placeholder}
					{...props}
				/>
				{icon && <Icon size={20} kind={icon} />}
			</React.Fragment>
		)
	}
)

export const DollyTextInput = props => (
	<Label name={props.name} label={props.label} feil={props.feil}>
		<TextInput {...props} />
	</Label>
)

export const FormikTextInput = props => {
	const [field, meta] = useField(props)
	return (
		<InputWrapper {...props}>
			<DollyTextInput
				value={field.value}
				onChange={field.onChange}
				onBlur={field.onBlur}
				feil={fieldError(meta)}
				{...props}
			/>
		</InputWrapper>
	)
}
