import _get from 'lodash/get'
import { fieldError } from '~/components/ui/form/formUtils'
import React from 'react'
import { DollyTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { useFormikContext } from 'formik'
import { FormikField } from '~/components/ui/form/FormikField'

type Props = {
	name: string
	defaultValue?: any
}

export default ({ name, defaultValue, ...props }: Props) => {
	const { errors, touched, setFieldTouched, setFieldValue, values } = useFormikContext()
	return (
		<FormikField name={name} fastfield={false}>
			{({ field, form, meta }) => {
				const handleBlur = event => {
					if (!_get(touched, field.name)) {
						setFieldTouched(field.name, true)
					}
					if (_get(touched, field.name) !== event.target.value) {
						setFieldValue(name, event.target.value, true)
					}
					if (props.onSubmit) {
						props.onSubmit()
					}
				}

				return (
					<DollyTextInput
						// @ts-ignore
						defaultValue={defaultValue || _get(values, name)}
						onBlur={handleBlur}
						name={name}
						feil={fieldError({
							touched: _get(touched, name),
							error: _get(errors, name)
						})}
						{...props}
					/>
				)
			}}
		</FormikField>
	)
}
