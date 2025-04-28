import * as React from 'react'
import { useContext } from 'react'
import { useFormContext } from 'react-hook-form'
import { ErrorMessage } from '@hookform/error-message'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'

export const DollyErrorMessage = ({ name }: { name: string }) => {
	const {
		formState: { errors },
	} = useFormContext()
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	return (
		(!errorContext || errorContext?.showError) && (
			<ErrorMessage
				name={name}
				errors={errors}
				render={({ message }) => <p style={{ color: '#ba3a26', fontStyle: 'italic' }}>{message}</p>}
			/>
		)
	)
}
