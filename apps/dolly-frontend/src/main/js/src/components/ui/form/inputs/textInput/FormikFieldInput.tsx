import * as _ from 'lodash'
import { fieldError } from '@/components/ui/form/formUtils'

import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikField } from '@/components/ui/form/FormikField'
import { useFormContext } from 'react-hook-form'

type Props = {
	name: string
	useOnChange?: boolean
	useControlled?: boolean
	onSubmit?: any
}

export default ({ name, useOnChange = false, useControlled = false, ...props }: Props) => {
	const {
		formState: { errors, touchedFields: touched },
		setValue: setFieldValue,
		getValues,
	} = useFormContext()
	return (
		<FormikField name={name} fastfield={false}>
			{useControlled ? (
				<DollyTextInput
					name={name}
					value={_.get(getValues(), name)}
					feil={fieldError({
						touched: _.get(touched, name),
						error: _.get(errors, name),
					})}
					{...props}
				/>
			) : (
				<DollyTextInput
					// @ts-ignore
					defaultValue={props.defaultValue || _.get(getValues(), name)}
					name={name}
					feil={fieldError({
						touched: _.get(touched, name),
						error: _.get(errors, name),
					})}
					{...props}
				/>
			)}
		</FormikField>
	)
}
