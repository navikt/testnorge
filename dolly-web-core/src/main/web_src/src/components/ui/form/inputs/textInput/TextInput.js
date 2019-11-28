import React from 'react'
import cn from 'classnames'
import { useField } from 'formik'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { fieldError } from '~/components/ui/form/formUtils'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Icon from '~/components/ui/icon/Icon'

export const TextInput = React.forwardRef(
	({ placeholder = 'Ikke spesifisert', className, icon, ...props }, ref) => {
		const css = cn('skjemaelement__input', className, {
			'skjemaelement__input--harFeil': props.feil
		})
		return (
			<React.Fragment>
				<input ref={ref} id={props.name} className={css} placeholder={placeholder} {...props} />
				{icon && <Icon size={20} kind={icon} />}
			</React.Fragment>
		)
	}
)

export const DollyTextInput = props => (
	<InputWrapper {...props}>
		<Label name={props.name} label={props.label} feil={props.feil}>
			<TextInput {...props} />
		</Label>
	</InputWrapper>
)

const P_FormikTextInput = props => {
	const [field, meta] = useField(props)
	return (
		<DollyTextInput
			value={field.value}
			onChange={field.onChange}
			onBlur={field.onBlur}
			feil={fieldError(meta)}
			{...props}
		/>
	)
}

export const FormikTextInput = ({ visHvisAvhuket = true, ...props }) => {
	const component = <P_FormikTextInput {...props} />
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
