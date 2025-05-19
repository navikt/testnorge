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
	const {
		formState: { errors },
	} = useFormContext()
	const errorContext: ShowErrorContextType = useContext(ShowErrorContext)
	return (
		(!errorContext || errorContext?.showError) && (
			<ErrorMessage
				name={name}
				errors={errors}
				render={({ message }) => <DollyErrorMessage message={message} />}
			/>
		)
	)
}

export const DollyErrorMessage = ({ message }: { message: string }) => (
	<ErrorMessageText>{message}</ErrorMessageText>
)
