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
	const { watch } = useFormContext()
	const value = watch(name)
	return useControlled ? (
		<DollyTextInput name={name} value={value} {...props} />
	) : (
		<DollyTextInput defaultValue={props.defaultValue || value} name={name} {...props} />
	)
}
