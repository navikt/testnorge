import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { FormikField } from '@/components/ui/form/FormikField'
import { SyntEvent } from '@/components/ui/form/formUtils'
import { Checkbox as NavCheckbox, Switch } from '@navikt/ds-react'

import './Checkbox.less'
import styled from 'styled-components'

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
	...props
}) => (
	<InputWrapper size={wrapperSize} checkboxMargin={checkboxMargin}>
		{isSwitch ? (
			<StyledSwitch disabled={isDisabled} {...props}>
				{label}
			</StyledSwitch>
		) : (
			<Checkbox id={label} disabled={isDisabled} {...props}>
				{label}
			</Checkbox>
		)}
	</InputWrapper>
)

export const FormikCheckbox = ({
	afterChange = null,
	size = 'small',
	fastfield = false,
	checkboxMargin = false,
	...props
}) => (
	<FormikField name={props.name} fastfield={fastfield}>
		{({ field, _form, _meta }) => {
			const handleChange = (event) => {
				field.onChange(SyntEvent(field.name, event.target.checked))
				if (afterChange) afterChange(event.target.checked)
			}

			return (
				<DollyCheckbox
					size={size}
					checked={field.value}
					onChange={handleChange}
					checkboxMargin={checkboxMargin}
					{...props}
				/>
			)
		}}
	</FormikField>
)
