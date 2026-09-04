import React, { useEffect, useId, useRef, useState } from 'react'
import { CopyButton, InlineMessage, Loader } from '@navikt/ds-react'

import type { Application } from '@/services/ApplicationService'
import TokenService from '@/services/TokenService'
import './ApplicationTokenCopyButton.less'

type Props = {
	application: Application
	label: string
}

const ApplicationTokenCopyButton = ({ application, label }: Props) => {
	const buttonRef = useRef<HTMLButtonElement>(null)
	const requestInProgress = useRef(false)
	const errorId = useId()
	const [tokenToCopy, setTokenToCopy] = useState<string | null>(null)
	const [loading, setLoading] = useState(false)
	const [error, setError] = useState(false)

	useEffect(() => {
		if (tokenToCopy && !loading) {
			buttonRef.current?.click()
		}
	}, [loading, tokenToCopy])

	const handleClick = (event: React.MouseEvent<HTMLButtonElement>) => {
		if (tokenToCopy) {
			setTokenToCopy(null)
			return
		}

		event.preventDefault()

		if (requestInProgress.current) {
			return
		}

		requestInProgress.current = true
		setLoading(true)
		setError(false)

		void TokenService.fetchToken(application, false)
			.then((response) => {
				if (!response.token) {
					throw new Error('Token response did not contain a token')
				}
				setTokenToCopy(response.token)
			})
			.catch(() => setError(true))
			.finally(() => {
				requestInProgress.current = false
				setLoading(false)
			})
	}

	return (
		<>
			<CopyButton
				aria-busy={loading}
				aria-describedby={error ? errorId : undefined}
				className="application-token-copy__button"
				copyText={tokenToCopy ?? ''}
				disabled={loading}
				ref={buttonRef}
				size="small"
				title={`Hent og kopier token for ${label}`}
				activeText={`Token for ${label} kopiert`}
				icon={loading ? <Loader size="xsmall" title={`Henter token for ${label}`} /> : undefined}
				onClick={handleClick}
			/>
			{error && (
				<InlineMessage
					className="application-token-copy__error"
					id={errorId}
					role="alert"
					size="small"
					status="error"
				>
					Kunne ikke hente token for {label}. Prøv på nytt.
				</InlineMessage>
			)}
		</>
	)
}

export default ApplicationTokenCopyButton
