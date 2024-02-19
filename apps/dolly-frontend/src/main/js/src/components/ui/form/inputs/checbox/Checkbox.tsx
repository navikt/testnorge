import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Checkbox as NavCheckbox, Switch } from '@navikt/ds-react'

import './Checkbox.less'
import styled from 'styled-components'
import { useFormContext } from 'react-hook-form'

const StyledAttributeCheckbox = styled(NavCheckbox)`
	&&& {
		.skjemaelement__label {
			text-transform: none;
		}
	}
`

const StyledSwitch = styled(Switch)`
	&&& {
		margin-left: 3px;
	}
`
const StyledCheckbox = styled(NavCheckbox)`
	&&& {
		.skjemaelement__label {
			font-size: 0.75em;
		}
	}
`

export const Checkbox = ({ id, attributtCheckbox = false, ...restProps }) =>
	attributtCheckbox ? (
		<StyledAttributeCheckbox id={id || restProps.name} {...restProps} />
	) : (
		<StyledCheckbox id={id || restProps.name} {...restProps} />
	)

export const DollyCheckbox = ({
	isSwitch = false,
	isDisabled = false,
	wrapperSize = 'grow',
	checkboxMargin = false,
	label = '',
	id = null,
	...props
}) => (
	<InputWrapper size={wrapperSize} checkboxMargin={checkboxMargin}>
		{isSwitch ? (
			<StyledSwitch disabled={isDisabled} {...props}>
				{label}
			</StyledSwitch>
		) : (
			<Checkbox id={id || label} disabled={isDisabled} {...props}>
				{label}
			</Checkbox>
		)}
	</InputWrapper>
)

export const FormikCheckbox = ({
	afterChange = null,
	size = 'small',
	checkboxMargin = false,
	...props
}) => {
	const formMethods = useFormContext()
	const value = formMethods.watch(props.name)
	const handleChange = (event: { target: { checked: any } }) => {
		formMethods.setValue(props.name, event.target.checked)
		formMethods.trigger()
		if (afterChange) afterChange(event.target.checked)
	}

	return (
		<DollyCheckbox
			size={size}
			checked={value}
			onChange={handleChange}
			checkboxMargin={checkboxMargin}
			{...props}
		/>
	)
}
