import _ from 'lodash'

import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { useFormContext } from 'react-hook-form'

type Props = {
	name: string
	useOnChange?: boolean
	useControlled?: boolean
	onSubmit?: any
	defaultValue?: any
}

export default ({ name, useOnChange = false, useControlled = false, ...props }: Props) => {
	const { getValues } = useFormContext()
	return useControlled ? (
		<DollyTextInput name={name} value={_.get(getValues(), name)} {...props} />
	) : (
		<DollyTextInput
			defaultValue={props.defaultValue || _.get(getValues(), name)}
			name={name}
			{...props}
		/>
	)
}
