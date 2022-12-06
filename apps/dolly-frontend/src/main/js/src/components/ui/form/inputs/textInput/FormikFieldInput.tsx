import _get from 'lodash/get'
import { fieldError } from '@/components/ui/form/formUtils'

import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { useFormikContext } from 'formik'
import { FormikField } from '@/components/ui/form/FormikField'

type Props = {
	name: string
	useOnChange?: boolean
	useControlled?: boolean
}

export default ({ name, useOnChange = false, useControlled = false, ...props }: Props) => {
	const { errors, touched, setFieldTouched, setFieldValue, values } = useFormikContext()
	return (
		<FormikField name={name} fastfield={false}>
			{({ field, _form, _meta }) => {
				const handleChanges = (event: { target: { value: any } }) => {
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

				return useControlled ? (
					<DollyTextInput
						name={name}
						value={_get(values, name)}
						onChange={handleChanges}
						feil={fieldError({
							touched: _get(touched, name),
							error: _get(errors, name),
						})}
						{...props}
					/>
				) : (
					<DollyTextInput
						// @ts-ignore
						defaultValue={props.defaultValue || _get(values, name)}
						onBlur={useOnChange ? null : handleChanges}
						onChange={useOnChange ? handleChanges : null}
						name={name}
						feil={fieldError({
							touched: _get(touched, name),
							error: _get(errors, name),
						})}
						{...props}
					/>
				)
			}}
		</FormikField>
	)
}
