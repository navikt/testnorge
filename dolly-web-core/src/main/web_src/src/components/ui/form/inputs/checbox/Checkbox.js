import React from 'react'
import { useField, FastField } from 'formik'
import { Checkbox as NavCheckbox } from 'nav-frontend-skjema'
import { InputWrapper } from '~/components/ui/form/inputWrapper/InputWrapper'
import { fieldError, SyntEvent } from '~/components/ui/form/formUtils'

import './Checkbox.less'

export const Checkbox = ({ id, ...restProps }) => {
	return <NavCheckbox id={id || restProps.name} {...restProps} />
}

export const DollyCheckbox = props => (
	<InputWrapper {...props}>
		<Checkbox {...props} />
	</InputWrapper>
)

export const FormikCheckbox = props => {
	const [field, meta] = useField(props)

	const handleChange = event => field.onChange(SyntEvent(field.name, event.target.checked))

	return <DollyCheckbox checked={field.value} onChange={handleChange} {...props} />
}

// TODO: Formik@2.0.3 har en bug som gjør at FastField ikke funker
// https://github.com/jaredpalmer/formik/issues/1974
// export const FastFormikCheckbox = props => {
// 	return (
// 		<FastField name={props.name}>
// 			{({ field, form, meta }) => {
// 				const handleChange = event => field.onChange(SyntEvent(field.name, event.target.checked))
// 				return (
// 					<DollyCheckbox
// 						onChange={handleChange}
// 						onBlur={field.onBlur}
// 						checked={field.value}
// 						{...props}
// 					/>
// 				)
// 			}}
// 		</FastField>
// 	)
// }
