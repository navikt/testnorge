import React from 'react'
import { useFormContext } from 'react-hook-form'
import { DollyTextInput } from '@/components/ui/form/inputs/textInput/TextInput'

type FormFieldInputProps = {
	name: string
	[key: string]: any
}

const FormFieldInput = ({ name, ...props }: FormFieldInputProps) => {
	const { watch } = useFormContext()
	const formValue = watch(name)
	return <DollyTextInput name={name} value={formValue} {...props} />
}

export default FormFieldInput
