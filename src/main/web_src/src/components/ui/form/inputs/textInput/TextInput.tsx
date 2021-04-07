import React from 'react'
import cn from 'classnames'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Icon from '~/components/ui/icon/Icon'
import UncontrolledFormikTextInput from '~/components/ui/form/inputs/textInput/UncontrolledFormikTextInput'

export const TextInput = React.forwardRef(
	(
		{
			placeholder = 'Ikke spesifisert',
			className,
			icon,
			...props
		}: { name: string; className?: string; icon?: string; placeholder?: string; feil?: any },
		ref
	) => {
		const css = cn('skjemaelement__input', className, {
			'skjemaelement__input--harFeil': props.feil
		})

		return (
			<>
				{/*@ts-ignore*/}
				<input ref={ref} id={props.name} className={css} placeholder={placeholder} {...props} />
				{icon && <Icon size={20} kind={icon} />}
			</>
		)
	}
)

export const DollyTextInput = (props: {
	name: string
	label?: string
	feil?: any
	onBlur?: Function
	onSubmit?: Function
}) => (
	<InputWrapper {...props}>
		{/*@ts-ignore*/}
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
}) => {
	const component = <UncontrolledFormikTextInput {...props} />
	// @ts-ignore
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
