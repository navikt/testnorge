import React from 'react'
import { Checkbox as NavCheckbox } from 'nav-frontend-skjema'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { FormikField } from '~/components/ui/form/FormikField'
import { SyntEvent } from '~/components/ui/form/formUtils'

import './Checkbox.less'
import styled from 'styled-components'

const StyledAttributeCheckbox = styled(NavCheckbox)`
	&&& {
		.skjemaelement__label {
			text-transform: none;
		}
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

export const Switch = ({ id, ...restProps }) => {
	return <input id={id || restProps.name} {...restProps} className="dolly-switch" type="checkbox" />
}

export const DollyCheckbox = ({ isSwitch = false, size, checkboxMargin, ...props }) => (
	<InputWrapper size={size} checkboxMargin={checkboxMargin}>
		{isSwitch ? <Switch {...props} /> : <Checkbox {...props} />}
	</InputWrapper>
)

export const FormikCheckbox = ({ afterChange = null, fastfield = false, ...props }) => (
	<FormikField name={props.name} fastfield={fastfield}>
		{({ field, form, meta }) => {
			const handleChange = event => {
				field.onChange(SyntEvent(field.name, event.target.checked))
				if (afterChange) afterChange(event.target.checked)
			}

			return <DollyCheckbox checked={field.value} onChange={handleChange} {...props} />
		}}
	</FormikField>
)
