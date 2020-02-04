import React from 'react'
import { Checkbox as NavCheckbox } from 'nav-frontend-skjema'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { FormikField } from '~/components/ui/form/FormikField'
import { SyntEvent } from '~/components/ui/form/formUtils'

import './Checkbox.less'

export const Checkbox = ({ id, ...restProps }) => {
	return <NavCheckbox id={id || restProps.name} className="dolly-checkbox" {...restProps} />
}

export const Switch = ({ id, ...restProps }) => {
	return <input id={id || restProps.name} {...restProps} className="dolly-switch" type="checkbox" />
}

export const DollyCheckbox = ({ isSwitch = false, size, checkboxMargin, ...props }) => (
	<InputWrapper size={size} checkboxMargin={checkboxMargin}>
		{isSwitch ? <Switch {...props} /> : <Checkbox {...props} />}
	</InputWrapper>
)

export const FormikCheckbox = ({ afterChange, fastfield, ...props }) => (
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
