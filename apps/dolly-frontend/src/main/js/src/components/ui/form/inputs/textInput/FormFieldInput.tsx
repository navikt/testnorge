import React from 'react'
import { useFormContext } from 'react-hook-form'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

type FormFieldInputProps = {
	name: string
	useControlled?: boolean
	defaultValue?: any
	[key: string]: any
}

const FormFieldInput = ({
	name,
	useControlled = false,
	defaultValue,
	...props
}: FormFieldInputProps) => {
	const { watch } = useFormContext()
	const formValue = watch(name)

	if (useControlled) {
		return <DollyTextInput name={name} value={formValue} {...props} />
	}

	const initialValue = defaultValue !== undefined ? defaultValue : formValue
	return <DollyTextInput name={name} defaultValue={initialValue} {...props} />
}

export default FormFieldInput
