import React from 'react'
import cn from 'classnames'
import { Label } from '~/components/ui/form/inputs/label/Label'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Icon from '~/components/ui/icon/Icon'
import FormikFieldInput from '~/components/ui/form/inputs/textInput/FormikFieldInput'
import styled from 'styled-components'

const StyledIcon = styled(Icon)`
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
			...props
		}: { name?: string; className?: string; icon?: string; placeholder?: string; feil?: any },
		ref
	) => {
		const css = cn('skjemaelement__input', className, {
			'skjemaelement__input--harFeil': props.feil,
		})

		return (
			<>
				{/*@ts-ignore*/}
				<input ref={ref} id={props.name} className={css} placeholder={placeholder} {...props} />
				{icon && <StyledIcon size={20} kind={icon} />}
			</>
		)
	}
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
	onBlur?: Function
	onSubmit?: Function
	onChange?: Function
	onPaste?: Function
	onKeyPress?: Function
	placeholder?: string
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
	useOnChange?: boolean
	useControlled?: boolean
	disabled?: boolean
	onKeyPress?: Function
}) => {
	const component = <FormikFieldInput {...props} />
	// @ts-ignore
	return visHvisAvhuket ? <Vis attributt={props.name}>{component}</Vis> : component
}
