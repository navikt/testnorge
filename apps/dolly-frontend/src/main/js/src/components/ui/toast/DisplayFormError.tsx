import 'react-toastify/dist/ReactToastify.css'
import { ErrorMessage } from '@hookform/error-message'
import * as React from 'react'
import { useContext } from 'react'
import { useFormContext } from 'react-hook-form'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'

type Props = {
	path: string
	errorMessage?: string
}

export const DisplayFormError = ({ path, errorMessage }: Props) => {
	const formMethods = useFormContext()
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)

	return (
		errorContext.showError &&
		formMethods.formState.errors && (
			<ErrorMessage
				name={path}
				errors={formMethods.formState.errors}
				render={({ message }) => (
					<p style={{ color: '#ba3a26', fontStyle: 'italic' }}>{errorMessage || message}</p>
				)}
			/>
		)
	)
}
