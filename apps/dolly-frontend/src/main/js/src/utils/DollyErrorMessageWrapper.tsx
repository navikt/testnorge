import * as React from 'react'
import { useContext } from 'react'
import { useFormContext } from 'react-hook-form'
import { ErrorMessage } from '@hookform/error-message'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import styled from 'styled-components'

const ErrorMessageText = styled.p`
	color: #ba3a26;
	font-style: italic;
`

export const DollyErrorMessageWrapper = ({ name }: { name: string }) => {
	const methods = useFormContext()
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)

	const errors = methods?.formState?.errors ?? {}
	const isSubmitted = methods?.formState?.isSubmitted || methods?.formState.submitCount > 0

	const fieldState = methods?.getFieldState?.(name, methods.formState)
	const isTouched = fieldState?.isTouched ?? false

	const shouldRender = !errorContext || errorContext?.showError || isSubmitted || isTouched

	return shouldRender ? (
		<ErrorMessage
			name={name}
			errors={errors}
			render={({ message }) => <DollyErrorMessage message={message} />}
		/>
	) : null
}

export const DollyErrorMessage = ({ message }: { message: string }) => (
	<ErrorMessageText>{message}</ErrorMessageText>
)
