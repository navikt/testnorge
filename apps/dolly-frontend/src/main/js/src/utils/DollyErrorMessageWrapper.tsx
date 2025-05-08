import * as React from 'react'
import { useContext } from 'react'
import { useFormContext } from 'react-hook-form'
import { ErrorMessage } from '@hookform/error-message'
import {
	ShowErrorContext,
	ShowErrorContextType,
} from '@/components/bestillingsveileder/ShowErrorContext'
import Button from '@/components/ui/button/Button'
import styled from 'styled-components'

type CloseableErrorMessageProps = {
	message: string
	onClose?: () => void
}

const ErrorBorderDiv = styled.div`
	border: 4px solid #c30000;
	border-radius: 8px;
	padding: 15px 10px 0 10px;
	margin: 10px;
	position: relative;
`

const ErrorMessageText = styled.p`
	color: #ba3a26;
	font-style: italic;
`

const ErrorCloseButton = styled(Button)`
	position: absolute;
	top: 5px;
	right: 5px;
	border-bottom: unset;
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

export const CloseableErrorMessage = ({ message, onClose }: CloseableErrorMessageProps) => {
	const [show, setShow] = React.useState(true)
	if (!show) {
		return null
	}
	return (
		<ErrorBorderDiv>
			<ErrorMessageText>{message}</ErrorMessageText>
			<ErrorCloseButton
				kind="kryss"
				onClick={() => {
					setShow(false)
					onClose?.()
				}}
			/>
		</ErrorBorderDiv>
	)
}

export const DollyErrorMessage = ({ message }: { message: string }) => (
	<ErrorMessageText>{message}</ErrorMessageText>
)
