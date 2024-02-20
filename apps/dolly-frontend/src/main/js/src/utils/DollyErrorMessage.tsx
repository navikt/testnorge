import * as React from 'react'
import { useFormContext } from 'react-hook-form'
import { ErrorMessage } from '@hookform/error-message'

export const DollyErrorMessage = ({ name }: { name: string }) => {
	const {
		formState: { errors },
	} = useFormContext()
	return (
		<ErrorMessage
			name={name}
			errors={errors}
			render={({ message }) => <p style={{ color: '#ba3a26', fontStyle: 'italic' }}>{message}</p>}
		/>
	)
}
